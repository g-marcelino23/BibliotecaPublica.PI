package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.model.Categoria;
import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.model.User;
import com.example.trabalho_biblioteca.repository.CategoriaRepository;
import com.example.trabalho_biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    LivroService livroService;
    @Autowired
    LivroRepository livroRepository;

    public List<String> getAllCategorias() {
        return categoriaRepository.getNomesCategorias();
    }

    public List<Categoria> getAllCategoriasObj() {
        return categoriaRepository.findAll();
    }

    public Categoria getCategoriaByName(String nomeGenero) {
        return categoriaRepository.findByGeneroIgnoreCase(nomeGenero);
    }

    // ALTERADO: só retorna livros permitidos segundo o usuário logado e classificação indicativa
    public List<Livro> getLivrosByCategoria(String nomeCategoria) {
        Categoria cat = categoriaRepository.findByGeneroIgnoreCase(nomeCategoria);
        User usuario = livroService.getUsuarioLogado();
        int idade = livroService.calcularIdadeHelper(usuario != null ? usuario.getDataNascimento() : null, LocalDate.now());
        List<Livro> livros = livroService.getLivrosByIdCategoria(cat.getId());
        return livros.stream()
                .filter(l -> livroService.podeAcessarLivroHelper(idade, l.getClassificacaoIndicativa()))
                .collect(Collectors.toList());
    }

    // ALTERADO: só retorna livros permitidos do usuário logado em todas as categorias, paginado
    public Page<Livro> getLivrosPagination(Pageable pageable) {
        User usuario = livroService.getUsuarioLogado();
        int idade = livroService.calcularIdadeHelper(usuario != null ? usuario.getDataNascimento() : null, LocalDate.now());
        List<Livro> permitidos = livroRepository.findAll().stream()
                .filter(l -> livroService.podeAcessarLivroHelper(idade, l.getClassificacaoIndicativa()))
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), permitidos.size());
        List<Livro> subList = start < end ? permitidos.subList(start, end) : List.of();
        return new PageImpl<>(subList, pageable, permitidos.size());
    }

    // ALTERADO: só retorna livros permitidos do usuário logado na categoria, paginado
    public Page<Livro> getLivrosPaginationCategoria(String categoria, int page) {
        Categoria cat = categoriaRepository.findByGeneroIgnoreCase(categoria);
        User usuario = livroService.getUsuarioLogado();
        int idade = livroService.calcularIdadeHelper(usuario != null ? usuario.getDataNascimento() : null, LocalDate.now());
        List<Livro> livros = livroService.getLivrosByIdCategoria(cat.getId());
        List<Livro> permitidos = livros.stream()
                .filter(l -> livroService.podeAcessarLivroHelper(idade, l.getClassificacaoIndicativa()))
                .collect(Collectors.toList());
        PageRequest pageRequest = PageRequest.of(page, 5);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), permitidos.size());
        List<Livro> subList = start < end ? permitidos.subList(start, end) : List.of();
        return new PageImpl<>(subList, pageRequest, permitidos.size());
    }
}
