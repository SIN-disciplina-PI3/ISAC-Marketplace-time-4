package com.isac.marketplace.repository;

import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.PlanoLoja;
import com.isac.marketplace.model.StatusAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {
    Optional<Loja> findByDocumento(String documento);
    Optional<Loja> findByTelefoneWhatsapp(String telefoneWhatsapp);
    boolean existsByDocumento(String documento);
    boolean existsByTelefoneWhatsapp(String telefoneWhatsapp);
    List<Loja> findByStatusAssinatura(StatusAssinatura statusAssinatura);
    List<Loja> findByPlano(PlanoLoja plano);
    List<Loja> findByPlanoAndStatusAssinatura(PlanoLoja plano, StatusAssinatura statusAssinatura);
    List<Loja> findByDataVencimentoAssinaturaBeforeAndStatusAssinatura(LocalDateTime data, StatusAssinatura statusAssinatura);
}
