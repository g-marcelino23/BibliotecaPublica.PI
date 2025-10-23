package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.dto.AutorDTO;
import com.example.trabalho_biblioteca.model.Autor;
import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.repository.AutorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AutorService {
    @Autowired
    AutorRepository autorRepository;

    @Value("${storage.capas-autores.path}")
    String caminhoCapasAutores;

    private Path capasAutoresLocation;
    @PostConstruct
    public void init(){
        this.capasAutoresLocation = Paths.get(caminhoCapasAutores);
        try{
            Files.createDirectories(capasAutoresLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar os diretórios de armazenamento", e);
        }
    }


    public Autor saveAutor(MultipartFile capa, String nome, String biografia){
        //verificando se o arquivo é uma imagem
        String tipoCapa = capa.getContentType();
        if(tipoCapa == null || (!tipoCapa.equals("image/jpeg") && !tipoCapa.equals("image/png"))){
            throw new IllegalArgumentException("A capa precisa ser um PNG ou JPEG.");
        }

        //Gerando um nome único para a capa do livro
        String nomeArquivoCapa = UUID.randomUUID().toString() + "-" + capa.getOriginalFilename();

        Autor autor = new Autor();
        autor.setNome(nome);
        autor.setBiografia(biografia);
        autor.setCaminhoCapa(nomeArquivoCapa);

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
            Autor autor = autorRepository.findById(id).orElseThrow(()-> new RuntimeException("Autor não encontrado"));
            // Excluir a capa do diretório de capas
            if (autor.getCaminhoCapa() != null && !autor.getCaminhoCapa().isEmpty()) {
                try {
                    Path capaPath = this.capasAutoresLocation.resolve(autor.getCaminhoCapa()).normalize();
                    Files.deleteIfExists(capaPath);
                } catch (IOException e) {
                    // Logar o erro, mas continuar para excluir do banco de dados
                    System.err.println("Erro ao deletar arquivo de capa físico: " + autor.getCaminhoCapa() + " - " + e.getMessage());
                }
            }
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

    public Autor updateAutor(MultipartFile capa, String nome, String biografia, Long idAutor){
        Autor autor = findById(idAutor);
        if(!(nome==null) && !(nome.isEmpty())){
            autor.setNome(nome);
        }

        if(!(biografia==null)&& !(biografia.isEmpty())){
            autor.setBiografia(biografia);
        }

        // Atualiza a capa se uma nova foi enviada
        if (capa != null && !capa.isEmpty()) {
            // Valida o tipo da nova capa
            String tipoNovaCapa = capa.getContentType();
            if (tipoNovaCapa == null || (!tipoNovaCapa.equals("image/jpeg") && !tipoNovaCapa.equals("image/png"))) {
                throw new IllegalArgumentException("A nova capa precisa ser um PNG ou JPEG.");
            }
            // Excluir a capa antiga
            if (autor.getCaminhoCapa() != null && !autor.getCaminhoCapa().isEmpty()) {
                try {
                    Path capaAntigaPath = this.capasAutoresLocation.resolve(autor.getCaminhoCapa()).normalize();
                    Files.deleteIfExists(capaAntigaPath);
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo de capa antigo: " + autor.getCaminhoCapa() + " - " + e.getMessage());
                }
            }

            // Salvar a nova capa
            String nomeNovaCapa = UUID.randomUUID().toString() + "-" + capa.getOriginalFilename();
            try {
                Path destinoNovaCapa = this.capasAutoresLocation.resolve(nomeNovaCapa);
                Files.copy(capa.getInputStream(), destinoNovaCapa, StandardCopyOption.REPLACE_EXISTING);
                autor.setCaminhoCapa(nomeNovaCapa);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a nova capa: " + nomeNovaCapa, e);
            }
        }
        return autorRepository.save(autor);
    }

    // --- NOVO MÉTODO PARA CARREGAR A CAPA COMO RESOURCE ---
    public Resource carregarCapaComoResource(String titulo) throws MalformedURLException {
        Autor autor = autorRepository.findByNome(titulo);

        if (autor == null) {
            // Log ou throw: Livro com título '{}' não encontrado , titulo
            throw new RuntimeException("Livro não encontrado com o título: " + titulo);
        }

        if (autor.getCaminhoCapa() == null || autor.getCaminhoCapa().isEmpty()) {
            // Log ou throw: Livro '{}' não possui caminho de capa definido , titulo
            throw new RuntimeException("Capa não definida para o livro: " + titulo);
        }

        try {
            Path caminhoCapa = this.capasAutoresLocation.resolve(autor.getCaminhoCapa()).normalize();
            Resource resource = new UrlResource(caminhoCapa.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Log: Recurso da capa não existe ou não é legível em: {} , caminhoCapa.toString()
                throw new RuntimeException("Capa não encontrada ou não pode ser lida: " + autor.getCaminhoCapa());
            }
        } catch (MalformedURLException e) {
            // Log: URL malformada para a capa '{}' do livro '{}': {} , livro.getCaminhoCapa(), titulo, e.getMessage()
            throw new RuntimeException("Erro ao carregar a capa (URL malformada): " + autor.getCaminhoCapa(), e);
        }
    }
}
