package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.FirebaseEntityToken;
import com.migramer.store.entities.Usuario;
import com.migramer.store.models.MessageResponse;
import com.migramer.store.repository.FirebaseEntityTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class FirebaseEntityTokenService {

    @Autowired
    private FirebaseEntityTokenRepository firebaseEntityTokenRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    @Lazy
    private FirebaseEntityTokenService self;

    public MessageResponse guardarToken(String token, String email) {
        guardarTokenUsuario(token, email);
        return new MessageResponse("El guardado está siendo procesado en segundo plano");
    }

    public void guardarTokenUsuario(String token, String email) {
        self.executeGuardarTokenUsuario(token, email);
    }

    @Async
    public void executeGuardarTokenUsuario(String token, String email) {
        Usuario usuario = usuarioService.getUsuarioByEmail(email);
        saveFirebaseToken(token, usuario);
    }

    @Transactional
    public void saveFirebaseToken(String token, Usuario usuario) {

        Optional<FirebaseEntityToken> firebaseEntityTokenOptional = firebaseEntityTokenRepository
                .findTop1ByUsuarioToken(usuario);

        FirebaseEntityToken firebaseEntityToken;
        if (firebaseEntityTokenOptional.isEmpty()) {

            firebaseEntityToken = new FirebaseEntityToken();
        } else {
            firebaseEntityToken = firebaseEntityTokenOptional.get();
        }

        firebaseEntityToken.setFechaCreacion(LocalDateTime.now());
        firebaseEntityToken.setActivo(true);
        firebaseEntityToken.setToken(token);
        firebaseEntityToken.setUsuarioToken(usuario);

        firebaseEntityTokenRepository.save(firebaseEntityToken);

    }

    public List<FirebaseEntityToken> getFirebaseEntityTokenList() {
        return firebaseEntityTokenRepository.findAllByActivo(true);
    }

}