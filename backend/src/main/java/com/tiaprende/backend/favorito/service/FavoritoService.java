package com.tiaprende.backend.favorito.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.exception.ResourceNotFoundException;
import com.tiaprende.backend.favorito.dto.FavoritoResponse;
import com.tiaprende.backend.favorito.repository.FavoritoRepository;
import com.tiaprende.backend.tutorial.repository.TutorialRepository;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final TutorialRepository tutorialRepository;

    public FavoritoService(
            FavoritoRepository favoritoRepository,
            TutorialRepository tutorialRepository
    ) {
        this.favoritoRepository = favoritoRepository;
        this.tutorialRepository = tutorialRepository;
    }

    public List<FavoritoResponse> listar(Long usuarioId) {
        return favoritoRepository.findByUsuario(usuarioId);
    }

    public void adicionar(Long usuarioId, Long tutorialId) {

        // Só tutoriais ativos podem ser favoritados.
        tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutorial nao encontrado."));

        favoritoRepository.add(usuarioId, tutorialId);
    }

    public void remover(Long usuarioId, Long tutorialId) {
        favoritoRepository.remove(usuarioId, tutorialId);
    }
}
