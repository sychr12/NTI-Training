package com.tiaprende.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.tiaprende.backend.login.ldap.ActiveDirectoryClient;
import com.tiaprende.backend.login.exception.InvalidCredentialsException;
import com.tiaprende.backend.login.exception.ActiveDirectoryUnavailableException;
import com.tiaprende.backend.login.session.AuthSession;
import com.tiaprende.backend.login.session.AuthSession.SessionUser;

@SpringBootTest
@Transactional
class ApiIntegrationTests {
    @Autowired WebApplicationContext context;
    @Autowired JdbcClient jdbc;
    @MockitoBean ActiveDirectoryClient directory;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        jdbc.sql("INSERT INTO usuarios (id, ad_object_guid, login, nome, perfil) VALUES (9001, 'test-guid', 'aluno', 'Aluno', 'ALUNO')").update();
        jdbc.sql("INSERT INTO tutoriais (id, titulo, conteudo) VALUES (9001, 'Tutorial', 'Conteudo'), (9002, 'Outro', 'Conteudo')").update();
    }

    MockHttpSession session(String role) {
        jdbc.sql("UPDATE usuarios SET perfil = :role WHERE id = 9001").param("role", role).update();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthSession.USER, new SessionUser(9001L, "aluno", "Aluno", null, role));
        return session;
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/home", "/api/perfil", "/api/progresso", "/api/progresso/trilhas", "/api/tutorials", "/api/tutoriais", "/api/trilhas", "/api/dicas", "/api/glossario", "/api/exercicios?tutorialId=9001"})
    void readsRequireSessionAndWorkWithSession(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isUnauthorized());
        mvc.perform(get(path).session(session("ALUNO"))).andExpect(status().isOk());
    }

    @Test
    void expiredSessionAndInvalidLoginReturn401() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        when(directory.authenticate("bad", "bad")).thenThrow(new InvalidCredentialsException());
        mvc.perform(post("/api/auth/login").contentType("application/json")
                .content("{\"usuario\":\"bad\",\"senha\":\"bad\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unavailableDirectoryReturns503() throws Exception {
        when(directory.authenticate("user", "pass")).thenThrow(new ActiveDirectoryUnavailableException(new RuntimeException()));
        mvc.perform(post("/api/auth/login").contentType("application/json")
                .content("{\"usuario\":\"user\",\"senha\":\"pass\"}"))
                .andExpect(status().isServiceUnavailable());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/tutorials", "/api/tutoriais", "/api/trilhas", "/api/dicas", "/api/glossario", "/api/exercicios"})
    void studentCannotManageContent(String path) throws Exception {
        mvc.perform(post(path).session(session("ALUNO")).contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userAdministrationRequiresAdminAndRefreshesRole() throws Exception {
        var session = session("ADMIN");
        mvc.perform(get("/api/users").session(session)).andExpect(status().isOk());
        jdbc.sql("UPDATE usuarios SET perfil = 'ALUNO' WHERE id = 9001").update();
        mvc.perform(patch("/api/users/9001").session(session).contentType("application/json")
                .content("{\"perfil\":\"ADMIN\"}")).andExpect(status().isForbidden());
    }

    @Test
    void inactiveUsersLoseAccessImmediately() throws Exception {
        var session = session("ADMIN");
        jdbc.sql("UPDATE usuarios SET ativo = FALSE WHERE id = 9001").update();
        mvc.perform(get("/api/home").session(session)).andExpect(status().isUnauthorized());
    }

    @Test
    void malformedRequestsReturn400() throws Exception {
        mvc.perform(post("/api/auth/login").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/tutorials/abc").session(session("ALUNO"))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/exercicios").session(session("ALUNO"))).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/tutorials/9999", "/api/trilhas/9999", "/api/dicas/9999", "/api/glossario/9999", "/api/exercicios/9999"})
    void missingResourcesReturn404(String path) throws Exception {
        mvc.perform(get(path).session(session("ALUNO"))).andExpect(status().isNotFound());
    }

    @Test
    void homeReflectsProgressAndHidesInactiveTutorials() throws Exception {
        jdbc.sql("INSERT INTO progresso_tutoriais (usuario_id, tutorial_id, status) VALUES (9001, 9001, 'EM_ANDAMENTO'), (9001, 9002, 'CONCLUIDO')").update();
        var session = session("ALUNO");
        mvc.perform(get("/api/home").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estatisticas.cursosEmAndamento").value(1))
                .andExpect(jsonPath("$.estatisticas.cursosConcluido").value(1))
                .andExpect(jsonPath("$.estatisticas.progressoGeral").value(50))
                .andExpect(jsonPath("$.continuarAprendendo[0].id").value(9001));
        jdbc.sql("UPDATE tutoriais SET ativo = FALSE WHERE id = 9001").update();
        mvc.perform(get("/api/tutorials/9001").session(session)).andExpect(status().isNotFound());
        mvc.perform(get("/api/home").session(session)).andExpect(jsonPath("$.estatisticas.cursosEmAndamento").value(0))
                .andExpect(jsonPath("$.continuarAprendendo").isEmpty());
    }

    @Test
    void emptyTracksRemainVisibleAndInactiveTutorialsDoNotCount() throws Exception {
        jdbc.sql("INSERT INTO trilhas (id, titulo, nivel) VALUES (9001, 'Trilha', 'Basico'), (9002, 'Vazia', 'Basico')").update();
        jdbc.sql("INSERT INTO trilha_tutoriais (trilha_id, tutorial_id, ordem) VALUES (9001, 9001, 1), (9001, 9002, 2)").update();
        jdbc.sql("UPDATE tutoriais SET ativo = FALSE WHERE id = 9002").update();
        jdbc.sql("INSERT INTO progresso_tutoriais (usuario_id, tutorial_id, status) VALUES (9001, 9001, 'CONCLUIDO')").update();
        mvc.perform(get("/api/progresso/trilhas").session(session("ALUNO"))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percentual").value(100))
                .andExpect(jsonPath("$[1].totalTutoriais").value(0));
        mvc.perform(get("/api/perfil").session(session("ALUNO"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.estatistica.trilhasConcluidas").value(1));
    }

    @Test
    void logoutInvalidatesSession() throws Exception {
        var session = session("ALUNO");
        mvc.perform(post("/api/auth/logout").session(session)).andExpect(status().isNoContent());
        org.junit.jupiter.api.Assertions.assertTrue(session.isInvalid());
    }

    @Test
    void corsAllowsConfiguredFrontendAndRejectsUnknownOrigins() throws Exception {
        mvc.perform(options("/api/home").header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        mvc.perform(options("/api/home").header("Origin", "https://example.invalid")
                .header("Access-Control-Request-Method", "GET")).andExpect(status().isForbidden());
    }
    @Test
    void invalidReferencesAndDuplicateTutorialsAreRejected() throws Exception {
        var session = session("PROFESSOR");
        mvc.perform(put("/api/progresso").session(session).contentType("application/json")
                .content("{\"tutorialId\":9999,\"status\":\"CONCLUIDO\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/progresso").session(session).contentType("application/json")
                .content("{\"tutorialId\":9001,\"status\":\"INVALIDO\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/trilhas").session(session).contentType("application/json")
                .content("{\"titulo\":\"Trilha\",\"nivel\":\"Basico\",\"tutorialIds\":[9001,9001]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/trilhas").session(session).contentType("application/json")
                .content("{\"titulo\":\"Trilha\",\"nivel\":\"Basico\",\"tutorialIds\":[9999]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unknownRoutesAndUnsupportedMethodsKeepHttpStatus() throws Exception {
        var session = session("ALUNO");
        mvc.perform(get("/api/unknown").session(session)).andExpect(status().isNotFound());
        mvc.perform(post("/api/home").session(session)).andExpect(status().isMethodNotAllowed());
    }

}
