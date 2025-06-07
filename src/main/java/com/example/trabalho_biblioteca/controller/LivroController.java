package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.LivroDTO; // Parece não estar sendo usado, pode ser removido se for o caso.
import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.trabalho_biblioteca.service.LivroService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // Adicionado para getCapaLivro se houver exceções de IO
import java.nio.file.Files; // Para determinar o content type de forma mais robusta
import java.nio.file.Path;  // Para trabalhar com o caminho do arquivo do Resource

import java.util.List;

@RestController
@RequestMapping("/api/livro")
@CrossOrigin(origins = "*") // Lembre-se da sua CorsConfig.java; esta anotação permite todas as origens.
public class LivroController {
    @Autowired
    LivroService livroService;

    @PostMapping("/cadastrar")
    public Livro postLivro( @RequestParam("descricao") String descricao,@RequestParam("titulo") String titulo, @RequestParam("autor") String autor, @RequestParam("pdf") MultipartFile file, @RequestParam("capa") MultipartFile capa) {
        return livroService.salvarLivro(file, capa, autor, titulo, descricao);
    }

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
        Livro livro = livroService.findByTitulo(titulo); // Você pode precisar tratar o caso de livro não encontrado
        if (livro == null) {
            return ResponseEntity.notFound().build();
        }
        Resource file = livroService.downloadByName(titulo); // Supondo que isso retorne o Resource do PDF

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

    // --- Novo metodo para passar a capa galera, esse ai q deu certinho fazer ela aparecer---
    @GetMapping("/capa/{titulo}")
    public ResponseEntity<Resource> getCapaLivro(@PathVariable String titulo) {
        try {
                      Resource capaResource = livroService.carregarCapaComoResource(titulo);

            if (capaResource == null || !capaResource.exists() || !capaResource.isReadable()) {
                // Log: Capa não encontrada ou não legível para o título: {} , titulo
                return ResponseEntity.notFound().build();
            }

            String contentType = null;
            try {
                // Tenta determinar o content type pelo arquivo
                // Isso requer que o Resource seja um FileSystemResource ou similar que exponha o caminho
                Path capaPath = capaResource.getFile().toPath();
                contentType = Files.probeContentType(capaPath);
            } catch (IOException e) {
                // Log: Não foi possível determinar o content type para a capa do livro: {} , titulo
                // Pode tentar adivinhar pela extensão do arquivo se probeContentType falhar
                String filename = capaResource.getFilename();
                if (filename != null) {
                    if (filename.toLowerCase().endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
                    else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
                    else if (filename.toLowerCase().endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;
                    // Adicione outros tipos se necessário
                }
            }

            // Se ainda não conseguiu determinar, usa um default genérico
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + capaResource.getFilename() + "\"")
                    .body(capaResource);

        } catch (Exception e) {
            // Log: Erro ao carregar capa para o livro {}: {} , titulo, e.getMessage()
            // e.printStackTrace(); // Para depuração, mas use um logger em produção
            return ResponseEntity.internalServerError().build();
        }
    }
}