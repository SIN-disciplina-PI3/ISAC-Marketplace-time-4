package com.isac.marketplace.service;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.CategoriaRepository;
import com.isac.marketplace.util.TelefoneUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotService.class);

    private static final List<String> PREFIXOS_BUSCA = List.of(
            "onde compro", "quem vende", "quero comprar", "estou procurando",
            "quero", "procuro", "tem", "onde tem", "preciso de"
    );

    private static final Set<String> SAUDACOES = Set.of(
            "oi", "ola", "olá", "bom dia", "boa tarde", "boa noite",
            "menu", "inicio", "início", "/inicio", "/menu", "comecar", "começar"
    );

    // Etapas da máquina de estados conversacional
    public static final String ETAPA_CRIAR_LOJA_NOME = "ANUNCIAR_NOME_LOJA";
    public static final String ETAPA_CRIAR_LOJA_BAIRRO = "ANUNCIAR_BAIRRO_LOJA";
    public static final String ETAPA_TITULO = "ANUNCIAR_TITULO";
    public static final String ETAPA_CATEGORIA = "ANUNCIAR_CATEGORIA";
    public static final String ETAPA_PRECO = "ANUNCIAR_PRECO";
    public static final String ETAPA_BAIRRO = "ANUNCIAR_BAIRRO";
    public static final String ETAPA_FOTO = "ANUNCIAR_FOTO";
    public static final String ETAPA_CONFIRMACAO = "ANUNCIAR_CONFIRMACAO";

    private final BuscaProdutoService buscaProdutoService;
    private final ConversaService conversaService;
    private final LojaService lojaService;
    private final AnuncioService anuncioService;
    private final CategoriaRepository categoriaRepository;

    public ChatbotService(
            BuscaProdutoService buscaProdutoService,
            ConversaService conversaService,
            LojaService lojaService,
            AnuncioService anuncioService,
            CategoriaRepository categoriaRepository
    ) {
        this.buscaProdutoService = buscaProdutoService;
        this.conversaService = conversaService;
        this.lojaService = lojaService;
        this.anuncioService = anuncioService;
        this.categoriaRepository = categoriaRepository;
    }

    public MensagemChatbotResponse responderMensagem(String telefone, String texto) {
        if (telefone == null || telefone.isBlank()) {
            return new MensagemChatbotResponse("Telefone inválido.");
        }

        String telNormalizado = TelefoneUtil.normalizar(telefone);

        if (texto == null || texto.trim().isBlank()) {
            return new MensagemChatbotResponse(mensagemMenu());
        }

        String textoLimpo = texto.trim();
        String textoMinusculo = textoLimpo.toLowerCase();

        // 1. Comando global /cancelar aborta qualquer fluxo
        if (textoMinusculo.equals("/cancelar") || textoMinusculo.equals("cancelar")) {
            conversaService.limparEstado(telNormalizado);
            return new MensagemChatbotResponse("❌ Operação cancelada. Como posso ajudar? Envie *menu* para ver as opções.");
        }

        // 2. Se a conversa já possui uma etapa ativa na máquina de estados, continua o fluxo guiado
        Optional<ConversaService.EstadoConversa> estadoOpt = conversaService.obterEstado(telNormalizado);
        if (estadoOpt.isPresent()) {
            return processarFluxoGuiado(telNormalizado, estadoOpt.get(), textoLimpo);
        }

        // 3. Comandos de Meus Anúncios, Renovar e Pausar
        if (textoMinusculo.equals("3") || textoMinusculo.equals("/meusanuncios") || textoMinusculo.equals("/meus anúncios")) {
            return processarMeusAnuncios(telNormalizado);
        }

        if (textoMinusculo.startsWith("/renovar")) {
            return processarRenovacao(telNormalizado, textoLimpo);
        }

        if (textoMinusculo.startsWith("/pausar")) {
            return processarPausa(telNormalizado, textoLimpo);
        }

        if (textoMinusculo.startsWith("/reativar")) {
            return processarReativacao(telNormalizado, textoLimpo);
        }

        // 4. Iniciar fluxo guiado de anúncio
        if (textoMinusculo.equals("2") || textoMinusculo.equals("/anunciar")) {
            return iniciarFluxoAnuncio(telNormalizado);
        }

        // 5. Saudação ou Menu
        if (SAUDACOES.contains(textoMinusculo)) {
            return new MensagemChatbotResponse(mensagemMenu());
        }

        // 6. Opção 1 ou comando /buscar
        if (textoMinusculo.equals("1") || textoMinusculo.startsWith("/buscar")) {
            String termoAposComando = extrairArgumentoComando(textoLimpo, "/buscar");
            if (!termoAposComando.isBlank()) {
                return executarBusca(telNormalizado, termoAposComando);
            }
            return new MensagemChatbotResponse(
                    "🔍 *Como você deseja buscar?*\n\n" +
                    "Digite o que você procura (ex: *tapioca*, *bolo de rolo*, *peixe*, *diarista*)."
            );
        }

        // 7. Opção 4 ou Ajuda
        if (textoMinusculo.equals("4") || textoMinusculo.equals("/ajuda") || textoMinusculo.equals("ajuda")) {
            return new MensagemChatbotResponse(mensagemAjuda());
        }

        // 8. Texto livre que não é comando = busca direta por produto/serviço
        String termoExtraido = extrairTermoBusca(textoLimpo);
        return executarBusca(telNormalizado, termoExtraido);
    }

    // =========================================================================
    // MÁQUINA DE ESTADOS: FLUXO GUIADO DE ANÚNCIO
    // =========================================================================

    private MensagemChatbotResponse iniciarFluxoAnuncio(String telefone) {
        Optional<Loja> lojaOpt = lojaService.buscarPorTelefone(telefone);

        Map<String, Object> dados = new HashMap<>();

        if (lojaOpt.isEmpty()) {
            // Loja ainda não existe para esse WhatsApp: inicia cadastro simplificado
            conversaService.salvarEstado(telefone, ETAPA_CRIAR_LOJA_NOME, dados);
            return new MensagemChatbotResponse(
                    "👋 Olá! Identificamos que este é o seu primeiro anúncio no ISAC Marketplace.\n\n" +
                    "Para começar, qual é o *Nome do seu Comércio ou Serviço*? (Ex: _Tapiocaria da Maria_ ou _João Eletricista_)"
            );
        }

        Loja loja = lojaOpt.get();
        // Verifica se já atingiu o limite antes de iniciar perguntas
        if (loja.getPlano() == PlanoLoja.GRATUITO) {
            long totalAtivos = anuncioService.listarPorLoja(loja).stream()
                    .filter(a -> a.getStatus() == StatusAnuncio.ATIVO)
                    .count();
            if (totalAtivos >= AnuncioService.LIMITE_GRATUITO) {
                return new MensagemChatbotResponse(
                        "⚠️ *Limite de Anúncios Atingido!*\n\n" +
                        "Sua loja já atingiu o limite de *2 anúncios ativos* do plano gratuito.\n\n" +
                        "Para publicar anúncios ilimitados e ter destaque nas buscas, torne-se um parceiro *Assinante ISAC*!\n" +
                        "Envie */meusanuncios* para gerenciar ou pausar anúncios antigos."
                );
            }
        }

        dados.put("lojaId", loja.getId());
        dados.put("bairroLoja", loja.getBairro());
        conversaService.salvarEstado(telefone, ETAPA_TITULO, dados);

        return new MensagemChatbotResponse(
                "📢 *Novo Anúncio no ISAC Marketplace*\n\n" +
                "Qual é o *Título* do seu produto ou serviço?\n" +
                "_(Ex: Tapioca Tradicional com Queijo Coalho)_\n\n" +
                "_(Dica: envie /cancelar a qualquer momento para abortar)_"
        );
    }

    private MensagemChatbotResponse processarFluxoGuiado(String telefone, ConversaService.EstadoConversa estado, String entrada) {
        String etapa = estado.etapa();
        Map<String, Object> dados = new HashMap<>(estado.dados());

        switch (etapa) {
            case ETAPA_CRIAR_LOJA_NOME:
                if (entrada.isBlank() || entrada.length() < 3) {
                    return new MensagemChatbotResponse("Por favor, informe um nome válido para o seu comércio ou serviço (mínimo de 3 caracteres):");
                }
                dados.put("nomeLoja", entrada);
                conversaService.salvarEstado(telefone, ETAPA_CRIAR_LOJA_BAIRRO, dados);
                return new MensagemChatbotResponse(
                        "Ótimo! Em qual *Bairro* você está localizado?\n" +
                        "_(Ex: Enseada dos Corais, Gaibu, Suape, Paiva, Ponte dos Carvalhos)_"
                );

            case ETAPA_CRIAR_LOJA_BAIRRO:
                if (entrada.isBlank() || entrada.length() < 3) {
                    return new MensagemChatbotResponse("Por favor, informe o seu bairro no Cabo de Santo Agostinho:");
                }
                String nomeLoja = (String) dados.get("nomeLoja");
                Loja novaLoja = lojaService.cadastrarViaWhatsApp(nomeLoja, telefone, entrada);
                dados.put("lojaId", novaLovaId(novaLoja));
                dados.put("bairroLoja", novaLoja.getBairro());

                conversaService.salvarEstado(telefone, ETAPA_TITULO, dados);
                return new MensagemChatbotResponse(
                        "✅ Cadastro da sua loja concluído!\n\n" +
                        "Agora vamos ao seu anúncio. Qual é o *Título* do produto ou serviço?"
                );

            case ETAPA_TITULO:
                if (entrada.isBlank() || entrada.length() < 3) {
                    return new MensagemChatbotResponse("Por favor, digite um título claro com pelo menos 3 caracteres:");
                }
                dados.put("titulo", entrada);
                conversaService.salvarEstado(telefone, ETAPA_CATEGORIA, dados);

                List<Categoria> categorias = categoriaRepository.findByAtivaTrueOrderByNomeAsc();
                StringBuilder sbCat = new StringBuilder("Escolha a *Categoria* digitando o número correspondente:\n\n");
                for (int i = 0; i < categorias.size(); i++) {
                    sbCat.append(i + 1).append(") ").append(categorias.get(i).getNome()).append("\n");
                }
                return new MensagemChatbotResponse(sbCat.toString());

            case ETAPA_CATEGORIA:
                List<Categoria> cats = categoriaRepository.findByAtivaTrueOrderByNomeAsc();
                try {
                    int opc = Integer.parseInt(entrada.trim());
                    if (opc < 1 || opc > cats.size()) {
                        return new MensagemChatbotResponse("Opção inválida. Digite um número de 1 a " + cats.size() + ":");
                    }
                    Categoria escolhida = cats.get(opc - 1);
                    dados.put("categoriaId", escolhida.getId());
                    dados.put("categoriaNome", escolhida.getNome());
                } catch (NumberFormatException e) {
                    Optional<Categoria> catMatch = cats.stream()
                            .filter(c -> c.getNome().equalsIgnoreCase(entrada.trim()))
                            .findFirst();
                    if (catMatch.isPresent()) {
                        dados.put("categoriaId", catMatch.get().getId());
                        dados.put("categoriaNome", catMatch.get().getNome());
                    } else {
                        return new MensagemChatbotResponse("Por favor, digite apenas o número da categoria desejada:");
                    }
                }

                conversaService.salvarEstado(telefone, ETAPA_PRECO, dados);
                return new MensagemChatbotResponse(
                        "Qual é o *Preço*? (Ex: *12,50*, *R$ 12,50* ou *25*):"
                );

            case ETAPA_PRECO:
                BigDecimal preco = parsePreco(entrada);
                if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
                    return new MensagemChatbotResponse("Preço inválido. Digite um valor maior que zero (ex: *15,00* ou *20*):");
                }
                dados.put("preco", preco.toString());
                conversaService.salvarEstado(telefone, ETAPA_BAIRRO, dados);

                String bairroPadrao = (String) dados.getOrDefault("bairroLoja", "Enseada dos Corais");
                return new MensagemChatbotResponse(
                        "Qual o *Bairro* onde o produto está disponível?\n" +
                        "_(Responda com o nome do bairro ou envie *\"mesmo\"* para usar " + bairroPadrao + ")_:"
                );

            case ETAPA_BAIRRO:
                String bairroFinal;
                if (entrada.equalsIgnoreCase("mesmo") || entrada.isBlank()) {
                    bairroFinal = (String) dados.getOrDefault("bairroLoja", "Enseada dos Corais");
                } else {
                    bairroFinal = entrada.trim();
                }
                dados.put("bairro", bairroFinal);
                conversaService.salvarEstado(telefone, ETAPA_FOTO, dados);

                return new MensagemChatbotResponse(
                        "Deseja adicionar uma *Foto* ao anúncio?\n" +
                        "Envie o link da foto ou digite *\"pular\"* para continuar sem foto:"
                );

            case ETAPA_FOTO:
                String fotoUrl = null;
                if (!entrada.equalsIgnoreCase("pular") && !entrada.equalsIgnoreCase("não") && !entrada.equalsIgnoreCase("nao")) {
                    fotoUrl = entrada.trim();
                }
                dados.put("fotoUrl", fotoUrl);
                conversaService.salvarEstado(telefone, ETAPA_CONFIRMACAO, dados);

                BigDecimal precoFormatado = new BigDecimal((String) dados.get("preco"));
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                return new MensagemChatbotResponse(
                        "📋 *Confirmação do Anúncio*\n\n" +
                        "• *Título:* " + dados.get("titulo") + "\n" +
                        "• *Categoria:* " + dados.get("categoriaNome") + "\n" +
                        "• *Preço:* " + nf.format(precoFormatado) + "\n" +
                        "• *Bairro:* " + dados.get("bairro") + "\n" +
                        "• *Foto:* " + (fotoUrl != null ? "Anexada" : "Sem foto") + "\n\n" +
                        "Deseja publicar este anúncio? Responda *sim* para confirmar ou *não* para cancelar."
                );

            case ETAPA_CONFIRMACAO:
                String resp = entrada.trim().toLowerCase();
                if (resp.equals("sim") || resp.equals("s") || resp.equals("confirmar")) {
                    return finalizarPublicacaoAnuncio(telefone, dados);
                } else if (resp.equals("não") || resp.equals("nao") || resp.equals("n") || resp.equals("cancelar")) {
                    conversaService.limparEstado(telefone);
                    return new MensagemChatbotResponse("Operação cancelada. Seus dados foram descartados.");
                } else {
                    return new MensagemChatbotResponse("Por favor, responda apenas *sim* para confirmar ou *não* para cancelar:");
                }

            default:
                conversaService.limparEstado(telefone);
                return new MensagemChatbotResponse("Fluxo reiniciado. Envie *menu* para ver as opções disponíveis.");
        }
    }

    private Long novaLovaId(Loja loja) {
        return loja != null ? loja.getId() : null;
    }

    private MensagemChatbotResponse finalizarPublicacaoAnuncio(String telefone, Map<String, Object> dados) {
        Long lojaId = Long.valueOf(dados.get("lojaId").toString());
        Loja loja = lojaService.buscarPorId(lojaId);

        Long catId = dados.get("categoriaId") != null ? Long.valueOf(dados.get("categoriaId").toString()) : null;
        BigDecimal preco = new BigDecimal((String) dados.get("preco"));
        String titulo = (String) dados.get("titulo");
        String bairro = (String) dados.get("bairro");
        String fotoUrl = (String) dados.get("fotoUrl");

        CriarAnuncioDTO dto = new CriarAnuncioDTO(
                titulo,
                null,
                preco,
                catId,
                bairro,
                TipoAnuncio.PRODUTO,
                fotoUrl
        );

        try {
            Anuncio publicado = anuncioService.criarAnuncio(loja, dto);
            conversaService.limparEstado(telefone);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataExp = publicado.getDataExpiracao().format(dtf);

            return new MensagemChatbotResponse(
                    "🎉 *Anúncio Publicado com Sucesso!*\n\n" +
                    "• *Número:* #" + publicado.getId() + "\n" +
                    "• *Título:* " + publicado.getTitulo() + "\n" +
                    "• *Expira em:* " + dataExp + " (15 dias)\n\n" +
                    "💡 *Dicas úteis:*\n" +
                    "• Para renovar: /renovar " + publicado.getId() + "\n" +
                    "• Para pausar: /pausar " + publicado.getId() + "\n" +
                    "• Para ver todos: /meusanuncios"
            );
        } catch (RegraNegocioException e) {
            conversaService.limparEstado(telefone);
            return new MensagemChatbotResponse("⚠️ Não foi possível publicar: " + e.getMessage());
        }
    }

    // =========================================================================
    // GESTÃO DE ANÚNCIOS PELO WHATSAPP (/meusanuncios, /renovar, /pausar)
    // =========================================================================

    private MensagemChatbotResponse processarMeusAnuncios(String telefone) {
        Optional<Loja> lojaOpt = lojaService.buscarPorTelefone(telefone);
        if (lojaOpt.isEmpty()) {
            return new MensagemChatbotResponse(
                    "Você ainda não possui anúncios cadastrados.\nEnvie */anunciar* para publicar seu primeiro produto ou serviço!"
            );
        }

        Loja loja = lojaOpt.get();
        List<Anuncio> anuncios = anuncioService.listarPorLoja(loja);

        if (anuncios.isEmpty()) {
            return new MensagemChatbotResponse(
                    "Sua loja *" + loja.getNomeFantasia() + "* ainda não tem anúncios.\nEnvie */anunciar* para publicar!"
            );
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        StringBuilder sb = new StringBuilder();
        sb.append("📦 *Seus Anúncios (").append(loja.getNomeFantasia()).append(")*\n\n");

        for (Anuncio a : anuncios) {
            String icone = (a.getStatus() == StatusAnuncio.ATIVO) ? "🟢" : "⚪";
            sb.append(icone).append(" *#").append(a.getId()).append(" - ").append(a.getTitulo()).append("*\n");
            sb.append("   💰 ").append(nf.format(a.getPreco())).append(" | Status: ").append(a.getStatus()).append("\n");
            sb.append("   📅 Expiração: ").append(a.getDataExpiracao().format(dtf)).append("\n");
            sb.append("   Comandos: `/renovar ").append(a.getId()).append("` ou `/pausar ").append(a.getId()).append("`\n\n");
        }

        return new MensagemChatbotResponse(sb.toString().trim());
    }

    private MensagemChatbotResponse processarRenovacao(String telefone, String texto) {
        Optional<Loja> lojaOpt = lojaService.buscarPorTelefone(telefone);
        if (lojaOpt.isEmpty()) {
            return new MensagemChatbotResponse("Você não possui anúncios cadastrados para renovar.");
        }

        Long anuncioId = extrairIdComando(texto, "/renovar");
        if (anuncioId == null) {
            return new MensagemChatbotResponse("Por favor, informe o número do anúncio para renovar (ex: */renovar 12*).");
        }

        try {
            Anuncio renovado = anuncioService.renovar(anuncioId, lojaOpt.get());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return new MensagemChatbotResponse(
                    "🔄 *Anúncio #" + anuncioId + " Renovado com Sucesso!*\n\n" +
                    "• Nova data de expiração: *" + renovado.getDataExpiracao().format(dtf) + "* (15 dias a partir de hoje)."
            );
        } catch (RegraNegocioException e) {
            return new MensagemChatbotResponse("⚠️ " + e.getMessage());
        }
    }

    private MensagemChatbotResponse processarPausa(String telefone, String texto) {
        Optional<Loja> lojaOpt = lojaService.buscarPorTelefone(telefone);
        if (lojaOpt.isEmpty()) {
            return new MensagemChatbotResponse("Você não possui anúncios cadastrados.");
        }

        Long anuncioId = extrairIdComando(texto, "/pausar");
        if (anuncioId == null) {
            return new MensagemChatbotResponse("Por favor, informe o número do anúncio para pausar (ex: */pausar 12*).");
        }

        try {
            anuncioService.pausar(anuncioId, lojaOpt.get());
            return new MensagemChatbotResponse("⏸️ *Anúncio #" + anuncioId + " pausado.* Ele não aparecerá mais nas buscas até você reativá-lo com `/reativar " + anuncioId + "`.");
        } catch (RegraNegocioException e) {
            return new MensagemChatbotResponse("⚠️ " + e.getMessage());
        }
    }

    private MensagemChatbotResponse processarReativacao(String telefone, String texto) {
        Optional<Loja> lojaOpt = lojaService.buscarPorTelefone(telefone);
        if (lojaOpt.isEmpty()) {
            return new MensagemChatbotResponse("Você não possui anúncios cadastrados.");
        }

        Long anuncioId = extrairIdComando(texto, "/reativar");
        if (anuncioId == null) {
            return new MensagemChatbotResponse("Por favor, informe o número do anúncio para reativar (ex: */reativar 12*).");
        }

        try {
            anuncioService.reativar(anuncioId, lojaOpt.get());
            return new MensagemChatbotResponse("▶️ *Anúncio #" + anuncioId + " reativado com sucesso!*");
        } catch (RegraNegocioException e) {
            return new MensagemChatbotResponse("⚠️ " + e.getMessage());
        }
    }

    // =========================================================================
    // BUSCA E PARSER
    // =========================================================================

    public MensagemChatbotResponse executarBusca(String telefone, String termo) {
        if (termo == null || termo.trim().length() < 2) {
            return new MensagemChatbotResponse(
                    "Por favor, informe pelo menos 2 caracteres para buscar (ex: *tapioca*, *peixe*, *aulas*)."
            );
        }

        List<AnuncioDTO> resultados = buscaProdutoService.buscarParaBot(termo.trim(), null, telefone, 5);

        if (resultados.isEmpty()) {
            return new MensagemChatbotResponse(
                    "😔 Não encontrei nenhum produto ou serviço para *" + termo + "* no momento.\n\n" +
                    "Dica: tente palavras mais simples ou envie *menu* para ver as opções."
            );
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🛍️ *Encontrei os seguintes resultados para \"").append(termo).append("\":*\n\n");

        List<String> links = new ArrayList<>();
        NumberFormat nfMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        for (int i = 0; i < resultados.size(); i++) {
            AnuncioDTO anuncio = resultados.get(i);
            sb.append("🔹 *").append(anuncio.titulo()).append("*\n");
            sb.append("   💰 ").append(nfMoeda.format(anuncio.preco())).append("\n");
            sb.append("   🏪 ").append(anuncio.nomeLoja()).append("\n");
            sb.append("   📍 ").append(anuncio.bairro()).append("\n");
            if (anuncio.linkWaMe() != null && !anuncio.linkWaMe().isBlank()) {
                sb.append("   📲 Falar com vendedor: ").append(anuncio.linkWaMe()).append("\n");
                links.add(anuncio.linkWaMe());
            }
            sb.append("\n");
        }

        sb.append("💡 _Entre em contato diretamente com o vendedor clicando no link do WhatsApp!_");

        return new MensagemChatbotResponse(sb.toString(), links);
    }

    public String extrairTermoBusca(String texto) {
        String limpo = texto.trim();
        String lower = limpo.toLowerCase();

        for (String prefixo : PREFIXOS_BUSCA) {
            if (lower.startsWith(prefixo + " ")) {
                return limpo.substring(prefixo.length() + 1).trim();
            }
        }
        return limpo;
    }

    public static BigDecimal parsePreco(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            String limpo = texto.replaceAll("[^0-9,.]", "").trim();
            if (limpo.contains(",") && limpo.contains(".")) {
                limpo = limpo.replace(".", "").replace(",", ".");
            } else if (limpo.contains(",")) {
                limpo = limpo.replace(",", ".");
            }
            return new BigDecimal(limpo);
        } catch (Exception e) {
            return null;
        }
    }

    private String extrairArgumentoComando(String texto, String comando) {
        if (texto.length() <= comando.length()) {
            return "";
        }
        return texto.substring(comando.length()).trim();
    }

    private Long extrairIdComando(String texto, String comando) {
        String arg = extrairArgumentoComando(texto, comando);
        try {
            return Long.parseLong(arg.replaceAll("\\D", ""));
        } catch (Exception e) {
            return null;
        }
    }

    private String mensagemMenu() {
        return "👋 *Olá! Bem-vindo ao Marketplace Comunitário do ISAC!*\n" +
               "Conectamos produtores, artesãos, comerciantes e moradores do Cabo de Santo Agostinho (Enseada dos Corais, Gaibu e região).\n\n" +
               "Como posso ajudar hoje? Escolha uma opção ou digite o que procura:\n\n" +
               "1️⃣ *Buscar* (produtos, serviços ou comércios)\n" +
               "2️⃣ *Anunciar* (divulgue seu produto ou serviço)\n" +
               "3️⃣ *Meus anúncios* (gerencie suas publicações)\n" +
               "4️⃣ *Ajuda* (dúvidas e informações sobre o ISAC)\n\n" +
               "💡 _Você também pode apenas digitar o que procura, ex: \"quero tapioca\" ou \"bolo de rolo\"!_";
    }

    private String mensagemAjuda() {
        return "ℹ️ *Ajuda do ISAC Marketplace*\n\n" +
               "• *Como comprar:* Busque pelo nome do produto e fale diretamente com o morador/vendedor no WhatsApp dele.\n" +
               "• *Como anunciar:* Você pode anunciar gratuitamente até 2 itens ativos simultaneamente. Para catálogos maiores, temos planos de assinatura quinzenal com destaque.\n" +
               "• *Duração:* Cada anúncio fica ativo por 15 dias e pode ser renovado a qualquer momento com /renovar <número>.\n\n" +
               "Envie *menu* para voltar ao início.";
    }
}
