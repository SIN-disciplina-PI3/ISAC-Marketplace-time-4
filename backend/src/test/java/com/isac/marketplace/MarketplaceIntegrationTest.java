package com.isac.marketplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.dto.CadastroLojaDTO;
import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.dto.MensagemChatbotRequest;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.TipoAnuncio;
import com.isac.marketplace.model.TipoDocumento;
import com.isac.marketplace.service.AnuncioService;
import com.isac.marketplace.service.AssinaturaService;
import com.isac.marketplace.service.LojaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("Testes de Integração de Ponta a Ponta do Marketplace")
class MarketplaceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LojaService lojaService;

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private AssinaturaService assinaturaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Fluxo completo: Cadastrar Loja -> Publicar Anúncio -> Buscar na Vitrine -> Consultar no Chatbot")
    void fluxoCompletoDePontaAPonta() throws Exception {
        // 1. Cadastra loja
        CadastroLojaDTO dtoLoja = new CadastroLojaDTO(
                "Doces da Enseada",
                "22.333.444/0001-81",
                TipoDocumento.CNPJ,
                "Alimentação",
                "81988887777",
                "doces@enseada.local",
                "Enseada dos Corais",
                "Rua do Sol, 50",
                "senha123"
        );

        mockMvc.perform(post("/api/lojas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoLoja)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeFantasia").value("Doces da Enseada"));

        Loja loja = lojaService.buscarPorTelefone("81988887777").orElseThrow();

        // 2. Publica Anúncio
        CriarAnuncioDTO dtoAnuncio = new CriarAnuncioDTO(
                "Cocada Cremosa de Maracujá",
                "Feita no tacho com fruta natural",
                new BigDecimal("8.50"),
                null,
                "Enseada dos Corais",
                TipoAnuncio.PRODUTO,
                null
        );

        anuncioService.criarAnuncio(loja, dtoAnuncio);

        // 3. Busca na Vitrine Web
        mockMvc.perform(get("/api/busca").param("termo", "cocada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.anuncios[?(@.titulo =~ /.*Cocada.*/)]").exists());

        // 4. Pergunta no Chatbot (Simulador)
        MensagemChatbotRequest requestBot = new MensagemChatbotRequest("81988880000", "quero cocada");

        mockMvc.perform(post("/api/chatbot/mensagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resposta", containsString("Cocada Cremosa")))
                .andExpect(jsonPath("$.resposta", containsString("Doces da Enseada")))
                .andExpect(jsonPath("$.resposta", containsString("wa.me/5581988887777")));
    }

    @Test
    @DisplayName("Cenário de cota do plano gratuito: 2 anúncios permitidos, 3º recusado")
    void cenarioCotaPlanoGratuito() {
        CadastroLojaDTO dtoLoja = new CadastroLojaDTO(
                "Artesanato Peixe-Boi",
                null,
                null,
                "Artesanato",
                "81999992222",
                null,
                "Gaibu",
                null,
                null
        );
        Loja loja = lojaService.cadastrar(dtoLoja);

        CriarAnuncioDTO a1 = new CriarAnuncioDTO("Item 1", "Desc", new BigDecimal("10.00"), null, null, null, null);
        CriarAnuncioDTO a2 = new CriarAnuncioDTO("Item 2", "Desc", new BigDecimal("20.00"), null, null, null, null);
        CriarAnuncioDTO a3 = new CriarAnuncioDTO("Item 3", "Desc", new BigDecimal("30.00"), null, null, null, null);

        // 1º e 2º devem passar
        anuncioService.criarAnuncio(loja, a1);
        anuncioService.criarAnuncio(loja, a2);

        // 3º deve falhar com COTA_EXCEDIDA
        org.junit.jupiter.api.Assertions.assertThrows(
                com.isac.marketplace.exception.RegraNegocioException.class,
                () -> anuncioService.criarAnuncio(loja, a3)
        );
    }

    @Test
    @DisplayName("Critério da Fase 1: POST /api/chatbot/mensagens com 'tapioca' retorna lojas da base demo")
    void deveResponderNoChatbotParaTapiocaRetornandoLojaDemo() throws Exception {
        MensagemChatbotRequest request = new MensagemChatbotRequest("81999990001", "tapioca");

        mockMvc.perform(post("/api/chatbot/mensagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resposta", containsString("Tapiocaria da Enseada")))
                .andExpect(jsonPath("$.resposta", containsString("Tapioca Tradicional")))
                .andExpect(jsonPath("$.resposta", containsString("wa.me/5581999990001")))
                .andExpect(jsonPath("$.links", hasItem(containsString("wa.me/5581999990001"))));
    }
}
