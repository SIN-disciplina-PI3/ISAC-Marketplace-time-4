package com.isac.marketplace.repository;

import com.isac.marketplace.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNomeIgnoreCase(String nome);
    List<Categoria> findByAtivaTrueOrderByNomeAsc();
    boolean existsByNomeIgnoreCase(String nome);
}
