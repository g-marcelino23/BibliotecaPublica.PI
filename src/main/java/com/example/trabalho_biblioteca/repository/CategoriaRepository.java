package com.example.trabalho_biblioteca.repository;

import com.example.trabalho_biblioteca.model.Categoria;
import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria findByGeneroIgnoreCase(String genero);

    @Query("select c.genero from Categoria c")
    List<String> getNomesCategorias();
}
