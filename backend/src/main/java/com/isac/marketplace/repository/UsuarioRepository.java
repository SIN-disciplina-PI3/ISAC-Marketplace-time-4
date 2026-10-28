package com.isac.marketplace.repository;

import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByLoja(Loja loja);
    boolean existsByEmail(String email);
}
