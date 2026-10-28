package com.isac.marketplace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "lojas")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fantasia", nullable = false, length = 150)
    private String nomeFantasia;

    @Column(unique = true, length = 20)
    private String documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 10)
    private TipoDocumento tipoDocumento;

    @Column(length = 100)
    private String categoria;

    @Column(name = "telefone_whatsapp", nullable = false, unique = true, length = 20)
    private String telefoneWhatsapp;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(length = 255)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanoLoja plano = PlanoLoja.GRATUITO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_assinatura", nullable = false, length = 20)
    private StatusAssinatura statusAssinatura = StatusAssinatura.INATIVA;

    @Column(name = "data_vencimento_assinatura")
    private LocalDateTime dataVencimentoAssinatura;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    public Loja() {}

    public boolean isElegivel() {
        if (this.plano == PlanoLoja.GRATUITO) {
            return true;
        }
        return this.statusAssinatura == StatusAssinatura.ATIVA;
    }

    public boolean isAssinanteAtivo() {
        return this.plano == PlanoLoja.ASSINANTE && this.statusAssinatura == StatusAssinatura.ATIVA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTelefoneWhatsapp() {
        return telefoneWhatsapp;
    }

    public void setTelefoneWhatsapp(String telefoneWhatsapp) {
        this.telefoneWhatsapp = telefoneWhatsapp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public PlanoLoja getPlano() {
        return plano;
    }

    public void setPlano(PlanoLoja plano) {
        this.plano = plano;
    }

    public StatusAssinatura getStatusAssinatura() {
        return statusAssinatura;
    }

    public void setStatusAssinatura(StatusAssinatura statusAssinatura) {
        this.statusAssinatura = statusAssinatura;
    }

    public LocalDateTime getDataVencimentoAssinatura() {
        return dataVencimentoAssinatura;
    }

    public void setDataVencimentoAssinatura(LocalDateTime dataVencimentoAssinatura) {
        this.dataVencimentoAssinatura = dataVencimentoAssinatura;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loja loja)) return false;
        return Objects.equals(id, loja.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
