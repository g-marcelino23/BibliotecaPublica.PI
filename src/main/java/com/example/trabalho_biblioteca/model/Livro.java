package com.example.trabalho_biblioteca.model;

import jakarta.persistence.*;

import java.lang.annotation.Target;

@Entity
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "titulo", length = 75, nullable = false)
    private String titulo;
    @Column(name = "autor", length = 75, nullable = false)
    private String autor;
    private String descricao;
    @Column(name = "caminho_arquivo", nullable = false)
    private String caminhoArquivo;
    @Column(name = "caminho_capa",nullable = false)
    private String caminhoCapa;

    public Livro(){}

    public Livro(String titulo, String autor, String descricao, String caminhoArquivo) {
        this.titulo = titulo;
        this.autor = autor;
        this.descricao = descricao;
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getCaminhoCapa() {
        return caminhoCapa;
    }

    public void setCaminhoCapa(String caminhoCapa) {
        this.caminhoCapa = caminhoCapa;
    }
}
