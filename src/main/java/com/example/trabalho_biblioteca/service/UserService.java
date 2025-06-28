package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.dto.UpdateUserDTO;
import com.example.trabalho_biblioteca.dto.UserDTO;
import com.example.trabalho_biblioteca.infra.security.TokenService;
import com.example.trabalho_biblioteca.model.User;
import com.example.trabalho_biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    TokenService tokenService;

    public UserDTO recuperarPorEmail(String email){
        User userRecuperado = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        return new UserDTO(userRecuperado.getName(), userRecuperado.getEmail());
    }

    public String deletarPorEmail(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        try{
            if(encoder.matches(password, user.getPassword())){
                userRepository.deleteByEmail(email);
            }
        }catch (RuntimeException e){
            return e.getMessage();
        }
        return "usuário apagado com sucesso!";
    }

    public String updateUser(UpdateUserDTO updateUserDTO, String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));

        if(updateUserDTO.newEmail() != null){
            user.setEmail(updateUserDTO.newEmail());
        }
        if(updateUserDTO.newName() != null){
            user.setName(updateUserDTO.newName());
        }
        if(updateUserDTO.newPassword() != null){
            String novaSenha = encoder.encode(updateUserDTO.newPassword());
            user.setPassword(novaSenha);
        }
        userRepository.save(user);
        return tokenService.generateToken(user);

    }

    public boolean compararSenhas(String senhaDigitada, String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("user not found"));
        if(encoder.matches(senhaDigitada, user.getPassword())){
            return true;
        }
        return false;
    }
}
