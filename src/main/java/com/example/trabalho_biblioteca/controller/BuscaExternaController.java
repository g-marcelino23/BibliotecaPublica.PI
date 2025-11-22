package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.LivroExternoResponseDTO;
import com.example.trabalho_biblioteca.service.BuscaLivroExternoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/busca-externa")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000") // Permite requisições do frontend
public class BuscaExternaController {

    private final BuscaLivroExternoService buscaLivroExternoService;

    @GetMapping
    public ResponseEntity<LivroExternoResponseDTO> buscarLivros(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "limite", defaultValue = "10") int limite
    ) {
        log.info("Recebida requisição de busca externa: query={}, limite={}", query, limite);

        try {
            LivroExternoResponseDTO resultado = buscaLivroExternoService.buscarGeral(query, limite);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao buscar livros externos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/titulo")
    public ResponseEntity<LivroExternoResponseDTO> buscarPorTitulo(
            @RequestParam String titulo,
            @RequestParam(defaultValue = "10") int limite
    ) {
        log.info("Busca por título: {}", titulo);
        LivroExternoResponseDTO resultado = buscaLivroExternoService.buscarPorTitulo(titulo, limite);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/autor")
    public ResponseEntity<LivroExternoResponseDTO> buscarPorAutor(
            @RequestParam String autor,
            @RequestParam(defaultValue = "10") int limite
    ) {
        log.info("Busca por autor: {}", autor);
        LivroExternoResponseDTO resultado = buscaLivroExternoService.buscarPorAutor(autor, limite);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/isbn")
    public ResponseEntity<LivroExternoResponseDTO> buscarPorISBN(@RequestParam String isbn) {
        log.info("Busca por ISBN: {}", isbn);
        LivroExternoResponseDTO resultado = buscaLivroExternoService.buscarPorISBN(isbn);
        return ResponseEntity.ok(resultado);
    }
}
