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
import com.migramer.store.entities.Rol;
import com.migramer.store.entities.Tienda;
import com.migramer.store.entities.Usuario;
import com.migramer.store.exceptions.BusinessException;
import com.migramer.store.exceptions.ResourceNotFoundException;
import com.migramer.store.models.ChangePasswordRequest;
import com.migramer.store.models.RecuperarPasswordResponse;
import com.migramer.store.models.RecuperarPaswordRequest;
import com.migramer.store.models.TokenRequest;
import com.migramer.store.models.TokenResponse;
import com.migramer.store.models.UsuarioDto;
import com.migramer.store.providers.emailprovider.models.EmailRequest;
import com.migramer.store.providers.emailprovider.models.TypeHtmlBody;
import com.migramer.store.providers.emailprovider.service.EmailProvider;
import com.migramer.store.providers.webhook.WebHookService;
import com.migramer.store.providers.webhook.model.NameNotification;
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

    @Autowired
    private WebHookService webHookService;

    private final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Transactional
    public void guardarUsuario(UsuarioDto usuarioDto, String rolSolicitado, Integer tiendaIdUsuarioActual) {
        Rol rol = rolService.getRolByName(rolSolicitado);
        Tienda tienda = tiendaService.findTiendaById(usuarioDto.getIdTienda());

        validarCreacionUsuario(usuarioDto, rolSolicitado, tiendaIdUsuarioActual);

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

    private void validarCreacionUsuario(UsuarioDto usuarioDto, String rolSolicitado, Integer tiendaIdUsuarioActual) {
        if (rolSolicitado.equals("PROPIETARIO") && !esAdmin(tiendaIdUsuarioActual)) {
            throw new BusinessException("Solo los administradores pueden crear propietarios de tienda");
        }

        if (rolSolicitado.equals("PROPIETARIO")
                && usuarioRepository.existsPropietarioByTiendaId(usuarioDto.getIdTienda())) {
            throw new BusinessException("Ya existe un dueño para esta tienda");
        }

        if (usuarioRepository.findByEmail(usuarioDto.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
    }

    public TokenResponse registrarUsuario(UsuarioDto usuarioDto, String rolSolicitado, Integer tiendaIdUsuarioActual) {
        guardarUsuario(usuarioDto, rolSolicitado, tiendaIdUsuarioActual);
        return generarTokenResponse(usuarioDto.getEmail(), usuarioDto.getNombre(), rolSolicitado, usuarioDto.getIdTienda());
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

        return generarTokenResponse(usuario);
    }

    @Transactional
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
        notificateUser("8115df28-69dc-4e8c-a06f-4d53128ed39a");
    }

    public void enviarPasswordByEmail(String emailTo, String password) {
        self.ejecutarEnviarPasswordByEmail(emailTo, password);
    }

    @Async
    public void ejecutarEnviarPasswordByEmail(String emailTo, String password) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setMessage("Tu nueva contraseña es: " + password);
        emailRequest.setEmailTo(emailTo);
        emailRequest.setSubject("REESTABLECIMIENTO DE CONTRASEÑA");
        logger.info("password: {}", password);
        emailProvider.sendEmail(emailRequest, TypeHtmlBody.RESET_PASSWORD);
    }

    @Async
    private void notificateUser(String uuuidTienda) {
        webHookService.sendNotificationChanges(NameNotification.USUARIO, uuuidTienda);
    }

    public RecuperarPasswordResponse callReestablecerPassword(RecuperarPaswordRequest recuperarPasword) {
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

    public TokenResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        Usuario usuario = getUsuarioByEmail(changePasswordRequest.getEmail());

        validarCambioPassword(changePasswordRequest, usuario);

        usuario.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        actualizarUsuario(usuario);

        return generarTokenResponse(usuario);
    }

    private void validarCambioPassword(ChangePasswordRequest changePasswordRequest, Usuario usuario) {
        if (!passwordEncoder.matches(changePasswordRequest.getPassword(), usuario.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }
        if (!usuario.getEstatus()) {
            throw new BusinessException("Usuario deshabilitado");
        }
        if (changePasswordRequest.getPassword().equals(changePasswordRequest.getNewPassword())) {
            throw new BusinessException("La contraseña no puede ser la misma");
        }
    }

    private boolean esAdmin(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
        return "ADMIN".equals(usuario.getRol().getNombre());
    }

    private TokenResponse generarTokenResponse(Usuario usuario) {
        String rol = usuario.getRol().getNombre();
        Integer tiendaId = rol.equals("ADMIN") ? null : usuario.getTienda().getId();

        String uuidTienda = usuario.getTienda().getUuid();

        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", usuario.getNombre());
        claims.put("rol", rol);
        if (tiendaId != null) claims.put("tiendaId", tiendaId);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtService.generateToken(usuario.getEmail(), claims));
        tokenResponse.setRol(rol);
        tokenResponse.setNombre(usuario.getNombre());
        tokenResponse.setTiendaId(tiendaId);
        tokenResponse.setUuidTienda(uuidTienda);
        return tokenResponse;
    }

    private TokenResponse generarTokenResponse(String email, String nombre, String rol, Integer tiendaId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", nombre);
        claims.put("rol", rol);
        if (!"ADMIN".equals(rol)) claims.put("tiendaId", tiendaId);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtService.generateToken(email, claims));
        tokenResponse.setRol(rol);
        tokenResponse.setTiendaId(tiendaId);
        tokenResponse.setNombre(nombre);
        return tokenResponse;
    }
}
