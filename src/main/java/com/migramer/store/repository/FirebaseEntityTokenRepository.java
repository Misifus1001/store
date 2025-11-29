package com.migramer.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.migramer.store.entities.FirebaseEntityToken;
import com.migramer.store.entities.Usuario;
import java.util.List;



public interface FirebaseEntityTokenRepository extends JpaRepository<FirebaseEntityToken, Integer>{

    Optional<FirebaseEntityToken> findTop1ByUsuarioToken(Usuario usuarioToken);

    List<FirebaseEntityToken> findAllByActivo(Boolean activo);

}