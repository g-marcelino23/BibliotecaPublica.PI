package com.example.trabalho_biblioteca.repository;

import com.example.trabalho_biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
     Autor findByNome(String nome);
}
