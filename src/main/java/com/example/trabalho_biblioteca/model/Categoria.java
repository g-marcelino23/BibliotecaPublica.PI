package com.example.trabalho_biblioteca.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String genero;


    public Categoria() {
    }

    public Categoria(String categoria) {
        this.genero = categoria;
    }

    public Categoria(Long id, String categoria) {
        this.id = id;
        this.genero = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", categoria=" + genero +
                '}';
    }


}
