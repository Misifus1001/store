package com.migramer.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.migramer.store.entities.Usuario;

// import jakarta.transaction.Transactional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.tienda.id = :tiendaId AND u.rol.nombre = 'PROPIETARIO'")
    boolean existsPropietarioByTiendaId(@Param("tiendaId") Integer tiendaId);

    // @Transactional
    // @Modifying
    // @Query("UPDATE Usuario u SET u.password = :password WHERE u.id = :id")
    // public void changePassword(@Param("password") String password, @Param("id") Integer id);

    long countByRolNombreAndTiendaId(String rolNombre, Integer tiendaId);
}
