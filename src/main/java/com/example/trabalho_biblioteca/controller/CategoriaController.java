package com.example.trabalho_biblioteca.controller;


import com.example.trabalho_biblioteca.model.Categoria;
import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {
    @Autowired
    CategoriaService categoriaService;

    @GetMapping()
    public List<String> todasCategorias(){
        return categoriaService.getAllCategorias();
    }

    @GetMapping("/getObjetoCategoria")
    public List<Categoria> todasCategoriasObj(){
        return categoriaService.getAllCategoriasObj();
    }

    @GetMapping("/nome")
    public Categoria getByName(@RequestParam("genero") String nomeGenero){
        return categoriaService.getCategoriaByName(nomeGenero);
    }

    @GetMapping("/livros")
    public List<Livro> getLivrosByCategoria(@RequestParam("categoria") String nomeCategoria){
        return categoriaService.getLivrosByCategoria(nomeCategoria);
    }

    @GetMapping("/livrosPagination")
    public Page<Livro> getLivrosPagination(@PageableDefault(value = 5) Pageable pageable){
        return categoriaService.getLivrosPagination(pageable);
    }

    @GetMapping("/livrosPagination/categoria")
    public Page<Livro> getLivrosPaginationCategoria(@RequestParam("categoria") String categoria, @RequestParam("page") int page){
        return categoriaService.getLivrosPaginationCategoria(categoria, page);
    }
}
