package com.example.trabalho_biblioteca.controller;

import com.example.trabalho_biblioteca.dto.UpdateUserDTO;
import com.example.trabalho_biblioteca.dto.UserDTO;
import com.example.trabalho_biblioteca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping
    public ResponseEntity<String> getUser(){
        return ResponseEntity.ok("sucesso!");
    }

    @GetMapping("/recuperar/{email}")
    public UserDTO recuperarUser(@PathVariable String email){
        return userService.recuperarPorEmail(email);
    }

    @DeleteMapping("email/{email}/senha/{senha}")
    public String deletarUser(@PathVariable String email, @PathVariable String senha){
        return userService.deletarPorEmail(email, senha);
    }

    @PutMapping("{email}")
    public UserDTO alterarUser(@RequestBody UpdateUserDTO updateUserDTO,@PathVariable String email){
        return userService.updateUser(updateUserDTO, email);
    }
}