package com.isac.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isac.marketplace.dto.CadastroLojaDTO;
import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.exception.GlobalExceptionHandler;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.UsuarioRepository;
import com.isac.marketplace.service.AnuncioService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do MinhaLojaController")
class MinhaLojaControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LojaService lojaService;

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private AnuncioRepository anuncioRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Loja lojaExemplo;
    private Usuario usuarioLoja;
    private UsernamePasswordAuthenticationToken authPrincipal;

    @BeforeEach
    void setUp() {
        MinhaLojaController controller = new MinhaLojaController(usuarioRepository, lojaService, anuncioService, anuncioRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        lojaExemplo = new Loja();
        lojaExemplo.setId(5L);
        lojaExemplo.setNomeFantasia("Tapiocaria da Enseada");
        lojaExemplo.setTelefoneWhatsapp("81999990001");
        lojaExemplo.setBairro("Enseada dos Corais");
        lojaExemplo.setPlano(PlanoLoja.GRATUITO);
        lojaExemplo.setStatusAssinatura(StatusAssinatura.INATIVA);

        usuarioLoja = new Usuario(1L, "tapioca@isac-demo.local", "hash", Papel.LOJA, lojaExemplo, true);
        authPrincipal = new UsernamePasswordAuthenticationToken(
                "tapioca@isac-demo.local",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_LOJA"))
        );
    }

    @Test
    @DisplayName("GET /api/minha-loja - Deve retornar dados da loja autenticada")
    void deveRetornarDadosDaMinhaLoja() throws Exception {
        when(usuarioRepository.findByEmail("tapioca@isac-demo.local")).thenReturn(Optional.of(usuarioLoja));

        mockMvc.perform(get("/api/minha-loja").principal(authPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nomeFantasia").value("Tapiocaria da Enseada"));
    }

    @Test
    @DisplayName("PUT /api/minha-loja - Deve atualizar dados da loja")
    void deveAtualizarMinhaLoja() throws Exception {
        when(usuarioRepository.findByEmail("tapioca@isac-demo.local")).thenReturn(Optional.of(usuarioLoja));

        CadastroLojaDTO dto = new CadastroLojaDTO("Tapiocaria Gourmet", null, null, "Alimentação", "81999990001", "tapioca@isac.org.br", "Enseada dos Corais", "Av Beira Mar, 10", null);
        Loja atualizada = new Loja();
        atualizada.setId(5L);
        atualizada.setNomeFantasia("Tapiocaria Gourmet");

        when(lojaService.atualizar(eq(5L), any(CadastroLojaDTO.class))).thenReturn(atualizada);

        mockMvc.perform(put("/api/minha-loja")
                        .principal(authPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeFantasia").value("Tapiocaria Gourmet"));
    }

    @Test
    @DisplayName("GET /api/minha-loja/anuncios - Deve listar anúncios da loja autenticada")
    void deveListarMeusAnuncios() throws Exception {
        when(usuarioRepository.findByEmail("tapioca@isac-demo.local")).thenReturn(Optional.of(usuarioLoja));

        Anuncio a1 = new Anuncio();
        a1.setId(101L);
        a1.setLoja(lojaExemplo);
        a1.setTitulo("Tapioca de Queijo Coalho");
        a1.setPreco(new BigDecimal("12.00"));
        a1.setBairro("Enseada dos Corais");
        a1.setStatus(StatusAnuncio.ATIVO);
        a1.setDisponivel(true);
        a1.setDataPublicacao(LocalDateTime.now());
        a1.setDataExpiracao(LocalDateTime.now().plusDays(15));

        when(anuncioService.listarPorLoja(lojaExemplo)).thenReturn(List.of(a1));

        mockMvc.perform(get("/api/minha-loja/anuncios").principal(authPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Tapioca de Queijo Coalho"));
    }

    @Test
    @DisplayName("POST /api/minha-loja/anuncios - Deve criar anúncio para a loja")
    void deveCriarAnuncioParaMinhaLoja() throws Exception {
        when(usuarioRepository.findByEmail("tapioca@isac-demo.local")).thenReturn(Optional.of(usuarioLoja));

        CriarAnuncioDTO dto = new CriarAnuncioDTO("Tapioca Doce", "Coco com leite condensado", new BigDecimal("10.00"), 1L, "Enseada", TipoAnuncio.PRODUTO, null);

        Anuncio criado = new Anuncio();
        criado.setId(102L);
        criado.setLoja(lojaExemplo);
        criado.setTitulo("Tapioca Doce");
        criado.setPreco(new BigDecimal("10.00"));
        criado.setStatus(StatusAnuncio.ATIVO);
        criado.setDisponivel(true);

        when(anuncioService.criarAnuncio(eq(lojaExemplo), any(CriarAnuncioDTO.class))).thenReturn(criado);

        mockMvc.perform(post("/api/minha-loja/anuncios")
                        .principal(authPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(102))
                .andExpect(jsonPath("$.titulo").value("Tapioca Doce"));
    }

    @Test
    @DisplayName("GET /api/minha-loja/assinatura - Deve retornar status da assinatura e cota")
    void deveRetornarStatusAssinatura() throws Exception {
        when(usuarioRepository.findByEmail("tapioca@isac-demo.local")).thenReturn(Optional.of(usuarioLoja));
        when(anuncioRepository.countByLojaAndStatus(lojaExemplo, StatusAnuncio.ATIVO)).thenReturn(1L);

        mockMvc.perform(get("/api/minha-loja/assinatura").principal(authPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plano").value("GRATUITO"))
                .andExpect(jsonPath("$.cotaUsada").value(1))
                .andExpect(jsonPath("$.cotaLimite").value(2))
                .andExpect(jsonPath("$.ilimitado").value(false));
    }

    @Test
    @DisplayName("Deve falhar com erro de negócio quando usuário não possui loja vinculada")
    void deveFalharQuandoUsuarioSemLoja() throws Exception {
        Usuario usuarioSemLoja = new Usuario(2L, "admin@isac.org.br", "hash", Papel.ADMIN, null, true);
        when(usuarioRepository.findByEmail("admin@isac.org.br")).thenReturn(Optional.of(usuarioSemLoja));

        UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
                "admin@isac.org.br", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        mockMvc.perform(get("/api/minha-loja").principal(adminAuth))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("LOJA_NAO_VINCULADA"));
    }
}
