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

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    @Value("${storage.pdf.path}")
    private String storagePath;
    @Value("${storage.capas.path}")
    private String capasPath;
    private Path rootLocation;
    private Path capasLocation;
    @PostConstruct
    public void init(){
        this.rootLocation = Paths.get(storagePath);
        this.capasLocation = Paths.get(capasPath);
        try{
            Files.createDirectories(rootLocation);
            Files.createDirectories(capasLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Livro salvarLivro(MultipartFile file, MultipartFile capa, String autor, String titulo, String descricao) {
        // Verificando se o arquivo é um PDF
        String tipoArquivo = file.getContentType();
        if (!"application/pdf".equals(tipoArquivo)) {
            throw new IllegalArgumentException("O arquivo precisa ser um PDF.");
        }

        //verificando se o arquivo é uma imagem
        String tipoCapa = capa.getContentType();
        if(!"image/jpeg".equals(tipoCapa)&& !"image/png".equals(tipoCapa)){
            throw new IllegalArgumentException("A capa precisa ser um png ou jpeg");
        }
        // Gerando nome único para o arquivo pdf
        String nomeArquivo = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        //Gerando um nome único para a capa do livro
        String nomeCapa = UUID.randomUUID().toString() + "-" + capa.getOriginalFilename();
    
        // Definindo o caminho completo onde o arquivo será salvo
        Path destinoArquivo = this.rootLocation.resolve(nomeArquivo);

        // Definindo o caminho completo de onde a capa está
        Path destinoCapa = this.capasLocation.resolve(nomeCapa);
    
        try {
            // Verificando se o arquivo não é nulo e transferindo para o diretório de armazenamento
            if (file != null && !file.isEmpty()) {
                // Copia o conteúdo do PDF para o diretório
                Files.copy(file.getInputStream(), destinoArquivo, StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IllegalArgumentException("Arquivo PDF não foi enviado corretamente.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o arquivo: " + nomeArquivo, e);
        }

        try {
            // Verificando se a capa não é nula e transferindo para o diretório de armazenamento
            if (capa != null && !capa.isEmpty()) {
                // Copia o conteúdo da capa para o diretório
                Files.copy(capa.getInputStream(), destinoCapa, StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IllegalArgumentException("Capa não foi salva corretamente.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a capa: " + nomeCapa, e);
        }


    
        // Criando o objeto Livro e atribuindo as informações
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setDescricao(descricao);
        livro.setCaminhoArquivo(nomeArquivo);  // Salvando o nome do arquivo no banco
        livro.setCaminhoCapa(nomeCapa);
    
        // Salvando o livro no repositório
        livroRepository.save(livro);
    
        return livro;
    }

    public String deletarLivro(Long id){
        Livro livro = livroRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Não existe livro com esse id"));
    
        // Excluir o arquivo físico
        if (livro.getCaminhoArquivo() != null) {
            File arquivo = new File(storagePath + "/" + livro.getCaminhoArquivo());
            if (arquivo.exists()) {
                arquivo.delete();
            }
        }
        // Excluir a capa do diretório de capas
        if (livro.getCaminhoCapa() != null) {
            File capa = new File(capasPath + "/" + livro.getCaminhoCapa());
            if (capa.exists()) {
                capa.delete();
            }
        }
    
        livroRepository.delete(livro);
        return "Livro de id = " + id + " foi deletado!";
    }

    public ResponseEntity<Livro> atualizarLivro(@RequestParam(value = "pdf", required = false) MultipartFile pdf,
                                            @RequestParam(value = "capa", required = false) MultipartFile capa,
                                            @RequestParam("titulo") String titulo,
                                            @RequestParam("autor") String autor,
                                            @RequestParam("descricao") String descricao,
                                            @PathVariable Long id) {
        Livro livroAntigo = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe livro com o id passado"));

        if (pdf != null && !pdf.isEmpty()) {
            // Excluir o arquivo antigo, se necessário
            File arquivoAntigo = new File(storagePath + "/" + livroAntigo.getCaminhoArquivo());
            if (arquivoAntigo.exists()) {
                arquivoAntigo.delete();
            }

            // Salvar o novo arquivo
            String nomeArquivo = UUID.randomUUID().toString() + "-" + pdf.getOriginalFilename();
            try {
                Files.copy(pdf.getInputStream(), Paths.get(storagePath).resolve(nomeArquivo), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            livroAntigo.setCaminhoArquivo(nomeArquivo);
        }


        if (capa != null && !capa.isEmpty()) {
            // Excluir a capa antiga, se necessário
            File capaAntiga = new File(capasPath + "/" + livroAntigo.getCaminhoCapa());
            if (capaAntiga.exists()) {
                capaAntiga.delete();
            }

            // Salvar a nova capa
            String nomeCapa = UUID.randomUUID().toString() + "-" + capa.getOriginalFilename();
            try {
                Files.copy(capa.getInputStream(), Paths.get(capasPath).resolve(nomeCapa), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            livroAntigo.setCaminhoCapa(nomeCapa);
        }

        livroAntigo.setTitulo(titulo);
        livroAntigo.setAutor(autor);
        livroAntigo.setDescricao(descricao);

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
