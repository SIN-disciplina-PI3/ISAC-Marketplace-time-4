package com.isac.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.dto.AlterarDisponibilidadeDTO;
import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.exception.GlobalExceptionHandler;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.UsuarioRepository;
import com.isac.marketplace.service.AnuncioService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AnuncioLojaController")
class AnuncioLojaControllerTest {

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Loja loja;
    private Usuario usuario;
    private UsernamePasswordAuthenticationToken authPrincipal;

    @BeforeEach
    void setUp() {
        AnuncioLojaController controller = new AnuncioLojaController(anuncioService, usuarioRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        loja = new Loja();
        loja.setId(10L);
        loja.setNomeFantasia("Loja Gaibu");

        usuario = new Usuario(1L, "gaibu@isac.local", "hash", Papel.LOJA, loja, true);
        authPrincipal = new UsernamePasswordAuthenticationToken(
                "gaibu@isac.local", null, List.of(new SimpleGrantedAuthority("ROLE_LOJA"))
        );
    }

    @Test
    @DisplayName("PUT /api/anuncios/{id} - Deve atualizar anúncio com sucesso")
    void deveAtualizarAnuncioComSucesso() throws Exception {
        when(usuarioRepository.findByEmail("gaibu@isac.local")).thenReturn(Optional.of(usuario));

        CriarAnuncioDTO dto = new CriarAnuncioDTO("Cuscuz com queijo atualizado", null, new BigDecimal("15.00"), 1L, "Gaibu", TipoAnuncio.PRODUTO, null);

        Anuncio atualizado = new Anuncio();
        atualizado.setId(50L);
        atualizado.setLoja(loja);
        atualizado.setTitulo("Cuscuz com queijo atualizado");
        atualizado.setPreco(new BigDecimal("15.00"));
        atualizado.setStatus(StatusAnuncio.ATIVO);
        atualizado.setDisponivel(true);

        when(anuncioService.atualizar(eq(50L), any(CriarAnuncioDTO.class), eq(loja))).thenReturn(atualizado);

        mockMvc.perform(put("/api/anuncios/50")
                        .principal(authPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.titulo").value("Cuscuz com queijo atualizado"));
    }

    @Test
    @DisplayName("PUT /api/anuncios/{id} - Deve falhar se anúncio não pertencer à loja autenticada")
    void deveFalharSeNaoPertencerALoja() throws Exception {
        when(usuarioRepository.findByEmail("gaibu@isac.local")).thenReturn(Optional.of(usuario));

        CriarAnuncioDTO dto = new CriarAnuncioDTO("Titulo", null, new BigDecimal("15.00"), 1L, "Gaibu", TipoAnuncio.PRODUTO, null);

        when(anuncioService.atualizar(eq(50L), any(CriarAnuncioDTO.class), eq(loja)))
                .thenThrow(new RegraNegocioException("ACESSO_NEGADO", "Você não tem permissão para editar este anúncio."));

        mockMvc.perform(put("/api/anuncios/50")
                        .principal(authPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("ACESSO_NEGADO"));
    }

    @Test
    @DisplayName("PATCH /api/anuncios/{id}/disponibilidade - Deve alterar disponibilidade")
    void deveAlterarDisponibilidade() throws Exception {
        when(usuarioRepository.findByEmail("gaibu@isac.local")).thenReturn(Optional.of(usuario));

        Anuncio atualizado = new Anuncio();
        atualizado.setId(50L);
        atualizado.setLoja(loja);
        atualizado.setTitulo("Item");
        atualizado.setDisponivel(false);
        atualizado.setStatus(StatusAnuncio.ATIVO);

        when(anuncioService.alterarDisponibilidade(50L, false, loja)).thenReturn(atualizado);

        mockMvc.perform(patch("/api/anuncios/50/disponibilidade")
                        .principal(authPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AlterarDisponibilidadeDTO(false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponivel").value(false));
    }

    @Test
    @DisplayName("POST /api/anuncios/{id}/renovar - Deve renovar anúncio com sucesso")
    void deveRenovarAnuncio() throws Exception {
        when(usuarioRepository.findByEmail("gaibu@isac.local")).thenReturn(Optional.of(usuario));

        Anuncio renovado = new Anuncio();
        renovado.setId(50L);
        renovado.setLoja(loja);
        renovado.setTitulo("Item");
        renovado.setStatus(StatusAnuncio.ATIVO);
        renovado.setDisponivel(true);

        when(anuncioService.renovar(50L, loja)).thenReturn(renovado);

        mockMvc.perform(post("/api/anuncios/50/renovar").principal(authPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("POST /api/anuncios/{id}/pausar - Deve pausar anúncio com sucesso")
    void devePausarAnuncio() throws Exception {
        when(usuarioRepository.findByEmail("gaibu@isac.local")).thenReturn(Optional.of(usuario));

        Anuncio pausado = new Anuncio();
        pausado.setId(50L);
        pausado.setLoja(loja);
        pausado.setTitulo("Item");
        pausado.setStatus(StatusAnuncio.PAUSADO);

        when(anuncioService.pausar(50L, loja)).thenReturn(pausado);

        mockMvc.perform(post("/api/anuncios/50/pausar").principal(authPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSADO"));
    }

    @Test
    @DisplayName("POST /api/anuncios/{id}/reativar - Deve reativar anúncio com sucesso")
    void deveReativarAnuncio() throws Exception {
        when(usuarioRepository.findByEmail("gaibu@isac.local")).thenReturn(Optional.of(usuario));

        Anuncio reativado = new Anuncio();
        reativado.setId(50L);
        reativado.setLoja(loja);
        reativado.setTitulo("Item");
        reativado.setStatus(StatusAnuncio.ATIVO);

        when(anuncioService.reativar(50L, loja)).thenReturn(reativado);

        mockMvc.perform(post("/api/anuncios/50/reativar").principal(authPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }
}
