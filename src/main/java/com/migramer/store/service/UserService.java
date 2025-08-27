package com.migramer.store.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.User;
import com.migramer.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String registrarUsuario(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();

        claims.put("name", user.getName());

        return jwtService.generateToken(user.getEmail(), claims);

    }

    public String autenticar(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        return jwtService.generateToken(user.getEmail(), Map.of("name", user.getName()));

    }
    
}