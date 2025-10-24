package com.migramer.store.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migramer.store.entities.Usuario;
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
        Usuario usuario = new Usuario();
        usuario.setEmail(userDto.getCorreo());
        usuario.setPassword(passwordEncoder.encode(userDto.getPassword()));
        usuario.setNombre(userDto.getNombre());
        usuario.setEstatus(true);
        usuario.setFechaCreacion(java.time.LocalDateTime.now());
        // Si necesitas asignar rol o tienda, hazlo aquí
        userRepository.save(usuario);
    }

    public String registrarUsuario(UserDto userDto){
        guardarUsuario(userDto);
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", userDto.getNombre());
        return jwtService.generateToken(userDto.getCorreo(), claims);
    }

    public Usuario getUsuarioByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public String validarUsuario(String email, String password){
        Usuario usuario = getUsuarioByEmail(email);
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }
        return jwtService.generateToken(usuario.getEmail(), Map.of("nombre", usuario.getNombre()));
    }
}