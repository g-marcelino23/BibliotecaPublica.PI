package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.model.ClassificacaoIndicativa;
import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.dto.LivroDTO;
import com.example.trabalho_biblioteca.model.User;
import com.example.trabalho_biblioteca.service.LivroService;
import com.example.trabalho_biblioteca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/livro")
@CrossOrigin(origins = "*")
public class LivroController {

    @Autowired
    LivroService livroService;

    @Autowired
    UserService userService;

    @PostMapping("/cadastrar")
    public Livro postLivro(
            @RequestParam("descricao") String descricao,
            @RequestParam("titulo") String titulo,
            @RequestParam("autor") String autor,
            @RequestParam("pdf") MultipartFile file,
            @RequestParam("capa") MultipartFile capa,
            @RequestParam("categoria") String nomeCategoria,
            @RequestParam("classificacaoIndicativa") String classificacaoStr
    ) {
        ClassificacaoIndicativa classificacao = parseClassificacao(classificacaoStr);
        return livroService.salvarLivro(file, capa, autor, titulo, descricao, nomeCategoria, classificacao);
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<Livro> atualizarLivro(
            @RequestParam(value = "pdf", required = false) MultipartFile pdf,
            @RequestParam(value = "capa", required = false) MultipartFile capa,
            @RequestParam("titulo") String titulo,
            @RequestParam("autor") String autor,
            @RequestParam("descricao") String descricao,
            @RequestParam("categoria") String categoria,
            @RequestParam("classificacaoIndicativa") String classificacaoStr,
            @PathVariable Long id
    ) {
        ClassificacaoIndicativa classificacao = parseClassificacao(classificacaoStr);
        return livroService.atualizarLivro(pdf, capa, titulo, autor, descricao, id, categoria, classificacao);
    }

    // Função segura para parse do Enum
    private ClassificacaoIndicativa parseClassificacao(String classificacaoStr) {
        try {
            return ClassificacaoIndicativa.valueOf(classificacaoStr.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Classificação Indicativa inválida! Use: L, DEZ, DOZE, QUATORZE, DEZESSEIS ou DEZOITO.");
        }
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<Livro> findById(@PathVariable Long id) {
        return livroService.findById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Livro>> findAll() {
        return livroService.findAll();
    }

    // NOVO: endpoint que só lista livros PERMITIDOS para o usuário logado
    @GetMapping("/permitidos")
    public ResponseEntity<List<LivroDTO>> getLivrosPermitidos() {
        User user = userService.getUsuarioLogado();
        List<Livro> permitidos = livroService.listarLivrosPermitidosParaUsuario(user);
        // Mapeando para DTO
        List<LivroDTO> dtos = permitidos.stream()
        .map(livro -> new LivroDTO(
            livro.getTitulo(),
            livro.getAutor().getNome(),
            livro.getDescricao(),
            livro.getCategoria() != null ? livro.getCategoria().getGenero() : null,
            livro.getClassificacaoIndicativa() != null ? livro.getClassificacaoIndicativa().getDescricao() : null
        ))
        .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/download/{titulo}")
    public ResponseEntity<Resource> download(@PathVariable String titulo) throws IOException {
        Livro livro = livroService.findByTitulo(titulo);
        if (livro == null) {
            return ResponseEntity.notFound().build();
        }
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
    public void deleteLivro(@PathVariable Long id) {
        livroService.deletarLivro(id);
    }

    @GetMapping("/capa/{titulo}")
    public ResponseEntity<Resource> getCapaLivro(@PathVariable String titulo) {
        try {
            Resource capaResource = livroService.carregarCapaComoResource(titulo);
            if (capaResource == null || !capaResource.exists() || !capaResource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = null;
            try {
                Path capaPath = capaResource.getFile().toPath();
                contentType = Files.probeContentType(capaPath);
            } catch (IOException e) {
                String filename = capaResource.getFilename();
                if (filename != null) {
                    if (filename.toLowerCase().endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
                    else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
                    else if (filename.toLowerCase().endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;
                }
            }
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + capaResource.getFilename() + "\"")
                    .body(capaResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("{nomeAutor}")
    public ResponseEntity<List<Livro>> findLivrosByAutor(@PathVariable String nomeAutor){
        return ResponseEntity.ok().body(livroService.getLivrosByAutor(nomeAutor));
    }
}
