package com.isac.marketplace.controller;

import com.isac.marketplace.dto.AlterarDisponibilidadeDTO;
import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Papel;
import com.isac.marketplace.model.Usuario;
import com.isac.marketplace.repository.UsuarioRepository;
import com.isac.marketplace.service.AnuncioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnuncioLojaController {

    private final AnuncioService anuncioService;
    private final UsuarioRepository usuarioRepository;

    public AnuncioLojaController(AnuncioService anuncioService, UsuarioRepository usuarioRepository) {
        this.anuncioService = anuncioService;
        this.usuarioRepository = usuarioRepository;
    }

    private Loja obterLojaSeNaoAdmin(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RegraNegocioException("NAO_AUTENTICADO", "Usuário não autenticado.");
        }
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RegraNegocioException("USUARIO_NAO_ENCONTRADO", "Usuário não encontrado."));

        if (usuario.getPapel() == Papel.ADMIN) {
            return null; // Admin pode gerenciar anúncios de qualquer loja
        }

        if (usuario.getLoja() == null) {
            throw new RegraNegocioException("LOJA_NAO_VINCULADA", "Este usuário não possui uma loja associada.");
        }
        return usuario.getLoja();
    }

    @PutMapping({"/api/anuncios/{id}", "/api/produtos/{id}"})
    public ResponseEntity<AnuncioDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CriarAnuncioDTO dto,
            Authentication authentication
    ) {
        Loja loja = obterLojaSeNaoAdmin(authentication);
        Anuncio atualizado = anuncioService.atualizar(id, dto, loja);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(atualizado));
    }

    @PatchMapping({"/api/anuncios/{id}/disponibilidade", "/api/produtos/{id}/disponibilidade"})
    public ResponseEntity<AnuncioDTO> alterarDisponibilidade(
            @PathVariable Long id,
            @RequestBody(required = false) AlterarDisponibilidadeDTO body,
            Authentication authentication
    ) {
        Loja loja = obterLojaSeNaoAdmin(authentication);
        Anuncio anuncio = anuncioService.buscarPorId(id);
        boolean novaDisponibilidade = (body != null && body.disponivel() != null)
                ? body.disponivel()
                : !Boolean.TRUE.equals(anuncio.getDisponivel());

        Anuncio atualizado = anuncioService.alterarDisponibilidade(id, novaDisponibilidade, loja);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(atualizado));
    }

    @PostMapping({"/api/anuncios/{id}/renovar", "/api/produtos/{id}/renovar"})
    public ResponseEntity<AnuncioDTO> renovar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Loja loja = obterLojaSeNaoAdmin(authentication);
        Anuncio renovado = anuncioService.renovar(id, loja);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(renovado));
    }

    @PostMapping({"/api/anuncios/{id}/pausar", "/api/produtos/{id}/pausar"})
    public ResponseEntity<AnuncioDTO> pausar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Loja loja = obterLojaSeNaoAdmin(authentication);
        Anuncio pausado = anuncioService.pausar(id, loja);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(pausado));
    }

    @PostMapping({"/api/anuncios/{id}/reativar", "/api/produtos/{id}/reativar"})
    public ResponseEntity<AnuncioDTO> reativar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Loja loja = obterLojaSeNaoAdmin(authentication);
        Anuncio reativado = anuncioService.reativar(id, loja);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(reativado));
    }
}
