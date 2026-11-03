package com.tiaprende.backend.login.ldap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.tiaprende.backend.login.config.AuthProperties;
import com.tiaprende.backend.login.exception.AuthConfigurationException;
import com.tiaprende.backend.login.exception.InvalidCredentialsException;

class ActiveDirectoryClientTests {

    private static final String DOMAIN = "idam.am.gov.br";

    /** Cliente que registra os binds e devolve estas contas na busca, sem falar com um AD real. */
    private static class FakeDirectory extends ActiveDirectoryClient {
        final List<String> binds = new ArrayList<>();
        String filter;
        private final List<SearchResult> accounts;

        FakeDirectory(SearchResult... accounts) {
            super(new AuthProperties(null, null, new AuthProperties.ActiveDirectory("ldaps://ad.test:636", DOMAIN,
                    "DC=idam,DC=am,DC=gov,DC=br", null, List.of("GTI"), false, 0, 0), null));
            this.accounts = List.of(accounts);
        }

        @Override
        DirContext bind(AuthProperties.ActiveDirectory ad, String principal, String credentials) throws NamingException {
            binds.add(principal);
            DirContext context = mock(DirContext.class);
            when(context.search(anyString(), anyString(), any(SearchControls.class))).thenAnswer((call) -> {
                filter = call.getArgument(1);
                return enumeration(accounts);
            });
            return context;
        }
    }

    private static NamingEnumeration<SearchResult> enumeration(List<SearchResult> items) {
        var iterator = items.iterator();
        return new NamingEnumeration<>() {
            @Override public SearchResult next() { return iterator.next(); }
            @Override public boolean hasMore() { return iterator.hasNext(); }
            @Override public void close() { }
            @Override public boolean hasMoreElements() { return iterator.hasNext(); }
            @Override public SearchResult nextElement() { return iterator.next(); }
        };
    }

    private static SearchResult account(String login, String guidHex) {
        BasicAttributes attributes = new BasicAttributes(true);
        attributes.put("sAMAccountName", login);
        attributes.put("objectGUID", java.util.HexFormat.of().parseHex(guidHex));
        attributes.put("memberOf", "CN=GTI,OU=GRUPOS,DC=idam,DC=am,DC=gov,DC=br");
        return new SearchResult(login, null, attributes);
    }

    private static final String GUID_A = "33221100554477668899aabbccddeeff";
    private static final String GUID_B = "ffeeddcc554477668899aabb33221100";

    @ParameterizedTest
    @ValueSource(strings = {
        "fulano@outro.dominio",
        "fulano@idam.am.gov.br",
        "OUTRO\\fulano",
        "CN=fulano,DC=idam,DC=am,DC=gov,DC=br",
        "ful*no",
        "fulano\nadmin",
    })
    void rejectsAnythingButAPlainUsernameWithoutContactingTheDirectory(String typed) {
        var directory = new FakeDirectory(account("fulano", GUID_A));
        assertThrows(InvalidCredentialsException.class, () -> directory.authenticate(typed, "senha"));
        assertTrue(directory.binds.isEmpty(), "nao deve gastar tentativa no AD");
    }

    @Test
    void bindsWithTheConfiguredDomainAndSearchesByBothNames() {
        var directory = new FakeDirectory(account("fulano", GUID_A));
        var user = directory.authenticate("  fulano ", "senha");
        assertEquals(List.of("fulano@idam.am.gov.br"), directory.binds);
        assertEquals("(&(objectCategory=person)(objectClass=user)"
                + "(|(sAMAccountName=fulano)(userPrincipalName=fulano@idam.am.gov.br)))", directory.filter);
        assertEquals("fulano", user.login());
    }

    @Test
    void usesTheAccountFoundByUpnWhenTheLoginNameDiffers() {
        // O UPN joao.silva@... pertence a conta jsilva e ninguem tem o sAMAccountName joao.silva.
        var user = new FakeDirectory(account("jsilva", GUID_A)).authenticate("joao.silva", "senha");
        assertEquals("jsilva", user.login());
    }

    @Test
    void refusesLoginWhenUpnAndAccountNameBelongToDifferentPeople() {
        // Uma conta tem o UPN joao@... e outra tem o sAMAccountName joao: nao ha como saber quem autenticou.
        var directory = new FakeDirectory(account("jsilva", GUID_A), account("joao", GUID_B));
        assertThrows(AuthConfigurationException.class, () -> directory.authenticate("joao", "senha"));
    }

    @Test
    void escapesLdapCharactersInTheFilter() {
        var directory = new FakeDirectory(account("a(b", GUID_A));
        directory.authenticate("a(b", "senha");
        assertTrue(directory.filter.contains("(sAMAccountName=a\\28b)"), directory.filter);
    }

    @Test
    void matchesConfiguredGroupNamesCaseInsensitively() {
        assertTrue(ActiveDirectoryClient.matchesAllowedGroup(
                "gti_estagiario",
                "CN=GTI_ESTAGIARIO,OU=GRUPOS,DC=idam,DC=am,DC=gov,DC=br"));
    }

    @Test
    void matchesAFullDistinguishedName() {
        assertTrue(ActiveDirectoryClient.matchesAllowedGroup(
                "CN=GTI,OU=GRUPOS,OU=GTI,OU=IDAM,DC=idam,DC=am,DC=gov,DC=br",
                "cn=gti,ou=grupos,ou=gti,ou=idam,dc=idam,dc=am,dc=gov,dc=br"));
    }

    @Test
    void convertsTheBinaryObjectGuidToUuid() {
        // O AD grava os tres primeiros blocos do GUID em little-endian.
        byte[] bytes = java.util.HexFormat.of().parseHex("33221100554477668899aabbccddeeff");
        assertEquals("00112233-4455-6677-8899-aabbccddeeff", ActiveDirectoryClient.objectGuidToUuid(bytes));
    }

    @Test
    void rejectsPartialGroupNames() {
        assertFalse(ActiveDirectoryClient.matchesAllowedGroup(
                "GTI",
                "CN=GTI_ESTAGIARIO,OU=GRUPOS,DC=idam,DC=am,DC=gov,DC=br"));
    }
}
