package com.isac.marketplace.service;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.model.StatusAnuncio;
import com.isac.marketplace.model.TipoAnuncio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do ChatbotService")
class ChatbotServiceTest {

    @Mock
    private BuscaProdutoService buscaProdutoService;

    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() {
        chatbotService = new ChatbotService(buscaProdutoService);
    }

    @Test
    @DisplayName("Deve responder saudação com menu de opções numeradas")
    void deveResponderSaudacaoComMenu() {
        MensagemChatbotResponse r1 = chatbotService.responderMensagem("81999990001", "oi");
        MensagemChatbotResponse r2 = chatbotService.responderMensagem("81999990001", "olá");
        MensagemChatbotResponse r3 = chatbotService.responderMensagem("81999990001", "menu");

        assertTrue(r1.resposta().contains("Marketplace Comunitário do ISAC"));
        assertTrue(r1.resposta().contains("1️⃣ *Buscar*"));
        assertTrue(r2.resposta().contains("2️⃣ *Anunciar*"));
        assertTrue(r3.resposta().contains("4️⃣ *Ajuda*"));
    }

    @Test
    @DisplayName("Deve orientar usuário ao selecionar opção 1 de busca sem termo")
    void deveOrientarAoDigitarOpcao1() {
        MensagemChatbotResponse r = chatbotService.responderMensagem("81999990001", "1");
        assertTrue(r.resposta().contains("Como você deseja buscar?"));
    }

    @Test
    @DisplayName("Deve executar busca ao enviar '/buscar tapioca'")
    void deveExecutarBuscaComComandoEArgumento() {
        AnuncioDTO dto = new AnuncioDTO(
                1L, 1L, "Tapiocaria da Enseada", "81999990001",
                "Tapioca de Queijo Coalho", "Desc", new BigDecimal("12.00"),
                1L, "Alimentação", "Enseada dos Corais", TipoAnuncio.PRODUTO,
                null, StatusAnuncio.ATIVO, true, true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(15),
                "https://wa.me/5581999990001?text=Ola"
        );

        when(buscaProdutoService.buscarParaBot(eq("tapioca"), isNull(), eq("81999990001"), eq(5)))
                .thenReturn(List.of(dto));

        MensagemChatbotResponse r = chatbotService.responderMensagem("81999990001", "/buscar tapioca");

        assertTrue(r.resposta().contains("Tapioca de Queijo Coalho"));
        assertTrue(r.resposta().contains("Tapiocaria da Enseada"));
        assertTrue(r.resposta().contains("https://wa.me/5581999990001"));
        assertEquals(1, r.links().size());
    }

    @Test
    @DisplayName("Deve remover prefixos como 'quero', 'procuro', 'onde compro' antes de buscar")
    void deveRemoverPrefixosDeBusca() {
        assertEquals("tapioca com queijo", chatbotService.extrairTermoBusca("quero tapioca com queijo"));
        assertEquals("peixe fresco", chatbotService.extrairTermoBusca("onde compro peixe fresco"));
        assertEquals("artesanato", chatbotService.extrairTermoBusca("procuro artesanato"));
        assertEquals("bolo de rolo", chatbotService.extrairTermoBusca("quem vende bolo de rolo"));
        assertEquals("aulas de surf", chatbotService.extrairTermoBusca("tem aulas de surf"));
    }

    @Test
    @DisplayName("Deve buscar diretamente ao receber texto livre que não seja comando")
    void deveBuscarDiretamenteTextoLivre() {
        AnuncioDTO dto = new AnuncioDTO(
                1L, 1L, "Peixaria Viva", "81999990005",
                "Peixe Vermelho Fresco", "Desc", new BigDecimal("38.00"),
                1L, "Alimentação", "Enseada dos Corais", TipoAnuncio.PRODUTO,
                null, StatusAnuncio.ATIVO, true, false,
                LocalDateTime.now(), LocalDateTime.now().plusDays(15),
                "https://wa.me/5581999990005?text=Ola"
        );

        when(buscaProdutoService.buscarParaBot(eq("peixe vermelho"), isNull(), eq("81999990001"), eq(5)))
                .thenReturn(List.of(dto));

        MensagemChatbotResponse r = chatbotService.responderMensagem("81999990001", "quero peixe vermelho");

        assertTrue(r.resposta().contains("Peixe Vermelho Fresco"));
        verify(buscaProdutoService).buscarParaBot("peixe vermelho", null, "81999990001", 5);
    }

    @Test
    @DisplayName("Deve retornar mensagem amigável quando nenhum anúncio for encontrado")
    void deveRetornarMensagemSemResultados() {
        when(buscaProdutoService.buscarParaBot(eq("disco voador"), isNull(), eq("81999990001"), eq(5)))
                .thenReturn(List.of());

        MensagemChatbotResponse r = chatbotService.responderMensagem("81999990001", "disco voador");

        assertTrue(r.resposta().contains("Não encontrei"));
        assertTrue(r.resposta().contains("disco voador"));
    }

    @Test
    @DisplayName("Deve exibir menu de ajuda com opção 4 ou /ajuda")
    void deveExibirAjuda() {
        MensagemChatbotResponse r1 = chatbotService.responderMensagem("81999990001", "4");
        MensagemChatbotResponse r2 = chatbotService.responderMensagem("81999990001", "/ajuda");

        assertTrue(r1.resposta().contains("Ajuda do ISAC Marketplace"));
        assertTrue(r2.resposta().contains("Como anunciar"));
    }

    @Test
    @DisplayName("Deve abortar operação ao receber comando /cancelar")
    void deveCancelarOperacao() {
        MensagemChatbotResponse r = chatbotService.responderMensagem("81999990001", "/cancelar");
        assertTrue(r.resposta().contains("Operação cancelada"));
    }
}
