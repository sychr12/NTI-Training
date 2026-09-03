package com.tiaprende.backend.auth;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.tiaprende.backend.auth.AuthenticationExceptions.ActiveDirectoryUnavailableException;
import com.tiaprende.backend.auth.AuthenticationExceptions.AuthConfigurationException;
import com.tiaprende.backend.auth.AuthenticationExceptions.InvalidCredentialsException;
import com.tiaprende.backend.auth.AuthenticationExceptions.UnauthorizedGroupException;

@Component
public class ActiveDirectoryClient {

	private static final String[] RETURNING_ATTRIBUTES = {
			"distinguishedName", "sAMAccountName", "displayName", "mail", "objectGUID", "memberOf"
	};

	private final AuthProperties properties;

	public ActiveDirectoryClient(AuthProperties properties) {
		this.properties = properties;
	}

	public AdUser authenticate(String username, String password) {
		if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
			throw new InvalidCredentialsException();
		}

		AuthProperties.ActiveDirectory ad = requireAdConfig();
		try {
			SearchResult userResult = findUser(ad, username);
			String userDn = attributeAsString(userResult.getAttributes(), "distinguishedName");

			if (!StringUtils.hasText(userDn)) {
				userDn = userResult.getNameInNamespace();
			}

			bind(ad.url(), userDn, password);

			AdUser user = mapUser(userResult.getAttributes(), username);
			ensureAuthorized(ad.requiredGroupDn(), user.memberOf());
			return user;
		}
		catch (AuthenticationException ex) {
			throw new InvalidCredentialsException();
		}
		catch (NamingException ex) {
			throw new ActiveDirectoryUnavailableException(ex);
		}
	}

	private SearchResult findUser(AuthProperties.ActiveDirectory ad, String username) throws NamingException {
		DirContext context = bind(ad.url(), ad.serviceUserDn(), ad.servicePassword());
		try {
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(RETURNING_ATTRIBUTES);

			String filter = ad.userSearchFilter().replace("{0}", escapeLdapFilter(username));
			NamingEnumeration<SearchResult> results = context.search(ad.baseDn(), filter, controls);
			if (!results.hasMore()) {
				throw new InvalidCredentialsException();
			}
			return results.next();
		}
		finally {
			context.close();
		}
	}

	private DirContext bind(String url, String principal, String credentials) throws NamingException {
		Hashtable<String, String> environment = new Hashtable<>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, url);
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, principal);
		environment.put(Context.SECURITY_CREDENTIALS, credentials);
		return new InitialDirContext(environment);
	}

	private AdUser mapUser(Attributes attributes, String fallbackLogin) throws NamingException {
		String login = valueOrFallback(attributeAsString(attributes, "sAMAccountName"), fallbackLogin);
		String displayName = attributeAsString(attributes, "displayName");
		String email = attributeAsString(attributes, "mail");
		String objectGuid = objectGuidAsString(attributes.get("objectGUID"));
		List<String> groups = attributeAsList(attributes.get("memberOf"));

		if (!StringUtils.hasText(objectGuid)) {
			throw new AuthConfigurationException("O atributo objectGUID nao foi retornado pelo Active Directory.");
		}

		return new AdUser(objectGuid, login, displayName, email, groups);
	}

	private void ensureAuthorized(String requiredGroupDn, List<String> userGroups) {
		if (!StringUtils.hasText(requiredGroupDn)) {
			throw new AuthConfigurationException("Configure NTI_AD_REQUIRED_GROUP_DN para restringir o acesso.");
		}

		boolean belongsToRequiredGroup = userGroups.stream()
				.anyMatch(group -> group.equalsIgnoreCase(requiredGroupDn));

		if (!belongsToRequiredGroup) {
			throw new UnauthorizedGroupException();
		}
	}

	private AuthProperties.ActiveDirectory requireAdConfig() {
		AuthProperties.ActiveDirectory ad = properties.ad();
		if (ad == null
				|| !StringUtils.hasText(ad.url())
				|| !StringUtils.hasText(ad.baseDn())
				|| !StringUtils.hasText(ad.serviceUserDn())
				|| !StringUtils.hasText(ad.servicePassword())
				|| !StringUtils.hasText(ad.userSearchFilter())) {
			throw new AuthConfigurationException("Configuracao do Active Directory incompleta.");
		}
		return ad;
	}

	private String attributeAsString(Attributes attributes, String name) throws NamingException {
		return attributeAsString(attributes.get(name));
	}

	private String attributeAsString(Attribute attribute) throws NamingException {
		if (attribute == null) {
			return null;
		}
		Object value = attribute.get();
		return value == null ? null : value.toString();
	}

	private List<String> attributeAsList(Attribute attribute) throws NamingException {
		List<String> values = new ArrayList<>();
		if (attribute == null) {
			return values;
		}
		NamingEnumeration<?> all = attribute.getAll();
		while (all.hasMore()) {
			Object value = all.next();
			if (value != null) {
				values.add(value.toString());
			}
		}
		return values;
	}

	private String objectGuidAsString(Attribute attribute) throws NamingException {
		if (attribute == null) {
			return null;
		}
		Object value = attribute.get();
		if (value instanceof byte[] bytes) {
			return objectGuidToUuid(bytes);
		}
		return value == null ? null : value.toString();
	}

	private String objectGuidToUuid(byte[] bytes) {
		if (bytes.length != 16) {
			throw new AuthConfigurationException("objectGUID retornado pelo AD tem formato invalido.");
		}

		long mostSignificantBits = ((long) bytes[3] & 0xff) << 56
				| ((long) bytes[2] & 0xff) << 48
				| ((long) bytes[1] & 0xff) << 40
				| ((long) bytes[0] & 0xff) << 32
				| ((long) bytes[5] & 0xff) << 24
				| ((long) bytes[4] & 0xff) << 16
				| ((long) bytes[7] & 0xff) << 8
				| ((long) bytes[6] & 0xff);

		long leastSignificantBits = 0;
		for (int index = 8; index < 16; index++) {
			leastSignificantBits = (leastSignificantBits << 8) | ((long) bytes[index] & 0xff);
		}

		return new UUID(mostSignificantBits, leastSignificantBits).toString();
	}

	private String valueOrFallback(String value, String fallback) {
		return StringUtils.hasText(value) ? value : fallback;
	}

	private String escapeLdapFilter(String value) {
		return value
				.replace("\\", "\\5c")
				.replace("*", "\\2a")
				.replace("(", "\\28")
				.replace(")", "\\29")
				.replace("\u0000", "\\00");
	}
}
