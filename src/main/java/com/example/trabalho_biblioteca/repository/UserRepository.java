package com.example.trabalho_biblioteca.repository;

import com.example.trabalho_biblioteca.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("delete from User u where u.email = :email")
    void deleteByEmail(String email);
}
