package com.example.trabalho_biblioteca.mapper;

import com.example.trabalho_biblioteca.dto.LivroDTO;
import com.example.trabalho_biblioteca.model.Livro;

public class LivroMapper {
    public static Livro dtoPraLivro(LivroDTO livroDto){
        Livro livro = new Livro();
        livro.setAutor(livroDto.autor());
        livro.setTitulo(livroDto.titulo());
        livro.setDescricao(livroDto.descricao());
        return livro;
    }

}
