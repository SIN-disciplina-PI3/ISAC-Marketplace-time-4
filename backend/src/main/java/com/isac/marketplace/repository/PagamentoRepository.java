package com.isac.marketplace.repository;

import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Pagamento;
import com.isac.marketplace.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByLojaOrderByIdDesc(Loja loja);
    List<Pagamento> findByStatusOrderByIdDesc(StatusPagamento status);
}
