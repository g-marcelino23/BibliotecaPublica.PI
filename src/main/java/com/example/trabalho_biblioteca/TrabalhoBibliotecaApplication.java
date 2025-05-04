package com.example.trabalho_biblioteca;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class TrabalhoBibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrabalhoBibliotecaApplication.class, args);
	}

}
