package com.migramer.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.migramer.store.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.tienda.id = :tiendaId AND u.rol.nombre = 'DUEÑO'")
    boolean existsDueñoByTiendaId(@Param("tiendaId") Integer tiendaId);

    long countByRolNombreAndTiendaId(String rolNombre, Integer tiendaId);
}
