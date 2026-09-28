package com.tiaprende.backend;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.tiaprende.backend.login.dto.AdUser;
import com.tiaprende.backend.user.repository.UserRepository;
import com.tiaprende.backend.tutorial.repository.TutorialRepository;
import com.tiaprende.backend.tutorial.dto.TutorialRequest;
import com.tiaprende.backend.trilha.repository.TrilhaRepository;
import com.tiaprende.backend.trilha.dto.TrilhaRequest;
import com.tiaprende.backend.dica.repository.DicaRepository;
import com.tiaprende.backend.dica.dto.DicaRequest;
import com.tiaprende.backend.glossario.repository.GlossarioRepository;
import com.tiaprende.backend.glossario.dto.TermoGlossarioRequest;
import com.tiaprende.backend.exercicio.repository.ExercicioRepository;
import com.tiaprende.backend.exercicio.dto.ExercicioRequest;
import com.tiaprende.backend.exercicio.dto.RespostaExercicioRequest;
import com.tiaprende.backend.exercicio.service.ExercicioService;
import com.tiaprende.backend.progresso.repository.ProgressoRepository;
import com.tiaprende.backend.home.service.HomeService;
import com.tiaprende.backend.login.session.AuthSession.SessionUser;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "NTI_TEST_DATABASE_DRIVER", matches = "org.postgresql.Driver")
class PostgresPersistenceTests {
    @Autowired org.springframework.web.context.WebApplicationContext context;
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    com.tiaprende.backend.login.ldap.ActiveDirectoryClient directory;
    @Autowired UserRepository users;
    @Autowired TutorialRepository tutorials;
    @Autowired TrilhaRepository tracks;
    @Autowired DicaRepository tips;
    @Autowired GlossarioRepository glossary;
    @Autowired ExercicioRepository exercises;
    @Autowired ExercicioService exerciseService;
    @Autowired ProgressoRepository progress;
    @Autowired HomeService home;

    TutorialRequest tutorial(String title) {
        return new TutorialRequest(title, "Descricao", "Conteudo", "Redes", "Basico");
    }

    @Test
    void adUpsertKeepsIdentityRoleAndStatusAndRefreshesEmail() {
        var first = users.upsertFromAd(new AdUser("guid-test", "login", "Nome", "old@example.test", List.of()));
        assertEquals("ALUNO", first.perfil());
        users.update(first.id(), "PROFESSOR", false);
        var next = users.upsertFromAd(new AdUser("guid-test", "newlogin", "Novo", "new@example.test", List.of()));
        assertEquals(first.id(), next.id());
        assertEquals("PROFESSOR", next.perfil());
        assertFalse(next.ativo());
        assertEquals("new@example.test", next.email());
        assertEquals("newlogin", next.login());
    }

    @Test
    void tutorialCrudAndSoftDelete() {
        var saved = tutorials.save(tutorial("Inicial"));
        assertEquals("Atualizado", tutorials.update(saved.id(), tutorial("Atualizado")).orElseThrow().titulo());
        assertTrue(tutorials.deactivate(saved.id()));
        assertTrue(tutorials.findById(saved.id()).isEmpty());
        assertTrue(tutorials.update(saved.id(), tutorial("Ignorado")).isEmpty());
        assertFalse(tutorials.deactivate(saved.id()));
    }

    @Test
    void trackTutorialOrderAndReplacement() {
        var a = tutorials.save(tutorial("A"));
        var b = tutorials.save(tutorial("B"));
        var track = tracks.save(new TrilhaRequest("Trilha", "Descricao", "Basico", List.of(b.id(), a.id())));
        assertEquals(b.id(), tracks.findTutoriais(track.id()).get(0).id());
        tracks.update(track.id(), new TrilhaRequest("Nova", "Descricao", "Basico", List.of(a.id())));
        assertEquals(1, tracks.findTutoriais(track.id()).size());
        assertEquals(a.id(), tracks.findTutoriais(track.id()).get(0).id());
        assertTrue(tracks.deactivate(track.id()));
        assertTrue(tracks.findById(track.id()).isEmpty());
    }

