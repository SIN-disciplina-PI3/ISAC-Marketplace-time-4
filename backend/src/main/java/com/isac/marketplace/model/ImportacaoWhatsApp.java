package com.isac.marketplace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma importação de arquivo de exportação de grupo WhatsApp (.txt).
 * Cada importação gera candidatos a anúncio que passam por revisão manual.
 */
@Entity
@Table(name = "importacoes_whatsapp")
public class ImportacaoWhatsApp {

    public enum StatusImportacao {
        PROCESSANDO, CONCLUIDA, ERRO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    @Column(name = "tamanho_bytes", nullable = false)
    private long tamanhoBytes;

    @Column(name = "total_mensagens")
    private int totalMensagens;

    @Column(name = "total_candidatos")
    private int totalCandidatos;

    @Column(name = "aprovados")
    private int aprovados;

    @Column(name = "descartados")
    private int descartados;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusImportacao status = StatusImportacao.PROCESSANDO;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "processado_em")
    private LocalDateTime processadoEm;

    @OneToMany(mappedBy = "importacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidatoAnuncio> candidatos = new ArrayList<>();

    // ---- getters e setters ----

    public Long getId() { return id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }
    public int getTotalMensagens() { return totalMensagens; }
    public void setTotalMensagens(int totalMensagens) { this.totalMensagens = totalMensagens; }
    public int getTotalCandidatos() { return totalCandidatos; }
    public void setTotalCandidatos(int totalCandidatos) { this.totalCandidatos = totalCandidatos; }
    public int getAprovados() { return aprovados; }
    public void setAprovados(int aprovados) { this.aprovados = aprovados; }
    public int getDescartados() { return descartados; }
    public void setDescartados(int descartados) { this.descartados = descartados; }
    public StatusImportacao getStatus() { return status; }
    public void setStatus(StatusImportacao status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getProcessadoEm() { return processadoEm; }
    public void setProcessadoEm(LocalDateTime processadoEm) { this.processadoEm = processadoEm; }
    public List<CandidatoAnuncio> getCandidatos() { return candidatos; }
}
