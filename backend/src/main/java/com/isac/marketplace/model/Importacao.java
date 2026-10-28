package com.isac.marketplace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "importacoes")
public class Importacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "arquivo_nome", nullable = false)
    private String arquivoNome;

    @Column(name = "total_mensagens", nullable = false)
    private Integer totalMensagens = 0;

    @Column(name = "total_candidatos", nullable = false)
    private Integer totalCandidatos = 0;

    @Column(nullable = false, length = 30)
    private String status = "CONCLUIDO";

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Importacao() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArquivoNome() {
        return arquivoNome;
    }

    public void setArquivoNome(String arquivoNome) {
        this.arquivoNome = arquivoNome;
    }

    public Integer getTotalMensagens() {
        return totalMensagens;
    }

    public void setTotalMensagens(Integer totalMensagens) {
        this.totalMensagens = totalMensagens;
    }

    public Integer getTotalCandidatos() {
        return totalCandidatos;
    }

    public void setTotalCandidatos(Integer totalCandidatos) {
        this.totalCandidatos = totalCandidatos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Importacao that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
