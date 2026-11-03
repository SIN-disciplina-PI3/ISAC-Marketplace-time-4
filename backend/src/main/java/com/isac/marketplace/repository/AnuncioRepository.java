package com.isac.marketplace.repository;

import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAnuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long>, JpaSpecificationExecutor<Anuncio> {

    long countByLojaAndStatus(Loja loja, StatusAnuncio status);

    long countByStatus(StatusAnuncio status);

    List<Anuncio> findByStatusOrderByDataPublicacaoDesc(StatusAnuncio status);

    List<Anuncio> findAllByOrderByDataPublicacaoDesc();

    List<Anuncio> findByLoja(Loja loja);

    List<Anuncio> findByLojaOrderByDataPublicacaoDesc(Loja loja);

    List<Anuncio> findByStatusAndDataExpiracaoBefore(StatusAnuncio status, LocalDateTime data);

    List<Anuncio> findByStatusAndAvisoExpiracaoEnviadoFalseAndDataExpiracaoBetween(
            StatusAnuncio status, LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT a FROM Anuncio a WHERE a.status = :status AND a.disponivel = true AND a.dataExpiracao > :agora")
    List<Anuncio> findAnunciosAtivosEDisponiveis(
            @Param("status") StatusAnuncio status,
            @Param("agora") LocalDateTime agora
    );
}
