package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.model.User;
import com.example.trabalho_biblioteca.dto.LoginRequestDTO;
import com.example.trabalho_biblioteca.dto.RegisterRequestDTO;
import com.example.trabalho_biblioteca.dto.ResponseDTO;
import com.example.trabalho_biblioteca.infra.security.TokenService;
import com.example.trabalho_biblioteca.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private  UserRepository repository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){
        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body){
        Optional<User> user = this.repository.findByEmail(body.email());

        if(user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            newUser.setRole("ROLE_" + body.role().toUpperCase());

            // TRATE A DATA DE NASCIMENTO!
            if (body.dataNascimento() != null && !body.dataNascimento().isBlank()) {
                // Se vier como String tipo "yyyy-MM-dd" do React
                newUser.setDataNascimento(LocalDate.parse(body.dataNascimento()));
                // Se vier como "dd/MM/yyyy", use:
                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                // newUser.setDataNascimento(LocalDate.parse(body.dataNascimento(), formatter));
            } else {
                return ResponseEntity.badRequest().body("Data de nascimento obrigatória");
            }

            this.repository.save(newUser);
            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }

}
