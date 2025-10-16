package com.example.trabalho_biblioteca.repository;

import com.example.trabalho_biblioteca.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
   Livro findByTitulo(String titulo);

   List<Livro> findByCategoriaId(long idCategoria);

   @Query(value = "select l.* from Livro l where l.categoria_id = :idCategoria", nativeQuery = true)
   Page<Livro> getLivrosByCategoria(long idCategoria, PageRequest page);

   @Query("select l from Livro l where l.autor.nome like :nomeAutor")
   List<Livro> getLivrosByAutor(@Param("nomeAutor") String nomeAutor);
}
