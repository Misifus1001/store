package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migramer.store.components.PasswordGenerator;
import com.migramer.store.emailprovider.models.EmailRequest;
import com.migramer.store.emailprovider.service.EmailProvider;
import com.migramer.store.entities.Rol;
import com.migramer.store.entities.Tienda;
import com.migramer.store.entities.Usuario;
import com.migramer.store.exceptions.BusinessException;
import com.migramer.store.exceptions.ResourceNotFoundException;
import com.migramer.store.models.ChangePasswordRequest;
// import com.migramer.store.models.EmailRequest;
import com.migramer.store.models.RecuperarPasswordResponse;
import com.migramer.store.models.RecuperarPaswordRequest;
import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UsuarioDto;
// import com.migramer.store.providers.EmailProvider;
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

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private EmailProvider emailProvider;

    @Autowired
    @Lazy
    private UsuarioService self;

    private final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Transactional
    public void guardarUsuario(UsuarioDto usuarioDto, String rolSolicitado, Integer tiendaIdUsuarioActual) {
        Rol rol = rolService.getRolByName(rolSolicitado);

        Tienda tienda = tiendaService.findTiendaById(usuarioDto.getIdTienda());

        if (rolSolicitado.equals("PROPIETARIO") && !esAdmin(tiendaIdUsuarioActual)) {
            throw new BusinessException("Solo los administradores pueden crear propietarios de tienda");
        }

        if (rolSolicitado.equals("PROPIETARIO") && usuarioRepository.existsPropietarioByTiendaId(usuarioDto.getIdTienda())) {
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

    @Transactional
    public void actualizarUsuario(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public void enviarPasswordByEmail(String emailTo, String password){
        self.ejecutarEnviarPasswordByEmail(emailTo,password);
    }

    @Async
    public void ejecutarEnviarPasswordByEmail(String emailTo, String password){
        com.migramer.store.emailprovider.models.EmailRequest emailRequest = new EmailRequest();
        emailRequest.setMessage("Tu nueva contraseña es: "+ password);
        emailRequest.setEmailTo(emailTo);
        emailRequest.setSubject("REESTABLECIMIENTO DE CONTRASEÑA");
        logger.info("password: {}",password);
        emailProvider.sendEmail(emailRequest);
    }

    public RecuperarPasswordResponse callReestablecerPassword(RecuperarPaswordRequest recuperarPasword){
        String password = passwordGenerator.generatePassword();
        reestablecerPassword(recuperarPasword.getEmail(), password);
        enviarPasswordByEmail(recuperarPasword.getEmail(), password);
        return new RecuperarPasswordResponse("La nueva contraseña ha sido enviada a tu correo");
    }

    public void reestablecerPassword(String email, String password) {
        self.ejecutarReestablecerPassword(email, password);
    }

    @Async
    public void ejecutarReestablecerPassword(String email, String password) {
        Usuario usuario = getUsuarioByEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        actualizarUsuario(usuario);
    }

    public TokenResponse changePassword(ChangePasswordRequest changePasswordRequest){

        Usuario usuario = getUsuarioByEmail(changePasswordRequest.getEmail());
        
        if (!passwordEncoder.matches(changePasswordRequest.getPassword(), usuario.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }
        
        if (!usuario.getEstatus()) {
            throw new BusinessException("Usuario deshabilitado");
        }

        if (changePasswordRequest.getPassword().equals(changePasswordRequest.getNewPassword())) {
            throw new BusinessException("La contraseña no puede ser la misma");
            
        }

        usuario.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        actualizarUsuario(usuario);
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