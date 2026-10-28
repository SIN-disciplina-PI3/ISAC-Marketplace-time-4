package com.isac.marketplace.service;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.util.TelefoneUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ChatbotService {

    private static final List<String> PREFIXOS_BUSCA = List.of(
            "onde compro", "quem vende", "quero comprar", "estou procurando",
            "quero", "procuro", "tem", "onde tem", "preciso de"
    );

    private static final Set<String> SAUDACOES = Set.of(
            "oi", "ola", "olá", "bom dia", "boa tarde", "boa noite",
            "menu", "inicio", "início", "/inicio", "/menu", "comecar", "começar"
    );

    private final BuscaProdutoService buscaProdutoService;

    public ChatbotService(BuscaProdutoService buscaProdutoService) {
        this.buscaProdutoService = buscaProdutoService;
    }

    public MensagemChatbotResponse responderMensagem(String telefone, String texto) {
        if (texto == null || texto.trim().isBlank()) {
            return new MensagemChatbotResponse(mensagemMenu());
        }

        String textoLimpo = texto.trim();
        String textoMinusculo = textoLimpo.toLowerCase();

        // 1. Saudação ou Menu
        if (SAUDACOES.contains(textoMinusculo)) {
            return new MensagemChatbotResponse(mensagemMenu());
        }

        // 2. Opções numéricas ou comandos diretos do menu
        if (textoMinusculo.equals("1") || textoMinusculo.startsWith("/buscar")) {
            String termoAposComando = extrairArgumentoComando(textoLimpo, "/buscar");
            if (!termoAposComando.isBlank()) {
                return executarBusca(telefone, termoAposComando);
            }
            return new MensagemChatbotResponse(
                    "🔍 *Como você deseja buscar?*\n\n" +
                    "Digite o que você procura (ex: *tapioca*, *bolo de rolo*, *artesanato*, *pedreiro*)."
            );
        }

        if (textoMinusculo.equals("2") || textoMinusculo.equals("/anunciar")) {
            return new MensagemChatbotResponse(
                    "📢 *Criar Anúncio no ISAC Marketplace*\n\n" +
                    "Para anunciar seu produto ou serviço rapidamente, acesse a vitrine no site ou inicie o fluxo informando o título do produto!"
            );
        }

        if (textoMinusculo.equals("3") || textoMinusculo.equals("/meusanuncios") || textoMinusculo.equals("/meus anúncios")) {
            return new MensagemChatbotResponse(
                    "📦 *Seus Anúncios*\n\n" +
                    "Para gerenciar, pausar ou renovar seus anúncios, acesse sua área logada no site do ISAC Marketplace."
            );
        }

        if (textoMinusculo.equals("4") || textoMinusculo.equals("/ajuda")) {
            return new MensagemChatbotResponse(mensagemAjuda());
        }

        if (textoMinusculo.equals("/cancelar")) {
            return new MensagemChatbotResponse("Operação cancelada. Como posso ajudar? Envie *menu* para ver as opções.");
        }

        // 3. Texto livre tratado como busca por palavra-chave
        String termoExtraido = extrairTermoBusca(textoLimpo);
        return executarBusca(telefone, termoExtraido);
    }

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

    private String extrairArgumentoComando(String texto, String comando) {
        if (texto.length() <= comando.length()) {
            return "";
        }
        return texto.substring(comando.length()).trim();
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
               "• *Duração:* Cada anúncio fica ativo por 15 dias e pode ser renovado a qualquer momento.\n\n" +
               "Envie *menu* para voltar ao início.";
    }
}
