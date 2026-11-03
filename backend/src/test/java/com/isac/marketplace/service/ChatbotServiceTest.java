package com.isac.marketplace.service;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do ChatbotService")
class ChatbotServiceTest {

    @Mock
    private BuscaProdutoService buscaProdutoService;

    @Mock
    private ConversaService conversaService;

    @Mock
    private LojaService lojaService;

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private CategoriaRepository categoriaRepository;

    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() {
        chatbotService = new ChatbotService(
                buscaProdutoService,
                conversaService,
                lojaService,
                anuncioService,
                categoriaRepository
        );
    }

    private Loja criarLojaTeste(Long id, String nome, PlanoLoja plano) {
        Loja l = new Loja();
        l.setId(id);
        l.setNomeFantasia(nome);
        l.setTelefoneWhatsapp("81999990001");
        l.setBairro("Enseada dos Corais");
        l.setPlano(plano);
        l.setStatusAssinatura(plano == PlanoLoja.ASSINANTE ? StatusAssinatura.ATIVA : StatusAssinatura.INATIVA);
        return l;
    }

    @Test
    @DisplayName("Deve responder saudação com menu de opções numeradas")
    void deveResponderSaudacaoComMenu() {
        when(conversaService.obterEstado("81999990001")).thenReturn(Optional.empty());

        MensagemChatbotResponse r1 = chatbotService.responderMensagem("81999990001", "oi");
        MensagemChatbotResponse r2 = chatbotService.responderMensagem("81999990001", "menu");

        assertTrue(r1.resposta().contains("Marketplace Comunitário do ISAC"));
        assertTrue(r1.resposta().contains("1️⃣ *Buscar*"));
        assertTrue(r1.resposta().contains("2️⃣ *Anunciar*"));
        assertTrue(r2.resposta().contains("4️⃣ *Ajuda*"));
    }

    @Test
    @DisplayName("Deve orientar usuário ao selecionar opção 1 de busca sem termo")
    void deveOrientarAoDigitarOpcao1() {
        when(conversaService.obterEstado("81999990001")).thenReturn(Optional.empty());

        MensagemChatbotResponse r = chatbotService.responderMensagem("81999990001", "1");
        assertTrue(r.resposta().contains("Como você deseja buscar?"));
    }

