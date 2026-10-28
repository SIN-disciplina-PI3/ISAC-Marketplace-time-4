package com.isac.marketplace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "buscas")
public class Busca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String termo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalBusca canal;

    @Column(nullable = false)
    private Integer resultados = 0;

    @Column(name = "telefone_hash", length = 64)
    private String telefoneHash;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Busca() {}

    public Busca(String termo, CanalBusca canal, Integer resultados, String telefoneHash, LocalDateTime criadoEm) {
        this.termo = termo;
        this.canal = canal;
        this.resultados = resultados != null ? resultados : 0;
        this.telefoneHash = telefoneHash;
        this.criadoEm = criadoEm != null ? criadoEm : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(String termo) {
        this.termo = termo;
    }

    public CanalBusca getCanal() {
        return canal;
    }

    public void setCanal(CanalBusca canal) {
        this.canal = canal;
    }

    public Integer getResultados() {
        return resultados;
    }

    public void setResultados(Integer resultados) {
        this.resultados = resultados;
    }

    public String getTelefoneHash() {
        return telefoneHash;
    }

    public void setTelefoneHash(String telefoneHash) {
        this.telefoneHash = telefoneHash;
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
        if (!(o instanceof Busca busca)) return false;
        return Objects.equals(id, busca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
