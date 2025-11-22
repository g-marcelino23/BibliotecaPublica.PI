package com.example.trabalho_biblioteca.service;

import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AmazonLinkService {

    private static final String AMAZON_BR_SEARCH = "https://www.amazon.com.br/s?k=";
    private static final String AMAZON_US_SEARCH = "https://www.amazon.com/s?k=";

    // Se você tiver tag de afiliado da Amazon, coloca aqui
    private static final String AFFILIATE_TAG = ""; // ex: "seusite-20"

    public String gerarLinkBuscaAmazonBR(String titulo, String autor) {
        String query = titulo;
        if (autor != null && !autor.isEmpty()) {
            query += " " + autor;
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = AMAZON_BR_SEARCH + encodedQuery;

        if (!AFFILIATE_TAG.isEmpty()) {
            url += "&tag=" + AFFILIATE_TAG;
        }

        return url;
    }

    public String gerarLinkBuscaAmazonUS(String titulo, String autor) {
        String query = titulo;
        if (autor != null && !autor.isEmpty()) {
            query += " " + autor;
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = AMAZON_US_SEARCH + encodedQuery;

        if (!AFFILIATE_TAG.isEmpty()) {
            url += "&tag=" + AFFILIATE_TAG;
        }

        return url;
    }

    public String gerarLinkPorISBN(String isbn) {
        String encodedISBN = URLEncoder.encode(isbn, StandardCharsets.UTF_8);
        String url = AMAZON_BR_SEARCH + encodedISBN;

        if (!AFFILIATE_TAG.isEmpty()) {
            url += "&tag=" + AFFILIATE_TAG;
        }

        return url;
    }
}
