package com.example.trabalho_biblioteca.dto;

public record UpdateUserDTO(
        String newEmail,
        String newName,
        String newPassword,
        String newDataNascimento // formato: "dd/MM/yyyy"
) { }
