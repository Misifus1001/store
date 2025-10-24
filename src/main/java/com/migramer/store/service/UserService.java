package com.migramer.store.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migramer.store.entities.User;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setName(userDto.getNombre());
        user.setActive(true);
        userRepository.save(user);
    }

    public String registrarUsuario(UserDto userDto){

        guardarUsuario(userDto);

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userDto.getNombre());

        return jwtService.generateToken(userDto.getCorreo(), claims);
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email).get();
        return user;
    }

    public String validarUsuario(String email, String password){

        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }
        return jwtService.generateToken(user.getEmail(), Map.of("name", user.getName()));
    }
    
}