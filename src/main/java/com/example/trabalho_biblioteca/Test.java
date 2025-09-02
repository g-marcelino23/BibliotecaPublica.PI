package com.example.trabalho_biblioteca;

import com.example.trabalho_biblioteca.model.Categoria;
import com.example.trabalho_biblioteca.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Test implements CommandLineRunner {
    @Autowired
    CategoriaRepository categoriaRepository;

    @Override
    public void run(String... args) throws Exception {
        Categoria cat1 = new Categoria("Romance");
        Categoria cat2 = new Categoria("Aventura");
        Categoria cat3 = new Categoria("Fantasia");
        Categoria cat4 = new Categoria("Ficção Científica");
        Categoria cat5 = new Categoria("Terror");
        Categoria cat6 = new Categoria("Suspense");
        Categoria cat7 = new Categoria("Distopia");
        Categoria cat8 = new Categoria("Biografia");
        Categoria cat9 = new Categoria("História");
        Categoria cat10 = new Categoria("Ciências");
        Categoria cat11 = new Categoria("Filosofia");
        Categoria cat12 = new Categoria("Autoajuda");
        Categoria cat13 = new Categoria("Religião");
        Categoria cat14 = new Categoria("Infantil");
        Categoria cat15 = new Categoria("Infantojuvenil");
        Categoria cat16 = new Categoria("Juvenil");
        Categoria cat17 = new Categoria("Poesia");
        Categoria cat18 = new Categoria("Teatro");
        Categoria cat19 = new Categoria("Contos");
        Categoria cat20 = new Categoria("Crônicas");
        Categoria cat21 = new Categoria("Geografia");
        Categoria cat22 = new Categoria("Português");
        Categoria cat23 = new Categoria("Matemática");
        Categoria cat24 = new Categoria("Geometria");
        Categoria cat25 = new Categoria("Física");
        Categoria cat26 = new Categoria("Química");
        Categoria cat27 = new Categoria("Biologia");


        categoriaRepository.saveAll(Arrays.asList(cat1, cat2, cat3, cat4, cat5, cat6,cat7, cat8, cat9, cat10, cat11, cat12, cat13, cat14, cat15, cat16, cat17, cat18, cat19, cat20, cat21, cat22, cat23, cat24, cat25, cat26, cat27));


    }
}
