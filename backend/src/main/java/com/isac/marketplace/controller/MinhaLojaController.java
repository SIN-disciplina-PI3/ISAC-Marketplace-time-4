package com.isac.marketplace.controller;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.AssinaturaDTO;
import com.isac.marketplace.dto.CadastroLojaDTO;
import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.dto.LojaDTO;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Papel;
import com.isac.marketplace.model.StatusAnuncio;
import com.isac.marketplace.model.Usuario;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.UsuarioRepository;
import com.isac.marketplace.service.AnuncioService;
import com.isac.marketplace.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/minha-loja")
public class MinhaLojaController {

    private final UsuarioRepository usuarioRepository;
    private final LojaService lojaService;
    private final AnuncioService anuncioService;
    private final AnuncioRepository anuncioRepository;

    public MinhaLojaController(
            UsuarioRepository usuarioRepository,
            LojaService lojaService,
            AnuncioService anuncioService,
            AnuncioRepository anuncioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.lojaService = lojaService;
        this.anuncioService = anuncioService;
        this.anuncioRepository = anuncioRepository;
    }

    private Loja obterLojaAutenticada(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RegraNegocioException("NAO_AUTENTICADO", "Usuário não autenticado.");
        }
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RegraNegocioException("USUARIO_NAO_ENCONTRADO", "Usuário não encontrado."));

        if (usuario.getLoja() == null) {
            throw new RegraNegocioException("LOJA_NAO_VINCULADA", "Este usuário não possui uma loja associada.");
        }
        return usuario.getLoja();
    }

    @GetMapping
    public ResponseEntity<LojaDTO> obterDadosMinhaLoja(Authentication authentication) {
        Loja loja = obterLojaAutenticada(authentication);
        return ResponseEntity.ok(LojaDTO.fromEntity(loja));
    }

    @PutMapping
    public ResponseEntity<LojaDTO> atualizarMinhaLoja(@Valid @RequestBody CadastroLojaDTO dto, Authentication authentication) {
        Loja loja = obterLojaAutenticada(authentication);
        Loja atualizada = lojaService.atualizar(loja.getId(), dto);
        return ResponseEntity.ok(LojaDTO.fromEntity(atualizada));
    }

    @GetMapping("/anuncios")
    public ResponseEntity<List<AnuncioDTO>> listarMeusAnuncios(Authentication authentication) {
        Loja loja = obterLojaAutenticada(authentication);
        List<Anuncio> anuncios = anuncioService.listarPorLoja(loja);
        List<AnuncioDTO> dtos = anuncios.stream().map(AnuncioDTO::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/anuncios")
    public ResponseEntity<AnuncioDTO> criarAnuncio(@Valid @RequestBody CriarAnuncioDTO dto, Authentication authentication) {
        Loja loja = obterLojaAutenticada(authentication);
        Anuncio criado = anuncioService.criarAnuncio(loja, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(AnuncioDTO.fromEntity(criado));
    }

    @GetMapping("/assinatura")
    public ResponseEntity<AssinaturaDTO> obterDadosAssinatura(Authentication authentication) {
        Loja loja = obterLojaAutenticada(authentication);
        long cotaUsada = anuncioRepository.countByLojaAndStatus(loja, StatusAnuncio.ATIVO);
        boolean ilimitado = loja.isAssinanteAtivo();
        int limite = ilimitado ? 999 : AnuncioService.LIMITE_GRATUITO;

        return ResponseEntity.ok(new AssinaturaDTO(
                loja.getPlano(),
                loja.getStatusAssinatura(),
                loja.getDataVencimentoAssinatura(),
                cotaUsada,
                limite,
                ilimitado
        ));
    }
}
