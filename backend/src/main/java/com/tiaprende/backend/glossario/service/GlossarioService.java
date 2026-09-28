package com.tiaprende.backend.glossario.service;

import com.tiaprende.backend.exception.ResourceNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.glossario.dto.TermoGlossarioRequest;
import com.tiaprende.backend.glossario.dto.TermoGlossarioResponse;
import com.tiaprende.backend.glossario.model.TermoGlossario;
import com.tiaprende.backend.glossario.repository.GlossarioRepository;

@Service
public class GlossarioService {

    private final GlossarioRepository glossarioRepository;

    public GlossarioService(
            GlossarioRepository glossarioRepository
    ) {
        this.glossarioRepository =
                glossarioRepository;
    }

    public List<TermoGlossarioResponse> listar(
            String busca
    ) {

        List<TermoGlossario> termos;

        if (busca == null || busca.isBlank()) {

            termos = glossarioRepository.findAll();

        } else {

            termos = glossarioRepository.search(
                    busca.trim()
            );
        }

        return termos
                .stream()
                .map(TermoGlossarioResponse::from)
                .toList();
    }

    public TermoGlossarioResponse buscarPorId(
            Long id
    ) {

        TermoGlossario termo =
                glossarioRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Termo nao encontrado."
                                )
                        );

        return TermoGlossarioResponse.from(termo);
    }

    public TermoGlossarioResponse criar(
            TermoGlossarioRequest request
    ) {

        validar(request);

        TermoGlossario termo =
                glossarioRepository.save(request);

        return TermoGlossarioResponse.from(termo);
    }

    public TermoGlossarioResponse atualizar(
            Long id,
            TermoGlossarioRequest request
    ) {

        validar(request);

        TermoGlossario termo =
                glossarioRepository
                        .update(id, request)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Termo nao encontrado."
                                )
                        );

        return TermoGlossarioResponse.from(termo);
    }

    public void remover(Long id) {

        boolean removido =
                glossarioRepository.deactivate(id);

        if (!removido) {

            throw new ResourceNotFoundException(
                    "Termo nao encontrado."
            );
        }
    }

    private void validar(
            TermoGlossarioRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Dados do termo sao obrigatorios."
            );
        }

        if (request.termo() == null
                || request.termo().isBlank()) {

            throw new IllegalArgumentException(
                    "Termo e obrigatorio."
            );
        }

        if (request.definicao() == null
                || request.definicao().isBlank()) {

            throw new IllegalArgumentException(
                    "Definicao e obrigatoria."
            );
        }

        if (request.categoria() == null
                || request.categoria().isBlank()) {

            throw new IllegalArgumentException(
                    "Categoria e obrigatoria."
            );
        }
    }
}