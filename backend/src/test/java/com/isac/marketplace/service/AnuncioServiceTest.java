package com.isac.marketplace.service;

import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.CategoriaRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do AnuncioService")
class AnuncioServiceTest {

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    private Clock fixedClock;
    private AnuncioService anuncioService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-28T10:00:00Z"), ZoneId.of("America/Recife"));
        anuncioService = new AnuncioService(anuncioRepository, categoriaRepository, fixedClock, false, 15);
    }

    private Loja criarLojaGratuita() {
        Loja loja = new Loja();
        loja.setId(1L);
        loja.setNomeFantasia("Loja Artesanal");
        loja.setBairro("Enseada dos Corais");
        loja.setPlano(PlanoLoja.GRATUITO);
        loja.setStatusAssinatura(StatusAssinatura.INATIVA);
        return loja;
    }

    private Loja criarLojaAssinanteAtiva() {
        Loja loja = new Loja();
        loja.setId(2L);
        loja.setNomeFantasia("Loja Assinante Top");
        loja.setBairro("Enseada dos Corais");
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.ATIVA);
        return loja;
    }

    @Test
    @DisplayName("Deve criar primeiro anúncio gratuito com sucesso e expiração em 15 dias")
    void deveCriarPrimeiroAnuncioGratuitoComSucesso() {
        Loja loja = criarLojaGratuita();
        CriarAnuncioDTO dto = new CriarAnuncioDTO(
                "Tapioca Tradicional",
                "Tapioca crocante com queijo",
                new BigDecimal("12.50"),
                null,
                "Enseada dos Corais",
                TipoAnuncio.PRODUTO,
                null
        );

        when(anuncioRepository.countByLojaAndStatus(loja, StatusAnuncio.ATIVO)).thenReturn(0L);
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(inv -> inv.getArgument(0));

        Anuncio criado = anuncioService.criarAnuncio(loja, dto);

        assertNotNull(criado);
        assertEquals("Tapioca Tradicional", criado.getTitulo());
        assertEquals(new BigDecimal("12.50"), criado.getPreco());
        assertEquals(StatusAnuncio.ATIVO, criado.getStatus());
        assertEquals(LocalDateTime.now(fixedClock).plusDays(15), criado.getDataExpiracao());
        assertFalse(criado.getDestaque());
    }

    @Test
    @DisplayName("Deve permitir segundo anúncio no plano gratuito (cota limite = 2)")
    void deveCriarSegundoAnuncioGratuitoComSucesso() {
        Loja loja = criarLojaGratuita();
        CriarAnuncioDTO dto = new CriarAnuncioDTO(
                "Bolo de Rolo",
                "500g goiabada",
                new BigDecimal("25.00"),
                null,
                "Enseada dos Corais",
                TipoAnuncio.PRODUTO,
                null
        );

        when(anuncioRepository.countByLojaAndStatus(loja, StatusAnuncio.ATIVO)).thenReturn(1L);
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(inv -> inv.getArgument(0));

        Anuncio criado = anuncioService.criarAnuncio(loja, dto);

        assertNotNull(criado);
        assertEquals("Bolo de Rolo", criado.getTitulo());
    }

    @Test
    @DisplayName("Deve recusar terceiro anúncio no plano gratuito por cota excedida")
    void deveRecusarTerceiroAnuncioGratuito() {
        Loja loja = criarLojaGratuita();
        CriarAnuncioDTO dto = new CriarAnuncioDTO(
                "Cuscuz Recheado",
                "Com carne de sol",
                new BigDecimal("18.00"),
                null,
                "Enseada dos Corais",
                TipoAnuncio.PRODUTO,
                null
        );

        when(anuncioRepository.countByLojaAndStatus(loja, StatusAnuncio.ATIVO)).thenReturn(2L);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> anuncioService.criarAnuncio(loja, dto));
        assertEquals("COTA_EXCEDIDA", ex.getCodigo());
        assertTrue(ex.getMessage().contains("upgrade"));
        verify(anuncioRepository, never()).save(any(Anuncio.class));
    }

    @Test
    @DisplayName("Deve permitir terceiro anúncio quando a loja for assinante com assinatura ativa")
    void devePermitirMaisDeDoisAnunciosParaAssinanteAtivo() {
        Loja loja = criarLojaAssinanteAtiva();
        CriarAnuncioDTO dto = new CriarAnuncioDTO(
                "Produto Ilimitado",
                "Descrição",
                new BigDecimal("50.00"),
                null,
                "Enseada dos Corais",
                TipoAnuncio.PRODUTO,
                null
        );

        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(inv -> inv.getArgument(0));

        Anuncio criado = anuncioService.criarAnuncio(loja, dto);

        assertNotNull(criado);
        assertTrue(criado.getDestaque());
        verify(anuncioRepository, never()).countByLojaAndStatus(any(), any());
    }

    @Test
    @DisplayName("Deve rejeitar anúncio com preço menor ou igual a zero")
    void deveRejeitarAnuncioComPrecoInvalido() {
        Loja loja = criarLojaGratuita();
        CriarAnuncioDTO dtoZero = new CriarAnuncioDTO("Item", null, BigDecimal.ZERO, null, null, null, null);
        CriarAnuncioDTO dtoNegativo = new CriarAnuncioDTO("Item", null, new BigDecimal("-5.00"), null, null, null, null);

        RegraNegocioException ex1 = assertThrows(RegraNegocioException.class, () -> anuncioService.criarAnuncio(loja, dtoZero));
        assertEquals("PRECO_INVALIDO", ex1.getCodigo());

        RegraNegocioException ex2 = assertThrows(RegraNegocioException.class, () -> anuncioService.criarAnuncio(loja, dtoNegativo));
        assertEquals("PRECO_INVALIDO", ex2.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar anúncio sem título")
    void deveRejeitarAnuncioSemTitulo() {
        Loja loja = criarLojaGratuita();
        CriarAnuncioDTO dto = new CriarAnuncioDTO("", "Desc", new BigDecimal("10.00"), null, null, null, null);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> anuncioService.criarAnuncio(loja, dto));
        assertEquals("TITULO_OBRIGATORIO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve renovar anúncio reiniciando o prazo de 15 dias a partir de agora")
    void deveRenovarAnuncioComSucesso() {
        Loja loja = criarLojaGratuita();
        Anuncio anuncio = new Anuncio();
        anuncio.setId(10L);
        anuncio.setLoja(loja);
        anuncio.setTitulo("Tapioca");
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setDataExpiracao(LocalDateTime.now(fixedClock).minusDays(1)); // já expirado

        when(anuncioRepository.findById(10L)).thenReturn(Optional.of(anuncio));
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(inv -> inv.getArgument(0));

        Anuncio renovado = anuncioService.renovar(10L, loja);

        assertEquals(LocalDateTime.now(fixedClock).plusDays(15), renovado.getDataExpiracao());
        assertEquals(StatusAnuncio.ATIVO, renovado.getStatus());
        assertTrue(renovado.getDisponivel());
        assertFalse(renovado.getAvisoExpiracaoEnviado());
    }

    @Test
    @DisplayName("Deve rejeitar renovação de anúncio rejeitado pela moderação")
    void deveRejeitarRenovacaoDeAnuncioRejeitado() {
        Loja loja = criarLojaGratuita();
        Anuncio anuncio = new Anuncio();
        anuncio.setId(10L);
        anuncio.setLoja(loja);
        anuncio.setStatus(StatusAnuncio.REJEITADO);

        when(anuncioRepository.findById(10L)).thenReturn(Optional.of(anuncio));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> anuncioService.renovar(10L, loja));
        assertEquals("ANUNCIO_REJEITADO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve alterar disponibilidade do anúncio com sucesso")
    void deveAlterarDisponibilidade() {
        Loja loja = criarLojaGratuita();
        Anuncio anuncio = new Anuncio();
        anuncio.setId(10L);
        anuncio.setLoja(loja);
        anuncio.setDisponivel(true);

        when(anuncioRepository.findById(10L)).thenReturn(Optional.of(anuncio));
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(inv -> inv.getArgument(0));

        Anuncio atualizado = anuncioService.alterarDisponibilidade(10L, false, loja);
        assertFalse(atualizado.getDisponivel());
    }

    @Test
    @DisplayName("Deve pausar e reativar anúncio respeitando cota")
    void devePausarEReativarAnuncio() {
        Loja loja = criarLojaGratuita();
        Anuncio anuncio = new Anuncio();
        anuncio.setId(10L);
        anuncio.setLoja(loja);
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setDataExpiracao(LocalDateTime.now(fixedClock).plusDays(5));

        when(anuncioRepository.findById(10L)).thenReturn(Optional.of(anuncio));
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(inv -> inv.getArgument(0));
        when(anuncioRepository.countByLojaAndStatus(loja, StatusAnuncio.ATIVO)).thenReturn(1L);

        Anuncio pausado = anuncioService.pausar(10L, loja);
        assertEquals(StatusAnuncio.PAUSADO, pausado.getStatus());

        Anuncio reativado = anuncioService.reativar(10L, loja);
        assertEquals(StatusAnuncio.ATIVO, reativado.getStatus());
    }

    @Test
    @DisplayName("Deve aprovar e rejeitar moderação de anúncio pelo administrador")
    void deveAprovarERejeitarModeracao() {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(10L);
        anuncio.setStatus(StatusAnuncio.PENDENTE_MODERACAO);

        when(anuncioRepository.findById(10L)).thenReturn(Optional.of(anuncio));
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(inv -> inv.getArgument(0));

        Anuncio aprovado = anuncioService.aprovarModeracao(10L);
        assertEquals(StatusAnuncio.ATIVO, aprovado.getStatus());
        assertNull(aprovado.getMotivoRejeicao());

        Anuncio rejeitado = anuncioService.rejeitarModeracao(10L, "Conteúdo fora das diretrizes comunitárias");
        assertEquals(StatusAnuncio.REJEITADO, rejeitado.getStatus());
        assertEquals("Conteúdo fora das diretrizes comunitárias", rejeitado.getMotivoRejeicao());
    }
}
