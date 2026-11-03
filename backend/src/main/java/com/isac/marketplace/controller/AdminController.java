package com.isac.marketplace.controller;

import com.isac.marketplace.dto.*;
import com.isac.marketplace.exception.RecursoNaoEncontradoException;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.*;
import com.isac.marketplace.service.AnuncioService;
import com.isac.marketplace.service.AssinaturaService;
import com.isac.marketplace.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final LojaService lojaService;
    private final AssinaturaService assinaturaService;
    private final AnuncioService anuncioService;
    private final LojaRepository lojaRepository;
    private final AnuncioRepository anuncioRepository;
    private final BuscaRepository buscaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EventoRepository eventoRepository;
    private final ConteudoRepository conteudoRepository;

    public AdminController(
            LojaService lojaService,
            AssinaturaService assinaturaService,
            AnuncioService anuncioService,
            LojaRepository lojaRepository,
            AnuncioRepository anuncioRepository,
            BuscaRepository buscaRepository,
            CategoriaRepository categoriaRepository,
            EventoRepository eventoRepository,
            ConteudoRepository conteudoRepository
    ) {
        this.lojaService = lojaService;
        this.assinaturaService = assinaturaService;
        this.anuncioService = anuncioService;
        this.lojaRepository = lojaRepository;
        this.anuncioRepository = anuncioRepository;
        this.buscaRepository = buscaRepository;
        this.categoriaRepository = categoriaRepository;
        this.eventoRepository = eventoRepository;
        this.conteudoRepository = conteudoRepository;
    }

    // -------------------------------------------------------------
    // LOJAS & ASSINATURAS
    // -------------------------------------------------------------

    @GetMapping("/api/admin/lojas")
    public ResponseEntity<List<LojaDTO>> listarLojas(@RequestParam(required = false) StatusAssinatura status) {
        List<Loja> lojas = lojaService.listarTodas(status);
        return ResponseEntity.ok(lojas.stream().map(LojaDTO::fromEntity).toList());
    }

    @GetMapping("/api/admin/lojas/{id}")
    public ResponseEntity<LojaDTO> buscarLojaPorId(@PathVariable Long id) {
        Loja loja = lojaService.buscarPorId(id);
        return ResponseEntity.ok(LojaDTO.fromEntity(loja));
    }

    @PostMapping({"/api/admin/lojas/{id}/assinatura/pagamento", "/api/lojas/{id}/assinatura/pagamento"})
    public ResponseEntity<LojaDTO> confirmarPagamentoAssinatura(@PathVariable Long id, Authentication authentication) {
        String adminEmail = (authentication != null && authentication.getName() != null) ? authentication.getName() : "ADMIN_ISAC";
        Loja loja = assinaturaService.confirmarPagamentoParaLoja(id, adminEmail);
        return ResponseEntity.ok(LojaDTO.fromEntity(loja));
    }

    @PostMapping({"/api/admin/lojas/{id}/assinatura/cancelamento", "/api/lojas/{id}/assinatura/cancelamento"})
    public ResponseEntity<LojaDTO> cancelarAssinatura(@PathVariable Long id) {
        Loja loja = assinaturaService.cancelarAssinatura(id);
        return ResponseEntity.ok(LojaDTO.fromEntity(loja));
    }

    @PostMapping({"/api/admin/assinaturas/vencidas/inativacao", "/api/assinaturas/vencidas/inativacao"})
    public ResponseEntity<Map<String, Object>> inativarAssinaturasVencidas() {
        int inativadas = assinaturaService.inativarAssinaturasVencidas();
        return ResponseEntity.ok(Map.of("inativadas", inativadas));
    }

    // -------------------------------------------------------------
    // ANÚNCIOS & MODERAÇÃO
    // -------------------------------------------------------------

    @GetMapping("/api/admin/anuncios")
    public ResponseEntity<List<AnuncioDTO>> listarAnuncios(@RequestParam(required = false) StatusAnuncio status) {
        List<Anuncio> anuncios;
        if (status != null) {
            anuncios = anuncioRepository.findByStatusOrderByDataPublicacaoDesc(status);
        } else {
            anuncios = anuncioRepository.findAllByOrderByDataPublicacaoDesc();
        }
        return ResponseEntity.ok(anuncios.stream().map(AnuncioDTO::fromEntity).toList());
    }

    @PostMapping("/api/admin/anuncios/{id}/aprovar")
    public ResponseEntity<AnuncioDTO> aprovarAnuncio(@PathVariable Long id) {
        Anuncio aprovado = anuncioService.aprovarModeracao(id);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(aprovado));
    }

    @PostMapping("/api/admin/anuncios/{id}/rejeitar")
    public ResponseEntity<AnuncioDTO> rejeitarAnuncio(
            @PathVariable Long id,
            @RequestBody(required = false) RejeitarAnuncioDTO body
    ) {
        String motivo = (body != null && body.motivo() != null) ? body.motivo() : "Anúncio não está em conformidade com as regras comunitárias do ISAC.";
        Anuncio rejeitado = anuncioService.rejeitarModeracao(id, motivo);
        return ResponseEntity.ok(AnuncioDTO.fromEntity(rejeitado));
    }

    // -------------------------------------------------------------
    // CATEGORIAS (CRUD)
    // -------------------------------------------------------------

    @GetMapping("/api/admin/categorias")
    public ResponseEntity<List<Categoria>> listarTodasCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @PostMapping("/api/admin/categorias")
    public ResponseEntity<Categoria> criarCategoria(@RequestBody Map<String, Object> body) {
        String nome = (String) body.get("nome");
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("NOME_OBRIGATORIO", "O nome da categoria é obrigatório.");
        }
        if (categoriaRepository.existsByNomeIgnoreCase(nome.trim())) {
            throw new RegraNegocioException("CATEGORIA_DUPLICADA", "Já existe uma categoria com este nome.");
        }
        Boolean ativa = body.containsKey("ativa") ? Boolean.valueOf(body.get("ativa").toString()) : true;
        Categoria categoria = new Categoria(null, nome.trim(), ativa);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaRepository.save(categoria));
    }

    @PutMapping("/api/admin/categorias/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("CATEGORIA_NAO_ENCONTRADA", "Categoria não encontrada."));

        if (body.containsKey("nome")) {
            String novoNome = (String) body.get("nome");
            if (novoNome != null && !novoNome.isBlank()) {
                categoria.setNome(novoNome.trim());
            }
        }
        if (body.containsKey("ativa")) {
            categoria.setAtiva(Boolean.valueOf(body.get("ativa").toString()));
        }
        return ResponseEntity.ok(categoriaRepository.save(categoria));
    }

    @DeleteMapping("/api/admin/categorias/{id}")
    public ResponseEntity<Void> desativarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("CATEGORIA_NAO_ENCONTRADA", "Categoria não encontrada."));
        categoria.setAtiva(false);
        categoriaRepository.save(categoria);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------
    // EVENTOS (CRUD)
    // -------------------------------------------------------------

    @GetMapping("/api/admin/eventos")
    public ResponseEntity<List<Evento>> listarTodosEventos() {
        return ResponseEntity.ok(eventoRepository.findAll());
    }

    @PostMapping("/api/admin/eventos")
    public ResponseEntity<Evento> criarEvento(@RequestBody Evento evento) {
        if (evento.getTitulo() == null || evento.getTitulo().isBlank()) {
            throw new RegraNegocioException("TITULO_OBRIGATORIO", "O título do evento é obrigatório.");
        }
        if (evento.getLocal() == null || evento.getLocal().isBlank()) {
            throw new RegraNegocioException("LOCAL_OBRIGATORIO", "O local do evento é obrigatório.");
        }
        if (evento.getDataInicio() == null) {
            evento.setDataInicio(LocalDateTime.now().plusDays(1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoRepository.save(evento));
    }

    @PutMapping("/api/admin/eventos/{id}")
    public ResponseEntity<Evento> atualizarEvento(@PathVariable Long id, @RequestBody Evento dados) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("EVENTO_NAO_ENCONTRADO", "Evento não encontrado."));

        if (dados.getTitulo() != null) evento.setTitulo(dados.getTitulo());
        if (dados.getDescricao() != null) evento.setDescricao(dados.getDescricao());
        if (dados.getLocal() != null) evento.setLocal(dados.getLocal());
        if (dados.getDataInicio() != null) evento.setDataInicio(dados.getDataInicio());
        if (dados.getDataFim() != null) evento.setDataFim(dados.getDataFim());
        if (dados.getPublicado() != null) evento.setPublicado(dados.getPublicado());

        return ResponseEntity.ok(eventoRepository.save(evento));
    }

    @DeleteMapping("/api/admin/eventos/{id}")
    public ResponseEntity<Void> excluirEvento(@PathVariable Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("EVENTO_NAO_ENCONTRADO", "Evento não encontrado."));
        eventoRepository.delete(evento);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------
    // CONTEÚDOS (CRUD / EDIÇÃO)
    // -------------------------------------------------------------

    @GetMapping("/api/admin/conteudos")
    public ResponseEntity<List<Conteudo>> listarTodosConteudos() {
        return ResponseEntity.ok(conteudoRepository.findAll());
    }

    @PostMapping("/api/admin/conteudos")
    public ResponseEntity<Conteudo> criarConteudo(@RequestBody Conteudo conteudo) {
        if (conteudo.getChave() == null || conteudo.getChave().isBlank()) {
            throw new RegraNegocioException("CHAVE_OBRIGATORIA", "A chave do conteúdo é obrigatória.");
        }
        if (conteudoRepository.existsByChave(conteudo.getChave().trim())) {
            throw new RegraNegocioException("CHAVE_DUPLICADA", "Já existe um conteúdo com esta chave.");
        }
        conteudo.setAtualizadoEm(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoRepository.save(conteudo));
    }

    @PutMapping("/api/admin/conteudos/{chave}")
    public ResponseEntity<Conteudo> atualizarConteudo(@PathVariable String chave, @RequestBody Conteudo dados) {
        Conteudo conteudo = conteudoRepository.findByChave(chave)
                .orElseThrow(() -> new RecursoNaoEncontradoException("CONTEUDO_NAO_ENCONTRADO", "Conteúdo com a chave '" + chave + "' não encontrado."));

        if (dados.getTitulo() != null && !dados.getTitulo().isBlank()) {
            conteudo.setTitulo(dados.getTitulo());
        }
        if (dados.getCorpoMarkdown() != null) {
            conteudo.setCorpoMarkdown(dados.getCorpoMarkdown());
        }
        conteudo.setAtualizadoEm(LocalDateTime.now());

        return ResponseEntity.ok(conteudoRepository.save(conteudo));
    }

    // -------------------------------------------------------------
    // MÉTRICAS
    // -------------------------------------------------------------

    @GetMapping("/api/admin/metricas")
    public ResponseEntity<MetricasDTO> obterMetricas() {
        long totalAnunciosAtivos = anuncioRepository.countByStatus(StatusAnuncio.ATIVO);
        long totalLojasAtivas = lojaRepository.countByStatusAssinatura(StatusAssinatura.ATIVA);
        long totalLojasPendentes = lojaRepository.countByStatusAssinatura(StatusAssinatura.PENDENTE);
        long totalLojasInativas = lojaRepository.countByStatusAssinatura(StatusAssinatura.INATIVA);
        long totalBuscas = buscaRepository.count();

        List<TermoMetricaDTO> termosMaisBuscados = buscaRepository.findTermosMaisBuscados().stream()
                .limit(10)
                .map(row -> new TermoMetricaDTO((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        List<TermoMetricaDTO> buscasSemResultado = buscaRepository.findBuscasSemResultado().stream()
                .limit(10)
                .map(row -> new TermoMetricaDTO((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        return ResponseEntity.ok(new MetricasDTO(
                totalAnunciosAtivos,
                totalLojasAtivas,
                totalLojasPendentes,
                totalLojasInativas,
                totalBuscas,
                termosMaisBuscados,
                buscasSemResultado
        ));
    }
}
