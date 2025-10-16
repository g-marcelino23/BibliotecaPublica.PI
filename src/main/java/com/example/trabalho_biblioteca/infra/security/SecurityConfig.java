package com.example.trabalho_biblioteca.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                    // 1. Rotas Públicas (permitAll)
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                    .requestMatchers(HttpMethod.GET,"/api/livro/capa/**").permitAll()
                    .requestMatchers(HttpMethod.GET,"/api/livro/download/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/livro/all").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // 2. Rotas de ADMIN (hasRole)
                    .requestMatchers("/api/livro/cadastrar").hasRole("ADMIN")
                    .requestMatchers("/api/livro/deletar/**").hasRole("ADMIN")
                    .requestMatchers("/api/livro/alterar/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/autor").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/autor").hasRole("ADMIN")
                    // 3. Rota de Favoritos (Regra simplificada)
                    .requestMatchers("/api/livro/favoritos/**").authenticated()
                    // 4. Rota de livros permitidos por idade
                    .requestMatchers(HttpMethod.GET, "/api/livro/permitidos").permitAll()
                    // 5. Todas as outras rotas exigem autenticação
                    .anyRequest().authenticated()
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
