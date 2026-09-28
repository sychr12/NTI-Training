package com.tiaprende.backend.login.config;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import com.tiaprende.backend.login.session.AuthSession;
import com.tiaprende.backend.login.session.AuthSession.SessionUser;
import com.tiaprende.backend.user.repository.UserRepository;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class SecurityConfig {
    private static final String[] CONTENT_PATHS = {
        "/api/tutorials/**", "/api/tutoriais/**", "/api/trilhas/**", "/api/dicas/**", "/api/glossario/**", "/api/exercicios/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository users) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .requestCache(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(new SessionUserAuthenticationFilter(users), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) ->
                                writeError(response, 401, "Sessao expirada ou inexistente."))
                        .accessDeniedHandler((request, response, exception) ->
                                writeError(response, 403, "Acesso negado.")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").permitAll()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/exercicios/*/responder").authenticated()
                        .requestMatchers(HttpMethod.POST, CONTENT_PATHS).hasAnyRole("PROFESSOR", "SUPERVISOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, CONTENT_PATHS).hasAnyRole("PROFESSOR", "SUPERVISOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, CONTENT_PATHS).hasAnyRole("PROFESSOR", "SUPERVISOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, CONTENT_PATHS).hasAnyRole("PROFESSOR", "SUPERVISOR", "ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll());
        return http.build();
    }

    private static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"mensagem\":\"" + message + "\"}");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(AuthProperties properties) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(properties.cors().allowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> { throw new UsernameNotFoundException(username); };
    }

    // Created only inside the security chain, avoiding servlet filter double registration.
    static class SessionUserAuthenticationFilter extends OncePerRequestFilter {
        private final UserRepository users;

        SessionUserAuthenticationFilter(UserRepository users) {
            this.users = users;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws ServletException, IOException {
            try {
                var session = request.getSession(false);
                if (session != null && session.getAttribute(AuthSession.USER) instanceof SessionUser previous) {
                    var current = users.findById(previous.id());
                    if (current.isEmpty() || !current.get().ativo()) {
                        session.invalidate();
                        SecurityContextHolder.clearContext();
                    } else {
                        var user = current.get();
                        var principal = new SessionUser(user.id(), user.login(), user.nome(), user.email(), user.perfil());
                        session.setAttribute(AuthSession.USER, principal);
                        var context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.perfil()))));
                        SecurityContextHolder.setContext(context);
                    }
                }
                chain.doFilter(request, response);
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }
}
