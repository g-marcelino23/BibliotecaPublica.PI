package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.LivroDTO;
import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.trabalho_biblioteca.service.LivroService;

import java.util.List;

@RestController
@RequestMapping("/api/livro")
@CrossOrigin(origins = "*")
public class LivroController {
    @Autowired
    LivroService livroService;

    @PostMapping("/cadastrar")
    public ResponseEntity<Livro> postLivro(@RequestBody LivroDTO livroDTO) {
        return livroService.salvarLivro(livroDTO);
    }

    @DeleteMapping("/deletar/{id}")
    public String deletarLivro(@PathVariable Long id) {
        return livroService.deletarLivro(id);
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<Livro> atualizarLivro(@PathVariable Long id, @RequestBody LivroDTO livroDTO){
        return livroService.atualizarLivro(livroDTO, id);
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<Livro> findById(@PathVariable Long id){
        return livroService.findById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Livro>> findAll(){
        return livroService.findAll();
    }
}
