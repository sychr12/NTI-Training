package com.tiaprende.backend.login.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nti.auth")
public record AuthProperties(
    Cors cors,
    Session session,
    ActiveDirectory ad,
    Login login
) {

    public AuthProperties{
        if (cors == null){
            cors = new Cors(
                List.of("http://localhost:3000")
            );
        }
    if (session == null){
        session = new Session(
            28800,
            2592000
        );
    }
    if (ad == null){
        ad = new ActiveDirectory(
            "",
            "",
            "",
            "(&(objectCategory=person)(objectClass=user)(sAMAccountName={0}))",
            List.of(),
            false,
            0,
            0
        );
    }
    if (login == null){
        login = new Login(0, 0);
    }
    }
    public record Cors (List<String> allowedOrigins){
        public Cors{
            if (allowedOrigins == null || allowedOrigins.isEmpty()){
                allowedOrigins = List.of("http://localhost:3000");
            }
        }
    }

    public record Session(
        int defaultTimeoutSeconds,
        int rememberTimeoutSeconds
    ) {

    public Session{
        if (defaultTimeoutSeconds <= 0){
            defaultTimeoutSeconds = 28800;
        }
        if (rememberTimeoutSeconds <= 0){
            rememberTimeoutSeconds = 2592000;
        }
    }
    }

    public record ActiveDirectory(
        String url,
        String domain,
        String baseDn,
        String userSearchFilter,
        List<String> allowedGroups,
        boolean allowInsecureLdap,
        int connectTimeoutMillis,
        int readTimeoutMillis
    ) {
        public ActiveDirectory{
            url = url == null ? "" : url;
            domain = domain == null ? "" : domain;
            baseDn = baseDn == null ? "" : baseDn;

            userSearchFilter = userSearchFilter == null || userSearchFilter.isBlank()
                    ? "(&(objectCategory=person)(objectClass=user)(sAMAccountName={0}))"
                    : userSearchFilter;
            allowedGroups = allowedGroups == null
                    ? List.of()
                    : allowedGroups.stream()
                            .filter(group -> group != null && !group.isBlank())
                            .map(String::trim)
                            .toList();
            if (connectTimeoutMillis <= 0){
                connectTimeoutMillis = 5000;
            }
            if (readTimeoutMillis <= 0){
                readTimeoutMillis = 5000;
            }
        }
    }

    /**
     * Limite de tentativas de login com senha errada, para evitar que alguém bloqueie contas no AD.
     */
    public record Login(
        int maxAttempts,
        int blockMinutes
    ) {
        public Login{
            if (maxAttempts <= 0){
                maxAttempts = 5;
            }
            if (blockMinutes <= 0){
                blockMinutes = 15;
            }
        }
    }
}
