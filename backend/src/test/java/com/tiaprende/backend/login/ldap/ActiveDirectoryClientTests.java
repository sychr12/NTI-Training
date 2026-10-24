package com.tiaprende.backend.login.ldap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ActiveDirectoryClientTests {

    @Test
    void buildsAUserPrincipalNameFromTheDomain() {
        assertTrue(ActiveDirectoryClient.userPrincipal("idam.am.gov.br", "beatriz.batista")
                .equals("beatriz.batista@idam.am.gov.br"));
        assertTrue(ActiveDirectoryClient.userPrincipal("idam.am.gov.br", "usuario@outro.dominio")
                .equals("usuario@outro.dominio"));
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
