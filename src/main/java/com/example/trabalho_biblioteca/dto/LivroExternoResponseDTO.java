package com.example.trabalho_biblioteca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LivroExternoResponseDTO {

    @JsonProperty("numFound")
    private Integer totalEncontrados;

    @JsonProperty("docs")
    private List<LivroExterno> livros;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LivroExterno {

        @JsonProperty("title")
        private String titulo;

        @JsonProperty("author_name")
        private List<String> autores;

        @JsonProperty("isbn")
        private List<String> isbn;

        @JsonProperty("publisher")
        private List<String> editora;

        @JsonProperty("publish_year")
        private List<Integer> anoPublicacao;

        @JsonProperty("cover_i")
        private Long capaId;

        @JsonProperty("number_of_pages_median")
        private Integer numeroPaginas;

        @JsonProperty("language")
        private List<String> idiomas;

        // NOVO: Link para comprar na Amazon
        private String linkCompraAmazon;

        public String getUrlCapa() {
            if (capaId != null) {
                return "https://covers.openlibrary.org/b/id/" + capaId + "-L.jpg";
            }
            return null;
        }

        public String getUrlCapaMedia() {
            if (capaId != null) {
                return "https://covers.openlibrary.org/b/id/" + capaId + "-M.jpg";
            }
            return null;
        }

        public String getPrimeiroAutor() {
            if (autores != null && !autores.isEmpty()) {
                return autores.get(0);
            }
            return "Autor desconhecido";
        }

        public Integer getPrimeiroAno() {
            if (anoPublicacao != null && !anoPublicacao.isEmpty()) {
                return anoPublicacao.get(0);
            }
            return null;
        }

        public String getPrimeiroISBN() {
            if (isbn != null && !isbn.isEmpty()) {
                return isbn.get(0);
            }
            return null;
        }
    }
}
