package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.dto.AutorDTO;
import com.example.trabalho_biblioteca.model.Autor;
import com.example.trabalho_biblioteca.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AutorService {
    @Autowired
    AutorRepository autorRepository;

    public Autor saveAutor(Autor autor){
        return autorRepository.save(autor);
    }

    public List<Autor> getAutores(){
        return autorRepository.findAll();
    }

    public Autor findById(Long id){
        return autorRepository.findById(id).orElseThrow(() -> new RuntimeException("Autor não encontrado"));
    }

    public ResponseEntity<Object> deleteById(Long id){
        try{
            autorRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public Autor findByNome(String nome){
        return autorRepository.findByNome(nome);
    }

    public Autor updateAutor(AutorDTO autorDTO, Long idAutor){
        Autor autor = findById(idAutor);

        if(!(autorDTO.nome() == null)){
            autor.setNome(autorDTO.nome());
        }
        if(!(autorDTO.biografia() == null)){
            autor.setBiografia(autorDTO.biografia());
        }
        return autorRepository.save(autor);
    }
}
