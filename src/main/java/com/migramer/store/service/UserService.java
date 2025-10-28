package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migramer.store.entities.User;
import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UserDto;
import com.migramer.store.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public void guardarUsuario(UserDto userDto){
        User user = new User();
        user.setEmail(userDto.getCorreo());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setName(userDto.getNombre());
        user.setFechaCreacion(LocalDateTime.now());
        user.setActivo(true);
        userRepository.save(user);
    }

    public TokenResponse registrarUsuario(UserDto userDto){

        guardarUsuario(userDto);
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userDto.getNombre());
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtService.generateToken(userDto.getCorreo(), claims));
        return tokenResponse;
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email).get();
        return user;
    }

    public TokenResponse validarUsuario(TokenRequest tokenRequest){

        User user = getUserByEmail(tokenRequest.getEmail());

        if (!passwordEncoder.matches(tokenRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtService.generateToken(user.getEmail(), Map.of("name", user.getName())));
        return tokenResponse;
    }
    
}