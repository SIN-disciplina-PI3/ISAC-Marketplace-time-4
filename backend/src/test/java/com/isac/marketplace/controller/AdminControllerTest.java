package com.isac.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isac.marketplace.dto.RejeitarAnuncioDTO;
import com.isac.marketplace.exception.GlobalExceptionHandler;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.*;
import com.isac.marketplace.service.AnuncioService;
import com.isac.marketplace.service.AssinaturaService;
import com.isac.marketplace.service.LojaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AdminController")
class AdminControllerTest {

    @Mock
    private LojaService lojaService;
    @Mock
    private AssinaturaService assinaturaService;
    @Mock
    private AnuncioService anuncioService;
    @Mock
    private LojaRepository lojaRepository;
    @Mock
    private AnuncioRepository anuncioRepository;
    @Mock
    private BuscaRepository buscaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private ConteudoRepository conteudoRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private UsernamePasswordAuthenticationToken adminAuth;

    @BeforeEach
    void setUp() {
        AdminController controller = new AdminController(
                lojaService,
                assinaturaService,
                anuncioService,
                lojaRepository,
                anuncioRepository,
                buscaRepository,
                categoriaRepository,
                eventoRepository,
                conteudoRepository
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        adminAuth = new UsernamePasswordAuthenticationToken(
                "admin@isac.org.br",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    @DisplayName("GET /api/admin/lojas - Deve listar lojas")
    void deveListarLojas() throws Exception {
        Loja loja = new Loja();
        loja.setId(1L);
        loja.setNomeFantasia("Loja Gaibu");

        when(lojaService.listarTodas(null)).thenReturn(List.of(loja));

        mockMvc.perform(get("/api/admin/lojas").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomeFantasia").value("Loja Gaibu"));
    }

    @Test
    @DisplayName("POST /api/admin/lojas/{id}/assinatura/pagamento - Deve confirmar pagamento")
    void deveConfirmarPagamento() throws Exception {
        Loja loja = new Loja();
        loja.setId(1L);
        loja.setNomeFantasia("Tapiocaria");
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.ATIVA);

        when(assinaturaService.confirmarPagamentoParaLoja(eq(1L), any())).thenReturn(loja);

        mockMvc.perform(post("/api/admin/lojas/1/assinatura/pagamento").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusAssinatura").value("ATIVA"));
    }

    @Test
    @DisplayName("POST /api/admin/lojas/{id}/assinatura/cancelamento - Deve cancelar assinatura")
    void deveCancelarAssinatura() throws Exception {
        Loja loja = new Loja();
        loja.setId(1L);
        loja.setStatusAssinatura(StatusAssinatura.INATIVA);

        when(assinaturaService.cancelarAssinatura(1L)).thenReturn(loja);

        mockMvc.perform(post("/api/admin/lojas/1/assinatura/cancelamento").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusAssinatura").value("INATIVA"));
    }

    @Test
    @DisplayName("POST /api/admin/assinaturas/vencidas/inativacao - Deve executar rotina de inativação manual")
    void deveInativarAssinaturasVencidas() throws Exception {
        when(assinaturaService.inativarAssinaturasVencidas()).thenReturn(3);

        mockMvc.perform(post("/api/admin/assinaturas/vencidas/inativacao").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inativadas").value(3));
    }

    @Test
    @DisplayName("POST /api/admin/anuncios/{id}/aprovar - Deve aprovar anúncio")
    void deveAprovarAnuncio() throws Exception {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(10L);
        anuncio.setTitulo("Bolo de Rolo");
        anuncio.setStatus(StatusAnuncio.ATIVO);

        when(anuncioService.aprovarModeracao(10L)).thenReturn(anuncio);

        mockMvc.perform(post("/api/admin/anuncios/10/aprovar").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("POST /api/admin/anuncios/{id}/rejeitar - Deve rejeitar anúncio com motivo")
    void deveRejeitarAnuncio() throws Exception {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(10L);
        anuncio.setTitulo("Item Ilegal");
        anuncio.setStatus(StatusAnuncio.REJEITADO);

        when(anuncioService.rejeitarModeracao(10L, "Conteúdo inadequado")).thenReturn(anuncio);

        mockMvc.perform(post("/api/admin/anuncios/10/rejeitar")
                        .principal(adminAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RejeitarAnuncioDTO("Conteúdo inadequado"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJEITADO"));
    }

    @Test
    @DisplayName("POST /api/admin/categorias - Deve criar categoria")
    void deveCriarCategoria() throws Exception {
        when(categoriaRepository.existsByNomeIgnoreCase("Culinária Local")).thenReturn(false);
        Categoria criada = new Categoria(10L, "Culinária Local", true);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(criada);

        mockMvc.perform(post("/api/admin/categorias")
                        .principal(adminAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("nome", "Culinária Local"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Culinária Local"));
    }

    @Test
    @DisplayName("DELETE /api/admin/categorias/{id} - Deve desativar categoria")
    void deveDesativarCategoria() throws Exception {
        Categoria categoria = new Categoria(2L, "Roupas", true);
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria));

        mockMvc.perform(delete("/api/admin/categorias/2").principal(adminAuth))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/admin/metricas - Deve retornar métricas consolidadas")
    void deveRetornarMetricas() throws Exception {
        when(anuncioRepository.countByStatus(StatusAnuncio.ATIVO)).thenReturn(25L);
        when(lojaRepository.countByStatusAssinatura(StatusAssinatura.ATIVA)).thenReturn(8L);
        when(lojaRepository.countByStatusAssinatura(StatusAssinatura.PENDENTE)).thenReturn(2L);
        when(lojaRepository.countByStatusAssinatura(StatusAssinatura.INATIVA)).thenReturn(4L);
        when(buscaRepository.count()).thenReturn(150L);
        when(buscaRepository.findTermosMaisBuscados()).thenReturn(List.<Object[]>of(new Object[]{"tapioca", 30L}));
        when(buscaRepository.findBuscasSemResultado()).thenReturn(List.<Object[]>of(new Object[]{"chaveiro", 5L}));

        mockMvc.perform(get("/api/admin/metricas").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAnunciosAtivos").value(25))
                .andExpect(jsonPath("$.totalLojasAtivas").value(8))
                .andExpect(jsonPath("$.totalLojasPendentes").value(2))
                .andExpect(jsonPath("$.totalLojasInativas").value(4))
                .andExpect(jsonPath("$.totalBuscas").value(150))
                .andExpect(jsonPath("$.termosMaisBuscados[0].termo").value("tapioca"))
                .andExpect(jsonPath("$.buscasSemResultado[0].termo").value("chaveiro"));
    }
}
