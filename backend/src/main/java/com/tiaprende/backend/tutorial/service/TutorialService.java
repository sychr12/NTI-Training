package com.tiaprende.backend.tutorial.service;

import com.tiaprende.backend.tutorial.repository.TutorialRepository;
import com.tiaprende.backend.exception.ResourceNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.tutorial.dto.TutorialRequest;
import com.tiaprende.backend.tutorial.dto.TutorialResponse;
import com.tiaprende.backend.tutorial.model.Tutorial;

@Service 
public class TutorialService {
    private final TutorialRepository tutorialRepository;

    public TutorialService(
        TutorialRepository tutorialRepository
    ){
        this.tutorialRepository = tutorialRepository;
    }
    public List<TutorialResponse> listaTodos(){
        return tutorialRepository
        .findAll()
        .stream()
        .map(TutorialResponse::from)
        .toList();
    }

    public TutorialResponse buscarPorId(Long id){
        Tutorial tutorial = tutorialRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tutorial nao encontrado"));
        
        return  TutorialResponse.from(tutorial);
    
        
    }

    public TutorialResponse criar(TutorialRequest request){
        validar(request);

        Tutorial tutorial = tutorialRepository.save(request);

        return TutorialResponse.from (tutorial);
    }

    public TutorialResponse atualizar(Long id,
        TutorialRequest request
    ){
        validar(request);

        Tutorial tutorial = tutorialRepository
        .update(id, request)
        .orElseThrow(() -> new ResourceNotFoundException("Tutorial nao encontrado."));

        return TutorialResponse.from (tutorial);
    }

    public void remover(Long id){
        boolean removido = tutorialRepository.deactivate(id);

        if (!removido){
            throw new ResourceNotFoundException("Tutorial nao encontrado.");
        }
    }

    private void validar (TutorialRequest request){
        if (request == null){
            throw new IllegalArgumentException("Dados do tutorial sao obrigatorios");
        }
        if (request.titulo()==null || request.titulo().isBlank()){
            throw new IllegalArgumentException(  "Titulo do tutorial e obrigatorio");
        }

        if (request.conteudo() == null || request.conteudo().isBlank()){
            throw new IllegalArgumentException( "Conteudo do tutorial e obrigatorio");
        }
    }
    
}
