// Local: src/main/java/com/example/trabalho_biblioteca/repository/FavoritoRepository.java
// VERIFIQUE ESTE CÓDIGO

package com.example.trabalho_biblioteca.repository;

import com.example.trabalho_biblioteca.model.Favorito;
import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.model.User; // Import para User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    // O Service está procurando por este método. O nome e o parâmetro (User user) devem ser idênticos.
    List<Favorito> findByUser(User user);

    // Verifique este também
    Optional<Favorito> findByUserAndLivro(User user, Livro livro);

    // E este
    boolean existsByUserAndLivro(User user, Livro livro);
}