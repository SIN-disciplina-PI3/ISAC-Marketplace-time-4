package com.isac.marketplace.repository;

import com.isac.marketplace.model.Conteudo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {
    Optional<Conteudo> findByChave(String chave);
    boolean existsByChave(String chave);
}
