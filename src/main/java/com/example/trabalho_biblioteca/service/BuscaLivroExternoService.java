package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.dto.LivroExternoResponseDTO;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.ArrayList;

@Service
@Slf4j
public class BuscaLivroExternoService {

    private final WebClient webClient;
    private final AmazonLinkService amazonLinkService;

    // Construtor único - Spring vai injetar automaticamente
    public BuscaLivroExternoService(WebClient.Builder webClientBuilder, AmazonLinkService amazonLinkService) {
        this.webClient = webClientBuilder
                .baseUrl("https://openlibrary.org")
                .build();
        this.amazonLinkService = amazonLinkService;
    }

    public LivroExternoResponseDTO buscarLivros(String query, int limite) {
        log.info("Buscando livros externos com query: {}", query);

        try {
            String url = String.format("/search.json?q=%s&limit=%d&fields=title,author_name,isbn,publisher,publish_year,cover_i,number_of_pages_median,language", 
                                    query, limite);
            log.info("URL completa: https://openlibrary.org{}", url);

            LivroExternoResponseDTO response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                log.error("Erro na API externa: {}", clientResponse.statusCode());
                                return Mono.error(new RuntimeException("Erro na API: " + clientResponse.statusCode()));
                            })
                    .bodyToMono(LivroExternoResponseDTO.class)
                    .timeout(Duration.ofSeconds(30)) // Aumenta timeout para 30s
                    .doOnError(error -> log.error("Erro ao buscar na API externa", error))
                    .block();

            log.info("Response recebido: {}", response);
            log.info("Total encontrados: {}", response != null ? response.getTotalEncontrados() : "null");
            log.info("Quantidade de livros: {}", response != null && response.getLivros() != null ? response.getLivros().size() : "0");

            // Adiciona o link da Amazon em cada livro
            if (response != null && response.getLivros() != null) {
                response.getLivros().forEach(livro -> {
                    String isbn = livro.getPrimeiroISBN();
                    if (isbn != null && !isbn.isEmpty()) {
                        livro.setLinkCompraAmazon(amazonLinkService.gerarLinkPorISBN(isbn));
                    } else {
                        livro.setLinkCompraAmazon(
                            amazonLinkService.gerarLinkBuscaAmazonBR(livro.getTitulo(), livro.getPrimeiroAutor())
                        );
                    }
                });
            }

            return response;

        } catch (Exception e) {
            log.error("Erro ao buscar livros na API externa", e);
            // Retorna um objeto vazio em vez de lançar exceção
            LivroExternoResponseDTO emptyResponse = new LivroExternoResponseDTO();
            emptyResponse.setTotalEncontrados(0);
            emptyResponse.setLivros(new ArrayList<>());
            return emptyResponse;
        }
    }


    public LivroExternoResponseDTO buscarPorTitulo(String titulo, int limite) {
        log.info("Buscando por título: {}", titulo);
        return buscarLivros("title:" + titulo, limite);
    }

    public LivroExternoResponseDTO buscarPorAutor(String autor, int limite) {
        log.info("Buscando por autor: {}", autor);
        return buscarLivros("author:" + autor, limite);
    }

    public LivroExternoResponseDTO buscarPorISBN(String isbn) {
        log.info("Buscando por ISBN: {}", isbn);
        return buscarLivros("isbn:" + isbn, 1);
    }

    public LivroExternoResponseDTO buscarGeral(String termo, int limite) {
        log.info("Busca geral: {}", termo);
        return buscarLivros(termo, limite);
    }
}
