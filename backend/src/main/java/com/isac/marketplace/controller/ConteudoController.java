package com.isac.marketplace.controller;

import com.isac.marketplace.exception.RecursoNaoEncontradoException;
import com.isac.marketplace.model.Conteudo;
import com.isac.marketplace.repository.ConteudoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conteudos")
public class ConteudoController {

    private final ConteudoRepository conteudoRepository;

    public ConteudoController(ConteudoRepository conteudoRepository) {
        this.conteudoRepository = conteudoRepository;
    }

    @GetMapping("/{chave}")
    public ResponseEntity<Conteudo> buscarPorChave(@PathVariable String chave) {
        Conteudo conteudo = conteudoRepository.findByChave(chave)
                .orElseThrow(() -> new RecursoNaoEncontradoException("CONTEUDO_NAO_ENCONTRADO", "Conteúdo com a chave '" + chave + "' não encontrado."));
        return ResponseEntity.ok(conteudo);
    }
}
