package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.model.Categoria;
import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.repository.CategoriaRepository;
import com.example.trabalho_biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoriaService {
    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    LivroService livroService;
    @Autowired
    LivroRepository livroRepository;
    public List<String> getAllCategorias(){
        return categoriaRepository.getNomesCategorias();
    }

    public List<Categoria> getAllCategoriasObj(){
        return categoriaRepository.findAll();
    }

    public Categoria getCategoriaByName(String nomeGenero){
        return categoriaRepository.findByGeneroIgnoreCase(nomeGenero);
    }

    public List<Livro> getLivrosByCategoria(String nomeCategoria){
        return livroService.getLivrosByIdCategoria(categoriaRepository.findByGeneroIgnoreCase(nomeCategoria).getId());
    }

    public Page<Livro> getLivrosPagination(Pageable pageable){
        return livroRepository.findAll(pageable);
    }

    public Page<Livro> getLivrosPaginationCategoria(String categoria, int page){
        Categoria cat = categoriaRepository.findByGeneroIgnoreCase(categoria);
        return livroService.getLivrosPaginationCategoria(cat.getId(), page);
    }
}
