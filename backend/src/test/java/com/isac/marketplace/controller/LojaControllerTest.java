package com.isac.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.dto.CadastroLojaDTO;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.PlanoLoja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.service.LojaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do LojaController (MockMvc standalone)")
class LojaControllerTest {

    @Mock
    private LojaService lojaService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new LojaController(lojaService)).build();
    }

    @Test
    @DisplayName("POST /api/lojas - Deve cadastrar loja com sucesso e retornar 201 Created")
    void deveCadastrarLojaComSucesso() throws Exception {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Artesanato do Mar",
                null,
                null,
                "Artesanato",
                "81999990002",
                "artesanato@mar.local",
                "Enseada dos Corais",
                "Rua das Ostras, 10",
                null
        );

        Loja salva = new Loja();
        salva.setId(10L);
        salva.setNomeFantasia("Artesanato do Mar");
        salva.setBairro("Enseada dos Corais");
        salva.setTelefoneWhatsapp("81999990002");
        salva.setPlano(PlanoLoja.GRATUITO);
        salva.setStatusAssinatura(StatusAssinatura.INATIVA);
        salva.setDataCadastro(LocalDateTime.now());

        when(lojaService.cadastrar(any(CadastroLojaDTO.class))).thenReturn(salva);

        mockMvc.perform(post("/api/lojas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nomeFantasia").value("Artesanato do Mar"))
                .andExpect(jsonPath("$.plano").value("GRATUITO"));
    }

    @Test
    @DisplayName("GET /api/lojas/{id} - Deve retornar loja existente")
    void deveBuscarLojaPorId() throws Exception {
        Loja loja = new Loja();
        loja.setId(10L);
        loja.setNomeFantasia("Artesanato do Mar");
        loja.setBairro("Enseada dos Corais");
        loja.setTelefoneWhatsapp("81999990002");
        loja.setPlano(PlanoLoja.GRATUITO);
        loja.setStatusAssinatura(StatusAssinatura.INATIVA);

        when(lojaService.buscarPorId(10L)).thenReturn(loja);

        mockMvc.perform(get("/api/lojas/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nomeFantasia").value("Artesanato do Mar"));
    }
}
