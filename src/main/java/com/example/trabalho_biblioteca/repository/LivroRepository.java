package com.example.trabalho_biblioteca.repository;

import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
