package com.isac.marketplace.controller;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.service.AnuncioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnuncioPublicoController {

    private final AnuncioService anuncioService;

    public AnuncioPublicoController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @GetMapping({"/api/anuncios/{id}", "/api/produtos/{id}"})
    public ResponseEntity<AnuncioDTO> buscarPorId(@PathVariable Long id) {
        Anuncio anuncio = anuncioService.buscarPorId(id);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(anuncio));
    }
}
