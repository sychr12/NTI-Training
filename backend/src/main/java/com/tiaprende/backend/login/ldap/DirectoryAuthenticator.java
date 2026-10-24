package com.tiaprende.backend.login.ldap;

import com.tiaprende.backend.login.dto.AdUser;

/**
 * Valida usuário e senha no diretório. Em produção é o Active Directory;
 * no perfil "dev" é um usuário local (DevAuthenticator).
 */
public interface DirectoryAuthenticator {

	AdUser authenticate(String username, String password);
}
