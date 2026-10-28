package com.isac.marketplace.repository;

import com.isac.marketplace.model.ConversaWhatsapp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConversaWhatsappRepository extends JpaRepository<ConversaWhatsapp, String> {

    @Modifying
    @Query("DELETE FROM ConversaWhatsapp c WHERE c.atualizadoEm < :limite")
    void deleteByAtualizadoEmBefore(@Param("limite") LocalDateTime limite);
}
