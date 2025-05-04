package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.dto.LivroDTO;
import com.example.trabalho_biblioteca.mapper.LivroMapper;
import com.example.trabalho_biblioteca.model.Livro;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.trabalho_biblioteca.repository.LivroRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class LivroService {
    @Autowired
    LivroRepository livroRepository;
    @Value("${storage.path}")
    private String storagePath;
    private Path rootLocation;
    @PostConstruct
    public void init(){
        this.rootLocation = Paths.get(storagePath);
        try{
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Livro salvarLivro(MultipartFile file, String autor, String titulo, String descricao) {
       String nomeArquivo = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

       try{
            Files.copy(file.getInputStream(), this.rootLocation.resolve(nomeArquivo), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       Livro livro = new Livro();
       livro.setTitulo(titulo);
       livro.setAutor(autor);
       livro.setDescricao(descricao);
       livro.setCaminhoArquivo(nomeArquivo);
       livroRepository.save(livro);

        return livro;
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

    public Livro findByTitulo(String titulo){
        return livroRepository.findByTitulo(titulo);
    }

    public ResponseEntity<List<Livro>> findAll(){
        return ResponseEntity.ok(livroRepository.findAll());
    }

    public Resource downloadByName(String titulo) throws MalformedURLException {
        Livro livro = livroRepository.findByTitulo(titulo);

        Path caminhoArquivo = rootLocation.resolve(livro.getCaminhoArquivo());
        Resource resource = new UrlResource(caminhoArquivo.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Arquivo não encontrado ou não pode ser lido");
        }

        return resource;
    }

    public void deletarLivroByTitulo(String titulo){
        Livro livro = livroRepository.findByTitulo(titulo);
        Path caminho = rootLocation.resolve(livro.getCaminhoArquivo());
        try{
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        livroRepository.deleteById(livro.getId());
    }

}
