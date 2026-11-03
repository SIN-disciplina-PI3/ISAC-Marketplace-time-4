package com.isac.marketplace.controller;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.service.AnuncioService;
import com.isac.marketplace.storage.ArmazenamentoFotos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioFotoController {

    private final AnuncioService anuncioService;
    private final ArmazenamentoFotos armazenamentoFotos;

    public AnuncioFotoController(AnuncioService anuncioService, ArmazenamentoFotos armazenamentoFotos) {
        this.anuncioService = anuncioService;
        this.armazenamentoFotos = armazenamentoFotos;
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<AnuncioDTO> uploadFoto(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new RegraNegocioException("ARQUIVO_VAZIO", "O arquivo de foto é obrigatório.");
        }

        try {
            String url = armazenamentoFotos.armazenar(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );

            // Atualiza a foto do anúncio (para a loja dona do anúncio ou admin)
            Anuncio atualizado = anuncioService.atualizarFoto(id, url, null);
            return ResponseEntity.ok(AnuncioDTO.fromEntity(atualizado));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar bytes da foto enviada.", e);
        }
    }
}
