package com.tiaprende.backend.dica.service;

import com.tiaprende.backend.exception.ResourceNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.dica.dto.DicaRequest;
import com.tiaprende.backend.dica.dto.DicaResponse;
import com.tiaprende.backend.dica.model.Dica;
import com.tiaprende.backend.dica.repository.DicaRepository;

@Service
public class DicaService {

    private final DicaRepository dicaRepository;

    public DicaService(
            DicaRepository dicaRepository
    ) {
        this.dicaRepository = dicaRepository;
    }

    public List<DicaResponse> listar(
            String categoria
    ) {

        List<Dica> dicas;

        if (categoria == null
                || categoria.isBlank()) {

            dicas = dicaRepository.findAll();

        } else {

            dicas = dicaRepository
                    .findByCategoria(categoria);
        }

        return dicas
                .stream()
                .map(DicaResponse::from)
                .toList();
    }

    public DicaResponse buscarPorId(Long id) {

        Dica dica = dicaRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Dica nao encontrada."
                        )
                );

        return DicaResponse.from(dica);
    }

    public DicaResponse criar(
            DicaRequest request
    ) {

        validar(request);

        Dica dica =
                dicaRepository.save(request);

        return DicaResponse.from(dica);
    }

    public DicaResponse atualizar(
            Long id,
            DicaRequest request
    ) {

        validar(request);

        Dica dica = dicaRepository
                .update(id, request)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Dica nao encontrada."
                        )
                );

        return DicaResponse.from(dica);
    }

    public void remover(Long id) {

        boolean removida =
                dicaRepository.deactivate(id);

        if (!removida) {

            throw new ResourceNotFoundException(
                    "Dica nao encontrada."
            );
        }
    }

    private void validar(
            DicaRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Dados da dica sao obrigatorios."
            );
        }

        if (request.titulo() == null
                || request.titulo().isBlank()) {

            throw new IllegalArgumentException(
                    "Titulo da dica e obrigatorio."
            );
        }

        if (request.conteudo() == null
                || request.conteudo().isBlank()) {

            throw new IllegalArgumentException(
                    "Conteudo da dica e obrigatorio."
            );
        }

        if (request.categoria() == null
                || request.categoria().isBlank()) {

            throw new IllegalArgumentException(
                    "Categoria da dica e obrigatoria."
            );
        }
    }
}