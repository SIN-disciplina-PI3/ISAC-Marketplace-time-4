package com.isac.marketplace.repository;

import com.isac.marketplace.model.AnuncioImportado;
import com.isac.marketplace.model.StatusCandidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnuncioImportadoRepository extends JpaRepository<AnuncioImportado, Long> {
    List<AnuncioImportado> findByImportacaoIdOrderByIdAsc(Long importacaoId);
    List<AnuncioImportado> findByStatus(StatusCandidato status);
}
