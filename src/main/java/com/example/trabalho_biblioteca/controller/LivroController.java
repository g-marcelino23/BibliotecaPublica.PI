package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.LivroDTO;
import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.trabalho_biblioteca.service.LivroService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/livro")
@CrossOrigin(origins = "*")
public class LivroController {
    @Autowired
    LivroService livroService;

    @PostMapping("/cadastrar")
    public Livro postLivro( @RequestParam String descricao,@RequestParam String titulo, @RequestParam String autor, @RequestParam MultipartFile file) {
        return livroService.salvarLivro(file, autor, titulo, descricao);
    }

//    @DeleteMapping("/deletar/{id}")
//    public String deletarLivro(@PathVariable Long id) {
//        return livroService.deletarLivro(id);
//    }

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

    @GetMapping("/download/{titulo}")
    public ResponseEntity<Resource> download(@PathVariable String titulo) throws IOException {
        Livro livro = livroService.findByTitulo(titulo);
        Resource file = livroService.downloadByName(titulo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + livro.getTitulo() + "\"")
                .body(file);
    }

    @DeleteMapping("/deletar/{titulo}")
    public void deleteByTitulo(@PathVariable String titulo){
        livroService.deletarLivroByTitulo(titulo);
    }
}
