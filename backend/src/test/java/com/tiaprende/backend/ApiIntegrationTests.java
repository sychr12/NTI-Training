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
    void favoritesArePersistedPerUser() throws Exception {
        var session = session("ALUNO");
        mvc.perform(get("/api/favoritos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/favoritos").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(put("/api/favoritos/9001").session(session)).andExpect(status().isNoContent());
        mvc.perform(put("/api/favoritos/9001").session(session)).andExpect(status().isNoContent());
        mvc.perform(get("/api/favoritos").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tutorialId").value(9001));
        mvc.perform(put("/api/favoritos/9999").session(session)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/favoritos/9001").session(session)).andExpect(status().isNoContent());
        mvc.perform(get("/api/favoritos").session(session)).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void preferencesDefaultToOnAndKeepUnsentFields() throws Exception {
        var session = session("ALUNO");
        mvc.perform(get("/api/perfil/preferencias").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.lembretesEstudo").value(true))
                .andExpect(jsonPath("$.notificacoesEmail").value(true));
        mvc.perform(put("/api/perfil/preferencias").session(session).contentType("application/json")
                .content("{\"lembretesEstudo\":false}")).andExpect(status().isOk());
        mvc.perform(put("/api/perfil/preferencias").session(session).contentType("application/json")
                .content("{\"notificacoesEmail\":false}")).andExpect(status().isOk());
        mvc.perform(get("/api/perfil/preferencias").session(session))
                .andExpect(jsonPath("$.lembretesEstudo").value(false))
                .andExpect(jsonPath("$.notificacoesEmail").value(false));
    }

    @Test
    void achievementsComputeXpStreakAndNewMedals() throws Exception {
        jdbc.sql("INSERT INTO progresso_tutoriais (usuario_id, tutorial_id, status, percentual) VALUES (9001, 9001, 'CONCLUIDO', 100)").update();
        jdbc.sql("INSERT INTO exercicios (id, tutorial_id, pergunta, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta) VALUES (9001, 9001, 'P', 'A', 'B', 'C', 'D', 'A')").update();
        jdbc.sql("INSERT INTO respostas_exercicios (usuario_id, exercicio_id, resposta, correto) VALUES (9001, 9001, 'A', TRUE)").update();
        // Datas relativas a data do banco, para funcionar no H2 e no PostgreSQL.
        var hoje = jdbc.sql("SELECT CURRENT_DATE").query(java.time.LocalDate.class).single();
        jdbc.sql("INSERT INTO dias_estudo (usuario_id, dia) VALUES (9001, :ontem), (9001, :anteontem)")
                .param("ontem", hoje.minusDays(1)).param("anteontem", hoje.minusDays(2)).update();
        var session = session("ALUNO");

        // 1 tutorial (50 XP) + 1 acerto (10 XP); estudou hoje, ontem e anteontem
        mvc.perform(get("/api/conquistas").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.xp").value(60))
                .andExpect(jsonPath("$.nivel.nome").value("Iniciante"))
                .andExpect(jsonPath("$.nivel.xpProximo").value(100))
                .andExpect(jsonPath("$.sequencia.atual").value(3))
                .andExpect(jsonPath("$.sequencia.estudouHoje").value(true))
                .andExpect(jsonPath("$.medalhas[?(@.codigo=='primeiro-passo')].conquistada").value(true))
                .andExpect(jsonPath("$.medalhas[?(@.codigo=='primeiro-passo')].nova").value(true))
                .andExpect(jsonPath("$.medalhas[?(@.codigo=='constante')].conquistada").value(true))
                .andExpect(jsonPath("$.medalhas[?(@.codigo=='dedicado')].conquistada").value(false))
                .andExpect(jsonPath("$.medalhas[?(@.codigo=='dedicado')].progresso").value(1));

        mvc.perform(post("/api/conquistas/vistas").session(session)).andExpect(status().isNoContent());
        mvc.perform(get("/api/conquistas").session(session))
                .andExpect(jsonPath("$.medalhas[?(@.codigo=='primeiro-passo')].nova").value(false));
    }

    @Test
    void completingRequiresAnsweringEveryExercise() throws Exception {
        jdbc.sql("INSERT INTO exercicios (id, tutorial_id, pergunta, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta, explicacao) VALUES (9003, 9001, 'P', 'A', 'B', 'C', 'D', 'B', 'Porque B')").update();
        var session = session("ALUNO");
        mvc.perform(put("/api/progresso").session(session).contentType("application/json")
                .content("{\"tutorialId\":9001,\"status\":\"CONCLUIDO\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Responda o exercicio deste tutorial antes de concluir."));
        mvc.perform(get("/api/exercicios/minhas-respostas?tutorialId=9001").session(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

        jdbc.sql("INSERT INTO respostas_exercicios (usuario_id, exercicio_id, resposta, correto) VALUES (9001, 9003, 'A', FALSE)").update();
        mvc.perform(get("/api/exercicios/minhas-respostas?tutorialId=9001").session(session))
                .andExpect(jsonPath("$[0].exercicioId").value(9003))
                .andExpect(jsonPath("$[0].resposta").value("A"))
                .andExpect(jsonPath("$[0].correto").value(false))
                .andExpect(jsonPath("$[0].respostaCorreta").value("B"));
    }

    @Test
    void answerKeyIsOnlyVisibleToContentEditors() throws Exception {
        jdbc.sql("INSERT INTO exercicios (id, tutorial_id, pergunta, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta, explicacao) VALUES (9001, 9001, 'Pergunta', 'A', 'B', 'C', 'D', 'C', 'Porque sim')").update();
        mvc.perform(get("/api/exercicios/gabarito?tutorialId=9001").session(session("ALUNO"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/exercicios?tutorialId=9001").session(session("ALUNO"))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].respostaCorreta").doesNotExist());
        mvc.perform(get("/api/exercicios/gabarito?tutorialId=9001").session(session("PROFESSOR"))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].respostaCorreta").value("C"))
                .andExpect(jsonPath("$[0].explicacao").value("Porque sim"));
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
