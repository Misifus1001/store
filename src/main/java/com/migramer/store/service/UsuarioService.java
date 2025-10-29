package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migramer.store.entities.Rol;
import com.migramer.store.entities.Tienda;
import com.migramer.store.entities.Usuario;
import com.migramer.store.exceptions.BusinessException;
import com.migramer.store.exceptions.ResourceNotFoundException;
import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UsuarioDto;
import com.migramer.store.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private TiendaService tiendaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Transactional
    public void guardarUsuario(UsuarioDto usuarioDto, String rolSolicitado, Integer tiendaIdUsuarioActual) {
        Rol rol = rolService.getRolByName(rolSolicitado);

        Tienda tienda = tiendaService.findTiendaById(usuarioDto.getIdTienda());

        if (rolSolicitado.equals("DUEÑO") && !esAdmin(tiendaIdUsuarioActual)) {
            throw new BusinessException("Solo los administradores pueden crear dueños de tienda");
        }

        if (rolSolicitado.equals("DUEÑO") && usuarioRepository.existsDueñoByTiendaId(usuarioDto.getIdTienda())) {
            throw new BusinessException("Ya existe un dueño para esta tienda");
        }

        if (usuarioRepository.findByEmail(usuarioDto.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
        usuario.setEstatus(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setRol(rol);
        usuario.setTienda(tienda);

        usuarioRepository.save(usuario);
    }

    public TokenResponse registrarUsuario(UsuarioDto usuarioDto, String rolSolicitado, Integer tiendaIdUsuarioActual) {
        guardarUsuario(usuarioDto, rolSolicitado, tiendaIdUsuarioActual);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", usuarioDto.getNombre());
        claims.put("rol", rolSolicitado);
        claims.put("tiendaId", usuarioDto.getIdTienda());
        
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtService.generateToken(usuarioDto.getEmail(), claims));
        tokenResponse.setRol(rolSolicitado);
        tokenResponse.setTiendaId(usuarioDto.getIdTienda());
        return tokenResponse;
    }

    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", email));
    }

    public TokenResponse validarUsuario(TokenRequest tokenRequest) {
        Usuario usuario = getUsuarioByEmail(tokenRequest.getEmail());
        
        if (!passwordEncoder.matches(tokenRequest.getPassword(), usuario.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }
        
        if (!usuario.getEstatus()) {
            throw new BusinessException("Usuario deshabilitado");
        }
        
        TokenResponse tokenResponse = new TokenResponse();
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", usuario.getNombre());
        claims.put("rol", usuario.getRol().getNombre());

        if (!usuario.getRol().getNombre().equals("ADMIN")) {
            claims.put("tiendaId", usuario.getTienda().getId());
            tokenResponse.setTiendaId(usuario.getTienda().getId());
        }

        tokenResponse.setToken(jwtService.generateToken(usuario.getEmail(), claims));
        tokenResponse.setRol(usuario.getRol().getNombre());

        tokenResponse.setNombre(usuario.getNombre());
        
        return tokenResponse;
    }

    private boolean esAdmin(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
        return "ADMIN".equals(usuario.getRol().getNombre());
    }
}