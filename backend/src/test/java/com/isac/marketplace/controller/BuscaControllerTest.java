package com.isac.marketplace.controller;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.ResultadoBuscaDTO;
import com.isac.marketplace.model.StatusAnuncio;
import com.isac.marketplace.model.TipoAnuncio;
import com.isac.marketplace.service.BuscaProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do BuscaController (MockMvc standalone)")
class BuscaControllerTest {

    @Mock
    private BuscaProdutoService buscaProdutoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BuscaController(buscaProdutoService)).build();
    }

    @Test
    @DisplayName("GET /api/busca - Deve retornar resultados com paginação")
    void deveRetornarBuscaComSucesso() throws Exception {
        AnuncioDTO anuncio = new AnuncioDTO(
                1L, 1L, "Tapiocaria", "81999990001",
                "Tapioca Tradicional", "Desc", new BigDecimal("12.00"),
                1L, "Alimentação", "Enseada dos Corais", TipoAnuncio.PRODUTO,
                null, StatusAnuncio.ATIVO, true, true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(15),
                "https://wa.me/5581999990001"
        );

        ResultadoBuscaDTO resultado = new ResultadoBuscaDTO(List.of(anuncio), 1, 0, 1);

        when(buscaProdutoService.buscarWeb(eq("tapioca"), isNull(), isNull(), eq(0), eq(12)))
                .thenReturn(resultado);

        mockMvc.perform(get("/api/busca").param("termo", "tapioca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.anuncios[0].titulo").value("Tapioca Tradicional"));
    }
}
