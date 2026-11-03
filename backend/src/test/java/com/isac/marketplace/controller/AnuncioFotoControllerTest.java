package com.isac.marketplace.controller;

import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAnuncio;
import com.isac.marketplace.service.AnuncioService;
import com.isac.marketplace.storage.ArmazenamentoFotos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AnuncioFotoController (Upload multipart)")
class AnuncioFotoControllerTest {

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private ArmazenamentoFotos armazenamentoFotos;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AnuncioFotoController(anuncioService, armazenamentoFotos)).build();
    }

    @Test
    @DisplayName("POST /api/anuncios/{id}/foto - Deve fazer upload da foto com sucesso")
    void deveFazerUploadDaFotoComSucesso() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "foto", "tapioca.jpg", "image/jpeg", "conteudo-foto".getBytes()
        );

        Loja loja = new Loja();
        loja.setId(1L);
        loja.setNomeFantasia("Tapiocaria");
        loja.setTelefoneWhatsapp("81999990001");

        Anuncio anuncioAtualizado = new Anuncio();
        anuncioAtualizado.setId(10L);
        anuncioAtualizado.setLoja(loja);
        anuncioAtualizado.setTitulo("Tapioca Quentinha");
        anuncioAtualizado.setPreco(new BigDecimal("12.00"));
        anuncioAtualizado.setStatus(StatusAnuncio.ATIVO);
        anuncioAtualizado.setFotoUrl("/uploads/anuncios/foto123.jpg");
        anuncioAtualizado.setDataPublicacao(LocalDateTime.now());
        anuncioAtualizado.setDataExpiracao(LocalDateTime.now().plusDays(15));

        when(armazenamentoFotos.armazenar(any(), eq("tapioca.jpg"), eq("image/jpeg")))
                .thenReturn("/uploads/anuncios/foto123.jpg");
        when(anuncioService.atualizarFoto(eq(10L), eq("/uploads/anuncios/foto123.jpg"), isNull()))
                .thenReturn(anuncioAtualizado);

        mockMvc.perform(multipart("/api/anuncios/10/foto").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.fotoUrl").value("/uploads/anuncios/foto123.jpg"));
    }
}
