package com.isac.marketplace.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Candidato a anúncio extraído de uma importação de chat WhatsApp.
 * Aguarda revisão manual antes de virar um anúncio real.
 */
@Entity
@Table(name = "candidatos_anuncio")
public class CandidatoAnuncio {

    public enum StatusCandidato {
        PENDENTE_REVISAO, APROVADO, DESCARTADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "importacao_id", nullable = false)
    private ImportacaoWhatsApp importacao;

    /** SHA-256 do número do remetente — LGPD: nunca armazenamos o número bruto */
    @Column(name = "remetente_hash", nullable = false, length = 64)
    private String remetenteHash;

    @Column(name = "data_mensagem")
    private LocalDateTime dataMensagem;

    @Column(name = "texto_original", nullable = false, columnDefinition = "TEXT")
    private String textoOriginal;

    @Column(name = "titulo_sugerido")
    private String tituloSugerido;

    @Column(name = "descricao_sugerida", columnDefinition = "TEXT")
    private String descricaoSugerida;

    @Column(name = "preco_sugerido", precision = 10, scale = 2)
    private BigDecimal precoSugerido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCandidato status = StatusCandidato.PENDENTE_REVISAO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anuncio_id")
    private Anuncio anuncio;

    @Column(name = "revisado_em")
    private LocalDateTime revisadoEm;

    // ---- getters e setters ----

    public Long getId() { return id; }
    public ImportacaoWhatsApp getImportacao() { return importacao; }
    public void setImportacao(ImportacaoWhatsApp importacao) { this.importacao = importacao; }
    public String getRemetenteHash() { return remetenteHash; }
    public void setRemetenteHash(String remetenteHash) { this.remetenteHash = remetenteHash; }
    public LocalDateTime getDataMensagem() { return dataMensagem; }
    public void setDataMensagem(LocalDateTime dataMensagem) { this.dataMensagem = dataMensagem; }
    public String getTextoOriginal() { return textoOriginal; }
    public void setTextoOriginal(String textoOriginal) { this.textoOriginal = textoOriginal; }
    public String getTituloSugerido() { return tituloSugerido; }
    public void setTituloSugerido(String tituloSugerido) { this.tituloSugerido = tituloSugerido; }
    public String getDescricaoSugerida() { return descricaoSugerida; }
    public void setDescricaoSugerida(String descricaoSugerida) { this.descricaoSugerida = descricaoSugerida; }
    public BigDecimal getPrecoSugerido() { return precoSugerido; }
    public void setPrecoSugerido(BigDecimal precoSugerido) { this.precoSugerido = precoSugerido; }
    public StatusCandidato getStatus() { return status; }
    public void setStatus(StatusCandidato status) { this.status = status; }
    public Anuncio getAnuncio() { return anuncio; }
    public void setAnuncio(Anuncio anuncio) { this.anuncio = anuncio; }
    public LocalDateTime getRevisadoEm() { return revisadoEm; }
    public void setRevisadoEm(LocalDateTime revisadoEm) { this.revisadoEm = revisadoEm; }
}
