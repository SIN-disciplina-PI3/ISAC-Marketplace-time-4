package com.isac.marketplace.repository;

import com.isac.marketplace.model.ImportacaoWhatsApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportacaoWhatsAppRepository extends JpaRepository<ImportacaoWhatsApp, Long> {
    List<ImportacaoWhatsApp> findAllByOrderByCriadoEmDesc();
}
