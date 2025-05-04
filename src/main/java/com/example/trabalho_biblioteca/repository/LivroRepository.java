package com.example.trabalho_biblioteca.repository;

import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroRepository extends JpaRepository<Livro, Long> {
//   @Query("select l from Livro l where l.titulo like LOWER(:titulo)")
   Livro findByTitulo(String titulo);
}
