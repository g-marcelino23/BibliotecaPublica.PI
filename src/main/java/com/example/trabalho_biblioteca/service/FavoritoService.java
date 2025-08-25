// Local: src/main/java/com/example/trabalho_biblioteca/service/FavoritoService.java
// CÓDIGO CORRIGIDO

package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.model.Favorito;
import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.model.User; // CORRIGIDO
import com.example.trabalho_biblioteca.repository.FavoritoRepository;
import com.example.trabalho_biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Transactional(readOnly = true)
    public List<Livro> listarFavoritosPorUsuario(User user) { // CORRIGIDO
        return favoritoRepository.findByUser(user) // CORRIGIDO (método no repo vai mudar)
                .stream()
                .map(Favorito::getLivro)
                .collect(Collectors.toList());
    }

    @Transactional
    public Favorito adicionarFavorito(User user, Long livroId) { // CORRIGIDO
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Erro: Livro não encontrado!"));

        if (favoritoRepository.existsByUserAndLivro(user, livro)) { // CORRIGIDO
            throw new RuntimeException("Este livro já está nos seus favoritos.");
        }

        Favorito novoFavorito = new Favorito();
        novoFavorito.setUser(user); // CORRIGIDO
        novoFavorito.setLivro(livro);

        return favoritoRepository.save(novoFavorito);
    }

    @Transactional
    public void removerFavorito(User user, Long livroId) { // CORRIGIDO
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Erro: Livro não encontrado!"));

        Favorito favorito = favoritoRepository.findByUserAndLivro(user, livro) // CORRIGIDO
                .orElseThrow(() -> new RuntimeException("Erro: Este livro não está na sua lista de favoritos."));

        favoritoRepository.delete(favorito);
    }
}