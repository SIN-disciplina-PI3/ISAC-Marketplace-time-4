package com.isac.marketplace.service;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.ResultadoBuscaDTO;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.BuscaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do BuscaProdutoService")
class BuscaProdutoServiceTest {

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private BuscaRepository buscaRepository;

    private Clock fixedClock;
    private BuscaProdutoService buscaProdutoService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-28T10:00:00Z"), ZoneId.of("America/Recife"));
        buscaProdutoService = new BuscaProdutoService(anuncioRepository, buscaRepository, fixedClock);
    }

    private Loja criarLoja(Long id, String nome, PlanoLoja plano, StatusAssinatura status) {
        Loja loja = new Loja();
        loja.setId(id);
        loja.setNomeFantasia(nome);
        loja.setPlano(plano);
        loja.setStatusAssinatura(status);
        loja.setBairro("Enseada dos Corais");
        loja.setTelefoneWhatsapp("8199999000" + id);
        return loja;
    }

    private Anuncio criarAnuncio(Long id, Loja loja, String titulo, String desc, BigDecimal preco, boolean destaque, boolean disp, LocalDateTime expiracao) {
        Anuncio a = new Anuncio();
        a.setId(id);
        a.setLoja(loja);
        a.setTitulo(titulo);
        a.setDescricao(desc);
        a.setPreco(preco);
        a.setBairro(loja.getBairro());
        a.setStatus(StatusAnuncio.ATIVO);
        a.setDisponivel(disp);
        a.setDestaque(destaque);
        a.setDataPublicacao(LocalDateTime.now(fixedClock).minusDays(1));
        a.setDataExpiracao(expiracao);
        return a;
    }

    @Test
    @DisplayName("Deve encontrar anúncio ignorando acentuação e caixa alta ('feijao' acha 'Feijão')")
    void deveBuscarPorTermoSemDiferenciarAcentosECaixa() {
        Loja loja = criarLoja(1L, "Sítio Verde", PlanoLoja.GRATUITO, StatusAssinatura.INATIVA);
        Anuncio feijao = criarAnuncio(1L, loja, "Feijão Macáçar Fresco", "Colhido hoje", new BigDecimal("10.00"), false, true, LocalDateTime.now(fixedClock).plusDays(10));

        when(anuncioRepository.findAnunciosAtivosEDisponiveis(eq(StatusAnuncio.ATIVO), any())).thenReturn(List.of(feijao));

        ResultadoBuscaDTO resultado = buscaProdutoService.buscarWeb("feijao", null, null, 0, 10);

        assertEquals(1, resultado.total());
        assertEquals("Feijão Macáçar Fresco", resultado.anuncios().get(0).titulo());
        verify(buscaRepository).save(any(Busca.class));
    }

    @Test
    @DisplayName("Deve rejeitar termo com menos de 2 caracteres na busca do bot")
    void deveRejeitarTermoComMenosDeDoisCaracteres() {
        RegraNegocioException ex1 = assertThrows(RegraNegocioException.class, () -> buscaProdutoService.buscarParaBot("a", null, "81999990001", 5));
        assertEquals("TERMO_INVALIDO", ex1.getCodigo());

        RegraNegocioException ex2 = assertThrows(RegraNegocioException.class, () -> buscaProdutoService.buscarParaBot("", null, "81999990001", 5));
        assertEquals("TERMO_INVALIDO", ex2.getCodigo());
    }

    @Test
    @DisplayName("Deve ignorar anúncios expirados na busca")
    void deveIgnorarAnunciosExpirados() {
        Loja loja = criarLoja(1L, "Tapiocaria", PlanoLoja.GRATUITO, StatusAssinatura.INATIVA);
        Anuncio expirado = criarAnuncio(1L, loja, "Tapioca Especial", "Desc", new BigDecimal("12.00"), false, true, LocalDateTime.now(fixedClock).minusDays(1));

        when(anuncioRepository.findAnunciosAtivosEDisponiveis(eq(StatusAnuncio.ATIVO), any())).thenReturn(List.of(expirado));

        ResultadoBuscaDTO resultado = buscaProdutoService.buscarWeb("tapioca", null, null, 0, 10);

        assertEquals(0, resultado.total());
    }

    @Test
    @DisplayName("Deve ignorar anúncios indisponíveis na busca")
    void deveIgnorarAnunciosIndisponiveis() {
        Loja loja = criarLoja(1L, "Tapiocaria", PlanoLoja.GRATUITO, StatusAssinatura.INATIVA);
        Anuncio indisponivel = criarAnuncio(1L, loja, "Tapioca Especial", "Desc", new BigDecimal("12.00"), false, false, LocalDateTime.now(fixedClock).plusDays(10));

        when(anuncioRepository.findAnunciosAtivosEDisponiveis(eq(StatusAnuncio.ATIVO), any())).thenReturn(List.of(indisponivel));

        ResultadoBuscaDTO resultado = buscaProdutoService.buscarWeb("tapioca", null, null, 0, 10);

        assertEquals(0, resultado.total());
    }

    @Test
    @DisplayName("Deve ignorar anúncios de lojas assinantes com assinatura inativa/pendente que não sejam elegíveis")
    void deveIgnorarAnunciosDeLojasAssinantesNaoAtivas() {
        Loja lojaInativa = criarLoja(1L, "Loja Inadimplente", PlanoLoja.ASSINANTE, StatusAssinatura.INATIVA);
        Anuncio anuncio = criarAnuncio(1L, lojaInativa, "Serviço Elétrico", "Desc", new BigDecimal("100.00"), false, true, LocalDateTime.now(fixedClock).plusDays(10));

        when(anuncioRepository.findAnunciosAtivosEDisponiveis(eq(StatusAnuncio.ATIVO), any())).thenReturn(List.of(anuncio));

        ResultadoBuscaDTO resultado = buscaProdutoService.buscarWeb("eletrico", null, null, 0, 10);

        assertEquals(0, resultado.total());
    }

    @Test
    @DisplayName("Deve priorizar anúncios em destaque de assinantes ativos")
    void devePriorizarAnunciosDeAssinantesComDestaque() {
        Loja lojaGratuita = criarLoja(1L, "Loja Gratuita", PlanoLoja.GRATUITO, StatusAssinatura.INATIVA);
        Loja lojaAssinante = criarLoja(2L, "Loja Assinante", PlanoLoja.ASSINANTE, StatusAssinatura.ATIVA);

        Anuncio semDestaque = criarAnuncio(1L, lojaGratuita, "Tapioca Simples", "Desc", new BigDecimal("10.00"), false, true, LocalDateTime.now(fixedClock).plusDays(10));
        semDestaque.setDataPublicacao(LocalDateTime.now(fixedClock)); // mais recente mas sem destaque

        Anuncio comDestaque = criarAnuncio(2L, lojaAssinante, "Tapioca Premium", "Desc", new BigDecimal("15.00"), true, true, LocalDateTime.now(fixedClock).plusDays(10));
        comDestaque.setDataPublicacao(LocalDateTime.now(fixedClock).minusDays(2)); // mais antigo mas com destaque

        when(anuncioRepository.findAnunciosAtivosEDisponiveis(eq(StatusAnuncio.ATIVO), any())).thenReturn(List.of(semDestaque, comDestaque));

        ResultadoBuscaDTO resultado = buscaProdutoService.buscarWeb("tapioca", null, null, 0, 10);

        assertEquals(2, resultado.total());
        assertEquals(2L, resultado.anuncios().get(0).id()); // o com destaque veio primeiro
        assertEquals(1L, resultado.anuncios().get(1).id());
    }

    @Test
    @DisplayName("Não deve repetir a mesma loja nos resultados do bot de WhatsApp")
    void deveNaoRepetirMesmaLojaNoBot() {
        Loja loja1 = criarLoja(1L, "Tapiocaria da Enseada", PlanoLoja.GRATUITO, StatusAssinatura.INATIVA);
        Loja loja2 = criarLoja(2L, "Tapiocas do Gaibu", PlanoLoja.GRATUITO, StatusAssinatura.INATIVA);

        Anuncio tap1 = criarAnuncio(1L, loja1, "Tapioca de Queijo", "Desc", new BigDecimal("12.00"), false, true, LocalDateTime.now(fixedClock).plusDays(10));
        Anuncio tap2 = criarAnuncio(2L, loja1, "Tapioca de Coco", "Desc", new BigDecimal("10.00"), false, true, LocalDateTime.now(fixedClock).plusDays(10));
        Anuncio tap3 = criarAnuncio(3L, loja2, "Tapioca Tradicional", "Desc", new BigDecimal("11.00"), false, true, LocalDateTime.now(fixedClock).plusDays(10));

        when(anuncioRepository.findAnunciosAtivosEDisponiveis(eq(StatusAnuncio.ATIVO), any())).thenReturn(List.of(tap1, tap2, tap3));

        List<AnuncioDTO> resultadosBot = buscaProdutoService.buscarParaBot("tapioca", null, "81999990001", 5);

        assertEquals(2, resultadosBot.size()); // Apenas 1 anúncio da loja 1 e 1 da loja 2
        assertEquals(1L, resultadosBot.get(0).lojaId());
        assertEquals(2L, resultadosBot.get(1).lojaId());
    }

    @Test
    @DisplayName("Deve registrar busca para o bot com hash seguro do telefone do usuário (LGPD)")
    void deveRegistrarBuscaComHashTelefone() {
        Loja loja = criarLoja(1L, "Loja 1", PlanoLoja.GRATUITO, StatusAssinatura.INATIVA);
        Anuncio tap = criarAnuncio(1L, loja, "Tapioca", "Desc", new BigDecimal("10.00"), false, true, LocalDateTime.now(fixedClock).plusDays(10));

        when(anuncioRepository.findAnunciosAtivosEDisponiveis(eq(StatusAnuncio.ATIVO), any())).thenReturn(List.of(tap));

        buscaProdutoService.buscarParaBot("tapioca", null, "81999990001", 5);

        verify(buscaRepository).save(argThat(busca ->
                busca.getCanal() == CanalBusca.WHATSAPP &&
                busca.getTelefoneHash() != null &&
                !busca.getTelefoneHash().contains("81999990001") &&
                busca.getTelefoneHash().length() == 64
        ));
    }
}
