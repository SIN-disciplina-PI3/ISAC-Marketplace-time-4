package com.isac.marketplace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "conversas_whatsapp")
public class ConversaWhatsapp {

    @Id
    @Column(name = "telefone_hash", length = 64)
    private String telefoneHash;

    @Column(nullable = false, length = 50)
    private String etapa;

    @Column(name = "dados_parciais", columnDefinition = "TEXT")
    private String dadosParciais;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public ConversaWhatsapp() {}

    public ConversaWhatsapp(String telefoneHash, String etapa, String dadosParciais, LocalDateTime atualizadoEm) {
        this.telefoneHash = telefoneHash;
        this.etapa = etapa;
        this.dadosParciais = dadosParciais;
        this.atualizadoEm = atualizadoEm != null ? atualizadoEm : LocalDateTime.now();
    }

    public String getTelefoneHash() {
        return telefoneHash;
    }

    public void setTelefoneHash(String telefoneHash) {
        this.telefoneHash = telefoneHash;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getDadosParciais() {
        return dadosParciais;
    }

    public void setDadosParciais(String dadosParciais) {
        this.dadosParciais = dadosParciais;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversaWhatsapp that)) return false;
        return Objects.equals(telefoneHash, that.telefoneHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(telefoneHash);
    }
}
