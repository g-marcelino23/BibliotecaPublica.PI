// Local: com/example/trabalho_biblioteca/service/UserService.java

package com.example.trabalho_biblioteca.service;

import com.example.trabalho_biblioteca.dto.UpdateUserDTO;
import com.example.trabalho_biblioteca.dto.UserDTO;
import com.example.trabalho_biblioteca.infra.security.TokenService;
import com.example.trabalho_biblioteca.infra.security.UserDetailsImpl;
import com.example.trabalho_biblioteca.model.User;
import com.example.trabalho_biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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


    public User getUsuarioLogado() {
        // Pega a autenticação do usuário que foi validada pelo SecurityFilter
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Nenhum usuário autenticado encontrado.");
        }

        // Pega os detalhes do usuário a partir do principal da autenticação
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = userDetails.getUser().getEmail(); // O getUsername() aqui retorna o email
       
        // Busca o usuário completo no banco de dados com o email do token
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário com email '" + userEmail + "' não encontrado no banco de dados!"));
    }
}