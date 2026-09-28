package com.tiaprende.backend.login.ldap;

import com.tiaprende.backend.login.config.AuthProperties;
import com.tiaprende.backend.login.dto.AdUser;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.tiaprende.backend.login.exception.ActiveDirectoryUnavailableException;
import com.tiaprende.backend.login.exception.AuthConfigurationException;
import com.tiaprende.backend.login.exception.InvalidCredentialsException;
import com.tiaprende.backend.login.exception.UnauthorizedGroupException;

@Component
public class ActiveDirectoryClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryClient.class);

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
		DirContext context = null;
		try {
			context = bind(ad.url(), userPrincipal(ad.domain(), username), password);
			SearchResult userResult = findUser(ad, context, username);
			AdUser user = mapUser(userResult.getAttributes(), username);
			ensureAuthorized(ad.allowedGroups(), user.memberOf());
			return user;
		}
		catch (AuthenticationException ex) {
			throw new InvalidCredentialsException();
		}
		catch (NamingException ex) {
			LOGGER.warn("Falha LDAP ao autenticar ou consultar o Active Directory: {}: {}",
					ex.getClass().getSimpleName(), ex.getMessage());
			throw new ActiveDirectoryUnavailableException(ex);
		}
		finally {
			if (context != null) {
				try {
					context.close();
				}
				catch (NamingException ignored) {
					// The authentication result has already been determined.
				}
			}
		}
	}

	private SearchResult findUser(AuthProperties.ActiveDirectory ad, DirContext context, String username)
			throws NamingException {
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(RETURNING_ATTRIBUTES);

		String accountName = username.contains("@") ? username.substring(0, username.indexOf('@')) : username;
		String filter = ad.userSearchFilter().replace("{0}", escapeLdapFilter(accountName));
		NamingEnumeration<SearchResult> results = context.search(ad.baseDn(), filter, controls);
		SearchResult result = null;
		try {
			try {
				if (results.hasMore()) {
					result = results.next();
				}
				if (result != null && results.hasMore()) {
					throw new AuthConfigurationException("A busca no AD retornou mais de um usuario.");
				}
			}
			catch (PartialResultException ex) {
				if (result == null) {
					throw ex;
				}
				LOGGER.debug("Referral parcial ignorado apos localizar o usuario no AD.");
			}
			if (result == null) {
				throw new InvalidCredentialsException();
			}
			return result;
		}
		finally {
			results.close();
		}
	}

	static String userPrincipal(String domain, String username) {
		String normalized = username.trim();
		return normalized.contains("@") || normalized.indexOf(92) >= 0
				? normalized
				: normalized + "@" + domain;
	}

	private DirContext bind(String url, String principal, String credentials) throws NamingException {
		Hashtable<String, String> environment = new Hashtable<>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, url);
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.REFERRAL, "ignore");
		environment.put("com.sun.jndi.ldap.connect.timeout", "5000");
		environment.put("com.sun.jndi.ldap.read.timeout", "5000");
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

	private void ensureAuthorized(List<String> allowedGroups, List<String> userGroups) {
		if (allowedGroups == null || allowedGroups.isEmpty()) {
			throw new AuthConfigurationException("Configure NTI_AD_ALLOWED_GROUPS para restringir o acesso.");
		}

		boolean belongsToRequiredGroup = userGroups.stream()
				.anyMatch(group -> allowedGroups.stream()
						.anyMatch(allowedGroup -> matchesAllowedGroup(allowedGroup, group)));

		if (!belongsToRequiredGroup) {
			throw new UnauthorizedGroupException();
		}
	}

	static boolean matchesAllowedGroup(String allowedGroup, String userGroupDn) {
		if (!StringUtils.hasText(allowedGroup) || !StringUtils.hasText(userGroupDn)) {
			return false;
		}
		String normalized = allowedGroup.trim();
		if (normalized.regionMatches(true, 0, "CN=", 0, 3)) {
			return userGroupDn.equalsIgnoreCase(normalized);
		}
		String expectedPrefix = "CN=" + normalized + ",";
		return userGroupDn.regionMatches(true, 0, expectedPrefix, 0, expectedPrefix.length());
	}

	private AuthProperties.ActiveDirectory requireAdConfig() {
		AuthProperties.ActiveDirectory ad = properties.ad();
		if (ad == null
				|| !StringUtils.hasText(ad.url())
				|| !StringUtils.hasText(ad.domain())
				|| !StringUtils.hasText(ad.baseDn())
				|| !StringUtils.hasText(ad.userSearchFilter())
				|| ad.allowedGroups() == null
				|| ad.allowedGroups().isEmpty()) {
			throw new AuthConfigurationException("Configuracao do Active Directory incompleta.");
		}
		if (ad.url().toLowerCase(java.util.Locale.ROOT).startsWith("ldap://") && !ad.allowInsecureLdap()) {
			throw new AuthConfigurationException(
					"LDAP sem criptografia bloqueado. Configure NTI_AD_ALLOW_INSECURE_LDAP=true apenas temporariamente.");
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
        try {
            while (all.hasMore()) {
                Object value = all.next();
                if (value != null) {
                    values.add(value.toString());
                }
            }
        } finally {
            all.close();
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