    @Test
    void tipAndGlossaryCrud() {
        var tip = tips.save(new DicaRequest("Dica", "Conteudo", "Redes"));
        assertEquals("Nova", tips.update(tip.id(), new DicaRequest("Nova", "Conteudo", "Redes")).orElseThrow().titulo());
        assertFalse(tips.findByCategoria("redes").isEmpty());
        assertTrue(tips.deactivate(tip.id()));
        assertTrue(tips.findById(tip.id()).isEmpty());
        var term = glossary.save(new TermoGlossarioRequest("TCP", "Protocolo", "Redes"));
        assertEquals("TCP/IP", glossary.update(term.id(), new TermoGlossarioRequest("TCP/IP", "Protocolo", "Redes")).orElseThrow().termo());
        assertFalse(glossary.search("tcp").isEmpty());
        assertTrue(glossary.deactivate(term.id()));
        assertTrue(glossary.findById(term.id()).isEmpty());
    }

    @Test
    void progressUpsertPreservesStartAndCompletionAndCanReopen() {
        var user = users.upsertFromAd(new AdUser("progress-guid", "login", "Nome", null, List.of()));
        var tutorial = tutorials.save(tutorial("Progresso"));
        var started = progress.saveOrUpdate(user.id(), tutorial.id(), "EM_ANDAMENTO");
        var done = progress.saveOrUpdate(user.id(), tutorial.id(), "CONCLUIDO");
        var repeated = progress.saveOrUpdate(user.id(), tutorial.id(), "CONCLUIDO");
        assertEquals(started.id(), done.id());
        assertEquals(started.iniciadoEm(), done.iniciadoEm());
        assertNotNull(done.concluidoEm());
        assertEquals(done.concluidoEm(), repeated.concluidoEm());
        assertNull(progress.saveOrUpdate(user.id(), tutorial.id(), "EM_ANDAMENTO").concluidoEm());
        assertEquals(1, progress.findByUsuario(user.id()).size());
    }

    @Test
    void exerciseAnswerIsNormalizedAndSavedPerUser() {
        var user = users.upsertFromAd(new AdUser("exercise-guid", "login", "Nome", null, List.of()));
        var tutorial = tutorials.save(tutorial("Exercicio"));
        var request = new ExercicioRequest(tutorial.id(), "Pergunta", "A", "B", "C", "D", " a ", "Explicacao");
        var exercise = exercises.save(request);
        assertEquals("A", exercise.respostaCorreta());
        assertTrue(exerciseService.responder(user.id(), exercise.id(), new RespostaExercicioRequest(" a ")).correto());
        exerciseService.responder(user.id(), exercise.id(), new RespostaExercicioRequest("A"));
        var session = new SessionUser(user.id(), user.login(), user.nome(), user.email(), user.perfil());
        assertEquals(1, home.carregarHome(session).estatisticas().exerciciosConcluidos());
        exercises.update(exercise.id(), request).orElseThrow();
        assertTrue(exercises.deactivate(exercise.id()));
        assertTrue(exercises.findById(exercise.id()).isEmpty());
        assertEquals(0, home.carregarHome(session).estatisticas().exerciciosConcluidos());
    }
    @Test
    void loginRotatesSessionAndUsesRememberTimeout() throws Exception {
        org.mockito.Mockito.when(directory.authenticate("login", "secret"))
                .thenReturn(new AdUser("login-guid", "login", "Nome", null, List.of()));
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
        var previous = new org.springframework.mock.web.MockHttpSession();
        var result = mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                .session(previous).contentType("application/json")
                .content("{\"usuario\":\"login\",\"senha\":\"secret\",\"lembrar\":true}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk()).andReturn();
        assertTrue(previous.isInvalid());
        var session = (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false);
        assertNotNull(session);
        assertEquals(2592000, session.getMaxInactiveInterval());
        assertNotNull(session.getAttribute(com.tiaprende.backend.login.session.AuthSession.USER));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/me").session(session))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/home").session(session))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    }

    @Test
    void disabledUserCannotLogin() throws Exception {
        var user = users.upsertFromAd(new AdUser("blocked-guid", "blocked", "Nome", null, List.of()));
        users.update(user.id(), "ALUNO", false);
        org.mockito.Mockito.when(directory.authenticate("blocked", "secret"))
                .thenReturn(new AdUser("blocked-guid", "blocked", "Nome", null, List.of()));
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                .contentType("application/json").content("{\"usuario\":\"blocked\",\"senha\":\"secret\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
    }

}
