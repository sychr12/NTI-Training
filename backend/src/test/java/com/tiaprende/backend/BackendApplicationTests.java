package com.tiaprende.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tiaprende.backend.login.config.AuthProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = {
		"nti.auth.ad.allowed-groups=GTI,GTI_ESTAGIARIO",
		"nti.auth.ad.allow-insecure-ldap=false"
})
class BackendApplicationTests {
	@Autowired
	AuthProperties authProperties;

	@Test
	void contextLoads() {
		assertEquals(java.util.List.of("GTI", "GTI_ESTAGIARIO"), authProperties.ad().allowedGroups());
		assertFalse(authProperties.ad().allowInsecureLdap());
	}

}
