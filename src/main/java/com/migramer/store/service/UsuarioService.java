package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.migramer.store.models.PaginacionResponse;
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
    public void guardarUsuario(UsuarioDto usuarioDto, String rolSolicitado, String uuidTienda) {

        validarUsuarioExistente(usuarioDto.getEmail());

        Rol rol = rolService.getRolByName(rolSolicitado);
        Tienda tienda = tiendaService.getTiendaEntityByUUID(uuidTienda);

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

    private void validarUsuarioExistente(String email) {
        Optional<Usuario> usuarioFind = usuarioRepository.findByEmail(email);

        if (usuarioFind.isPresent()) {
            throw new BusinessException("Ya existe un usuario asociado a ese correo");
        }
    }

    public TokenResponse registrarUsuario(UsuarioDto usuarioDto, String rolSolicitado, String uuidTienda) {
        guardarUsuario(usuarioDto, rolSolicitado, uuidTienda);
        return generarTokenResponse(usuarioDto.getEmail(), usuarioDto.getNombre(), rolSolicitado, uuidTienda);
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

        return generarTokenResponse(usuario.getEmail(), usuario.getNombre(), usuario.getRol().getNombre(),
                usuario.getTienda().getUuid());

    }

    public PaginacionResponse getEmpleadosByTienda(Integer page, Integer size, String uuidTienda) {
        return getUsuariosByTiendaAndRol(page, size, "VENDEDOR", uuidTienda);
    }

    public PaginacionResponse getAllUsersByTienda(Integer page, Integer size, String uuidTienda) {
        return getUsuariosByTiendaAndRol(page, size, "PROPIETARIO", uuidTienda);
    }

    public PaginacionResponse getUsuariosByTiendaAndRol(Integer page, Integer size, String rolName, String uuidTienda) {

        try {

            Tienda tienda = tiendaService.getTiendaEntityByUUID(uuidTienda);

            Rol rol = rolService.getRolByName(rolName);

            PaginacionResponse paginacionResponse = new PaginacionResponse();

            Pageable pageable = PageRequest.of(page, size);

            Page<Usuario> usuarioPage = usuarioRepository.findAllByTiendaAndRol(tienda, rol, pageable);

            Page<UsuarioDto> usuarioDtoPage = usuarioPageToUsuarioDtoPage(usuarioPage);

            paginacionResponse.setItems(usuarioDtoPage.getContent());
            paginacionResponse.setTotalItems(usuarioDtoPage.getTotalElements());
            paginacionResponse.setTotalPages(usuarioDtoPage.getTotalPages());
            paginacionResponse.setCurrentPage(usuarioDtoPage.getNumber());
            paginacionResponse.setPreviousPage(
                    usuarioDtoPage.getNumber() > 0 ? usuarioDtoPage.getNumber() - 1 : usuarioDtoPage.getNumber());
            paginacionResponse.setNextPage(
                    usuarioDtoPage.getNumber() + 1 < usuarioDtoPage.getTotalPages() ? usuarioDtoPage.getNumber() + 1
                            : usuarioDtoPage.getNumber());

            return paginacionResponse;
        } catch (Exception e) {
            logger.error("Ocurrió un error: {}", e);
            throw new RuntimeException(e);
        }
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
        return generarTokenResponse(usuario.getEmail(), usuario.getNombre(), usuario.getRol().getNombre(),
                usuario.getTienda().getUuid());
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

    private Page<UsuarioDto> usuarioPageToUsuarioDtoPage(Page<Usuario> usuarioPage) {
        return usuarioPage.map(usuario -> usuarioToUsuarioDto(usuario));
    }

    private UsuarioDto usuarioToUsuarioDto(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setEmail(usuario.getEmail());

        return usuarioDto;
    }

    private TokenResponse generarTokenResponse(String email, String nombre, String rol, String uuidTienda) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", nombre);
        claims.put("rol", rol);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtService.generateToken(email, claims));
        tokenResponse.setRol(rol);
        tokenResponse.setUuidTienda(uuidTienda);
        tokenResponse.setNombre(nombre);
        return tokenResponse;
    }
}
