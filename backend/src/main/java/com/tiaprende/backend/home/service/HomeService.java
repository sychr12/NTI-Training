package com.tiaprende.backend.home.service;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import com.tiaprende.backend.home.dto.ContinueLearningResponse;
import com.tiaprende.backend.home.dto.HomeResponse;
import com.tiaprende.backend.home.dto.HomeStatsResponse;
import com.tiaprende.backend.login.session.AuthSession.SessionUser;
import com.tiaprende.backend.progresso.repository.ProgressoRepository;

@Service
public class HomeService {
    private final ProgressoRepository progressoRepository;
    private final JdbcClient jdbcClient;

    public HomeService(ProgressoRepository progressoRepository, JdbcClient jdbcClient) {
        this.progressoRepository = progressoRepository;
        this.jdbcClient = jdbcClient;
    }

    public HomeResponse carregarHome(SessionUser usuario) {
        int concluidos = progressoRepository.countTutoriaisConcluidos(usuario.id());
        int iniciados = progressoRepository.countTutoriaisIniciados(usuario.id());
        int exercicios = jdbcClient.sql("""
                SELECT COUNT(*) FROM respostas_exercicios r
                JOIN exercicios e ON e.id = r.exercicio_id AND e.ativo = TRUE
                JOIN tutoriais t ON t.id = e.tutorial_id AND t.ativo = TRUE
                WHERE r.usuario_id = :usuarioId AND r.correto = TRUE
                """).param("usuarioId", usuario.id()).query(Integer.class).single();
        var continuar = jdbcClient.sql("""
                SELECT t.id, t.titulo, t.descricao
                FROM progresso_tutoriais p
                JOIN tutoriais t ON t.id = p.tutorial_id AND t.ativo = TRUE
                WHERE p.usuario_id = :usuarioId AND p.status = 'EM_ANDAMENTO'
                ORDER BY p.atualizado_em DESC, t.id
                """).param("usuarioId", usuario.id())
                .query((rs, row) -> new ContinueLearningResponse(rs.getLong("id"),
                        rs.getString("titulo"), rs.getString("descricao"), 0, "TUTORIAL"))
                .list();
        return new HomeResponse(usuario.nome(), new HomeStatsResponse(iniciados - concluidos,
                concluidos, exercicios, progressoRepository.calcularProgressoGeral(usuario.id())), continuar);
    }
}
