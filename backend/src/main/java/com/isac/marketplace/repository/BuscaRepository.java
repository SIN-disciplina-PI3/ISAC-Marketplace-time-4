package com.isac.marketplace.repository;

import com.isac.marketplace.model.Busca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuscaRepository extends JpaRepository<Busca, Long> {

    List<Busca> findTop20ByOrderByCriadoEmDesc();

    @Query("SELECT b.termo, COUNT(b.id) AS total FROM Busca b GROUP BY b.termo ORDER BY COUNT(b.id) DESC")
    List<Object[]> findTermosMaisBuscados();

    @Query("SELECT b.termo, COUNT(b.id) AS total FROM Busca b WHERE b.resultados = 0 GROUP BY b.termo ORDER BY COUNT(b.id) DESC")
    List<Object[]> findBuscasSemResultado();
}
