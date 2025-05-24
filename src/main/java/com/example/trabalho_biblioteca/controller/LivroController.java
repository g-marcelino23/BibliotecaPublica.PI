package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.LivroDTO;
import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    public Livro postLivro( @RequestParam("descricao") String descricao,@RequestParam("titulo") String titulo, @RequestParam("autor") String autor, @RequestParam("pdf") MultipartFile file, @RequestParam("capa") MultipartFile capa) {
        return livroService.salvarLivro(file, capa, autor, titulo, descricao);
    }

//    @DeleteMapping("/deletar/{id}")
//    public String deletarLivro(@PathVariable Long id) {
//        return livroService.deletarLivro(id);
//    }

@PutMapping("/alterar/{id}")
public ResponseEntity<Livro> atualizarLivro(
        @RequestParam(value = "pdf", required = false) MultipartFile pdf,
        @RequestParam(value = "capa", required = false) MultipartFile capa,
        @RequestParam("titulo") String titulo,
        @RequestParam("autor") String autor,
        @RequestParam("descricao") String descricao,
        @PathVariable Long id) {
    return livroService.atualizarLivro(pdf, capa, titulo, autor, descricao, id);
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

        String nomeArquivo = livro.getTitulo();
        if (!nomeArquivo.toLowerCase().endsWith(".pdf")) {
            nomeArquivo += ".pdf";
    }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .body(file);
    }

    @DeleteMapping("/deletar/{id}")
    public void deleteLivro(@PathVariable Long id){
        livroService.deletarLivro(id);
    }
}
