package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.dto.LivroDTO;
import com.example.trabalho_biblioteca.mapper.LivroMapper;
import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.trabalho_biblioteca.repository.LivroRepository;

import java.util.List;

@Service
public class LivroService {
    @Autowired
    LivroRepository livroRepository;

    public ResponseEntity<Livro> salvarLivro(LivroDTO livro){
       return ResponseEntity.ok(livroRepository.save(LivroMapper.dtoPraLivro(livro)));
    }

    public String deletarLivro(Long id){
        Livro livro = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe livro com ess id"));
        livroRepository.delete(livro);
        return "Livro de id = "+ id+" foi deletado!";
    }

    public ResponseEntity<Livro> atualizarLivro(LivroDTO novoLivro, Long id){
        Livro livroAntigo = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe livro com o id passado"));
        livroAntigo.setAutor(novoLivro.autor());
        livroAntigo.setTitulo(novoLivro.titulo());
        livroAntigo.setDescricao(novoLivro.descricao());
        return ResponseEntity.ok(livroRepository.save(livroAntigo));
    }

    public ResponseEntity<Livro> findById(long id){
        return ResponseEntity.ok(livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe um livro com esse id")));
    }

    public ResponseEntity<List<Livro>> findAll(){
        return ResponseEntity.ok(livroRepository.findAll());
    }

}
