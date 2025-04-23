package com.example.trabalho_biblioteca.model;

import jakarta.persistence.*;

import java.lang.annotation.Target;

@Entity
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "titulo", length = 75)
    private String titulo;
    @Column(name = "autor", length = 75, nullable = false)
    private String Autor;
    private String descricao;

    public Livro(){}

    public Livro(String titulo, String autor, String descricao) {
        this.titulo = titulo;
        Autor = autor;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return Autor;
    }

    public void setAutor(String autor) {
        Autor = autor;
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
}
