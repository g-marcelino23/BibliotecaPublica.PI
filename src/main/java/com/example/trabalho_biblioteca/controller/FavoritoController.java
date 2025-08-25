// Local: src/main/java/com/example/trabalho_biblioteca/controller/FavoritoController.java

package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.model.Livro;
import com.example.trabalho_biblioteca.model.User;
import com.example.trabalho_biblioteca.service.FavoritoService;
import com.example.trabalho_biblioteca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livro/favoritos") // <-- ROTA ALTERADA PARA CONSISTÊNCIA
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Livro>> getMeusFavoritos() {
        User usuarioLogado = userService.getUsuarioLogado();
        List<Livro> favoritos = favoritoService.listarFavoritosPorUsuario(usuarioLogado);
        return ResponseEntity.ok(favoritos);
    }

    @PostMapping
    public ResponseEntity<Void> adicionarFavorito(@RequestBody Map<String, Long> payload) {
        Long livroId = payload.get("livroId");
        if (livroId == null) {
            return ResponseEntity.badRequest().build();
        }
        System.out.println("antes de puxar o usuario logado");

            User usuarioLogado = userService.getUsuarioLogado();
            favoritoService.adicionarFavorito(usuarioLogado, livroId);
            return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @DeleteMapping("/{livroId}")
    public ResponseEntity<Void> removerFavorito(@PathVariable Long livroId) {
        User usuarioLogado = userService.getUsuarioLogado();
        favoritoService.removerFavorito(usuarioLogado, livroId);
        return ResponseEntity.noContent().build();
    }
}