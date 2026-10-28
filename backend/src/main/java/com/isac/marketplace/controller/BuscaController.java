package com.isac.marketplace.controller;

import com.isac.marketplace.dto.ResultadoBuscaDTO;
import com.isac.marketplace.service.BuscaProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/busca")
public class BuscaController {

    private final BuscaProdutoService buscaProdutoService;

    public BuscaController(BuscaProdutoService buscaProdutoService) {
        this.buscaProdutoService = buscaProdutoService;
    }

    @GetMapping
    public ResponseEntity<ResultadoBuscaDTO> buscar(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) String bairro,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho
    ) {
        ResultadoBuscaDTO resultado = buscaProdutoService.buscarWeb(termo, categoria, bairro, pagina, tamanho);
        return ResponseEntity.ok(resultado);
    }
}