    @Test
    @DisplayName("Deve executar busca ao enviar '/buscar tapioca'")
    void deveExecutarBuscaComComandoEArgumento() {
        when(conversaService.obterEstado("81999990001")).thenReturn(Optional.empty());

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
    @DisplayName("Deve buscar diretamente ao receber texto livre que não seja comando")
    void deveBuscarDiretamenteTextoLivre() {
        when(conversaService.obterEstado("81999990001")).thenReturn(Optional.empty());

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
    @DisplayName("Deve abortar operação e limpar estado ao receber comando /cancelar")
    void deveCancelarOperacao() {
        MensagemChatbotResponse r = chatbotService.responderMensagem("81999990001", "/cancelar");
        assertTrue(r.resposta().contains("Operação cancelada"));
        verify(conversaService).limparEstado("81999990001");
    }

    @Test
    @DisplayName("Fluxo completo de /anunciar para loja já cadastrada até a publicação")
    void fluxoCompletoDeAnuncioParaLojaExistente() {
        String tel = "81999990001";
        Loja loja = criarLojaTeste(1L, "Tapiocaria", PlanoLoja.GRATUITO);

        // 1. Início do fluxo /anunciar
        when(conversaService.obterEstado(tel)).thenReturn(Optional.empty());
        when(lojaService.buscarPorTelefone(tel)).thenReturn(Optional.of(loja));
        when(anuncioService.listarPorLoja(loja)).thenReturn(List.of());

        MensagemChatbotResponse r1 = chatbotService.responderMensagem(tel, "/anunciar");
        assertTrue(r1.resposta().contains("Qual é o *Título* do seu produto ou serviço?"));
        verify(conversaService).salvarEstado(eq(tel), eq(ChatbotService.ETAPA_TITULO), anyMap());

        // 2. Informa Título -> solicita Categoria
        Map<String, Object> dadosEtapa1 = new HashMap<>(Map.of("lojaId", 1L, "bairroLoja", "Enseada dos Corais"));
        when(conversaService.obterEstado(tel)).thenReturn(Optional.of(new ConversaService.EstadoConversa(
                "hash", ChatbotService.ETAPA_TITULO, dadosEtapa1, LocalDateTime.now()
        )));
        when(categoriaRepository.findByAtivaTrueOrderByNomeAsc()).thenReturn(List.of(
                new Categoria(1L, "Alimentação", true),
                new Categoria(2L, "Artesanato", true)
        ));

        MensagemChatbotResponse r2 = chatbotService.responderMensagem(tel, "Tapioca Tradicional");
        assertTrue(r2.resposta().contains("Escolha a *Categoria*"));
        assertTrue(r2.resposta().contains("1) Alimentação"));

        // 3. Informa Categoria -> solicita Preço
        Map<String, Object> dadosEtapa2 = new HashMap<>(Map.of("lojaId", 1L, "titulo", "Tapioca Tradicional"));
        when(conversaService.obterEstado(tel)).thenReturn(Optional.of(new ConversaService.EstadoConversa(
                "hash", ChatbotService.ETAPA_CATEGORIA, dadosEtapa2, LocalDateTime.now()
        )));

        MensagemChatbotResponse r3 = chatbotService.responderMensagem(tel, "1");
        assertTrue(r3.resposta().contains("Qual é o *Preço*?"));

        // 4. Informa Preço -> solicita Bairro
        Map<String, Object> dadosEtapa3 = new HashMap<>(Map.of("lojaId", 1L, "titulo", "Tapioca Tradicional", "categoriaId", 1L, "categoriaNome", "Alimentação"));
        when(conversaService.obterEstado(tel)).thenReturn(Optional.of(new ConversaService.EstadoConversa(
                "hash", ChatbotService.ETAPA_PRECO, dadosEtapa3, LocalDateTime.now()
        )));

        MensagemChatbotResponse r4 = chatbotService.responderMensagem(tel, "R$ 12,50");
        assertTrue(r4.resposta().contains("Qual o *Bairro*"));

        // 5. Informa Bairro ("mesmo") -> solicita Foto
        Map<String, Object> dadosEtapa4 = new HashMap<>(dadosEtapa3);
        dadosEtapa4.put("preco", "12.50");
        dadosEtapa4.put("bairroLoja", "Enseada dos Corais");
        when(conversaService.obterEstado(tel)).thenReturn(Optional.of(new ConversaService.EstadoConversa(
                "hash", ChatbotService.ETAPA_BAIRRO, dadosEtapa4, LocalDateTime.now()
        )));

        MensagemChatbotResponse r5 = chatbotService.responderMensagem(tel, "mesmo");
        assertTrue(r5.resposta().contains("Deseja adicionar uma *Foto*"));

        // 6. Informa Foto ("pular") -> exibe Resumo e pede confirmação
        Map<String, Object> dadosEtapa5 = new HashMap<>(dadosEtapa4);
        dadosEtapa5.put("bairro", "Enseada dos Corais");
        when(conversaService.obterEstado(tel)).thenReturn(Optional.of(new ConversaService.EstadoConversa(
                "hash", ChatbotService.ETAPA_FOTO, dadosEtapa5, LocalDateTime.now()
        )));

        MensagemChatbotResponse r6 = chatbotService.responderMensagem(tel, "pular");
        assertTrue(r6.resposta().contains("Confirmação do Anúncio"));
        assertTrue(r6.resposta().contains("Tapioca Tradicional"));
        assertTrue(r6.resposta().contains("R$"));

        // 7. Confirmação ("sim") -> Anúncio publicado!
        Map<String, Object> dadosEtapa6 = new HashMap<>(dadosEtapa5);
        dadosEtapa6.put("fotoUrl", null);
        when(conversaService.obterEstado(tel)).thenReturn(Optional.of(new ConversaService.EstadoConversa(
                "hash", ChatbotService.ETAPA_CONFIRMACAO, dadosEtapa6, LocalDateTime.now()
        )));
        when(lojaService.buscarPorId(1L)).thenReturn(loja);

        Anuncio anuncioCriado = new Anuncio();
        anuncioCriado.setId(101L);
        anuncioCriado.setTitulo("Tapioca Tradicional");
        anuncioCriado.setDataExpiracao(LocalDateTime.now().plusDays(15));
        when(anuncioService.criarAnuncio(eq(loja), any(CriarAnuncioDTO.class))).thenReturn(anuncioCriado);

        MensagemChatbotResponse r7 = chatbotService.responderMensagem(tel, "sim");
        assertTrue(r7.resposta().contains("Anúncio Publicado com Sucesso!"));
        assertTrue(r7.resposta().contains("#101"));
        verify(conversaService).limparEstado(tel);
    }

    @Test
    @DisplayName("Primeiro anúncio de loja nova deve solicitar Nome e Bairro antes do anúncio")
    void primeiroAnuncioLojaNovaPedeCadastro() {
        String tel = "81988880000";

        when(conversaService.obterEstado(tel)).thenReturn(Optional.empty());
        when(lojaService.buscarPorTelefone(tel)).thenReturn(Optional.empty());

        MensagemChatbotResponse r = chatbotService.responderMensagem(tel, "/anunciar");
        assertTrue(r.resposta().contains("primeiro anúncio"));
        assertTrue(r.resposta().contains("Nome do seu Comércio"));
        verify(conversaService).salvarEstado(eq(tel), eq(ChatbotService.ETAPA_CRIAR_LOJA_NOME), anyMap());
    }

    @Test
    @DisplayName("Deve barrar /anunciar se a loja gratuita já atingiu a cota de 2 anúncios ativos")
    void deveBarrarAnunciarQuandoCotaExcedida() {
        String tel = "81999990001";
        Loja loja = criarLojaTeste(1L, "Tapiocaria", PlanoLoja.GRATUITO);

        Anuncio a1 = new Anuncio(); a1.setStatus(StatusAnuncio.ATIVO);
        Anuncio a2 = new Anuncio(); a2.setStatus(StatusAnuncio.ATIVO);

        when(conversaService.obterEstado(tel)).thenReturn(Optional.empty());
        when(lojaService.buscarPorTelefone(tel)).thenReturn(Optional.of(loja));
        when(anuncioService.listarPorLoja(loja)).thenReturn(List.of(a1, a2));

        MensagemChatbotResponse r = chatbotService.responderMensagem(tel, "/anunciar");
        assertTrue(r.resposta().contains("Limite de Anúncios Atingido"));
        assertTrue(r.resposta().contains("2 anúncios ativos"));
        verify(conversaService, never()).salvarEstado(anyString(), anyString(), anyMap());
    }

    @Test
    @DisplayName("Deve listar anúncios da loja ao enviar /meusanuncios")
    void deveListarMeusAnuncios() {
        String tel = "81999990001";
        Loja loja = criarLojaTeste(1L, "Tapiocaria", PlanoLoja.GRATUITO);

        Anuncio a = new Anuncio();
        a.setId(10L);
        a.setTitulo("Tapioca de Queijo");
        a.setPreco(new BigDecimal("12.00"));
        a.setStatus(StatusAnuncio.ATIVO);
        a.setDataExpiracao(LocalDateTime.now().plusDays(10));

        when(conversaService.obterEstado(tel)).thenReturn(Optional.empty());
        when(lojaService.buscarPorTelefone(tel)).thenReturn(Optional.of(loja));
        when(anuncioService.listarPorLoja(loja)).thenReturn(List.of(a));

        MensagemChatbotResponse r = chatbotService.responderMensagem(tel, "/meusanuncios");
        assertTrue(r.resposta().contains("Seus Anúncios"));
        assertTrue(r.resposta().contains("Tapioca de Queijo"));
        assertTrue(r.resposta().contains("/renovar 10"));
    }

    @Test
    @DisplayName("Deve renovar anúncio com comando /renovar <id>")
    void deveRenovarAnuncioViaComando() {
        String tel = "81999990001";
        Loja loja = criarLojaTeste(1L, "Tapiocaria", PlanoLoja.GRATUITO);

        Anuncio renovado = new Anuncio();
        renovado.setId(10L);
        renovado.setDataExpiracao(LocalDateTime.now().plusDays(15));

        when(conversaService.obterEstado(tel)).thenReturn(Optional.empty());
        when(lojaService.buscarPorTelefone(tel)).thenReturn(Optional.of(loja));
        when(anuncioService.renovar(10L, loja)).thenReturn(renovado);

        MensagemChatbotResponse r = chatbotService.responderMensagem(tel, "/renovar 10");
        assertTrue(r.resposta().contains("Renovado com Sucesso"));
        assertTrue(r.resposta().contains("#10"));
    }

    @Test
    @DisplayName("Deve pausar anúncio com comando /pausar <id>")
    void devePausarAnuncioViaComando() {
        String tel = "81999990001";
        Loja loja = criarLojaTeste(1L, "Tapiocaria", PlanoLoja.GRATUITO);

        when(conversaService.obterEstado(tel)).thenReturn(Optional.empty());
        when(lojaService.buscarPorTelefone(tel)).thenReturn(Optional.of(loja));

        MensagemChatbotResponse r = chatbotService.responderMensagem(tel, "/pausar 10");
        assertTrue(r.resposta().contains("pausado"));
        verify(anuncioService).pausar(10L, loja);
    }

    @Test
    @DisplayName("Deve realizar parse correto de preços em diferentes formatos")
    void deveParsearPrecosCorretamente() {
        assertEquals(new BigDecimal("12.50"), ChatbotService.parsePreco("12,50"));
        assertEquals(new BigDecimal("12.50"), ChatbotService.parsePreco("R$ 12,50"));
        assertEquals(new BigDecimal("12.50"), ChatbotService.parsePreco("12.50"));
        assertEquals(new BigDecimal("25"), ChatbotService.parsePreco("25"));
        assertNull(ChatbotService.parsePreco(""));
        assertNull(ChatbotService.parsePreco("gratis"));
    }
}
