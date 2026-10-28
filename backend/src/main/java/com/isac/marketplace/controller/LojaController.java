package com.isac.marketplace.controller;

import com.isac.marketplace.dto.CadastroLojaDTO;
import com.isac.marketplace.dto.LojaDTO;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lojas")
public class LojaController {

    private final LojaService lojaService;

    public LojaController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @PostMapping
    public ResponseEntity<LojaDTO> cadastrar(@Valid @RequestBody CadastroLojaDTO dto) {
        Loja loja = lojaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(LojaDTO.fromEntity(loja));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LojaDTO> buscarPorId(@PathVariable Long id) {
        Loja loja = lojaService.buscarPorId(id);
        return ResponseEntity.ok(LojaDTO.fromEntity(loja));
    }
}
