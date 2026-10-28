package com.isac.marketplace.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gateway de envio para desenvolvimento e testes.
 * Registra as mensagens em log e armazena em memória para asserções de teste.
 */
@Component
@ConditionalOnProperty(name = "whatsapp.evolution.enabled", havingValue = "false", matchIfMissing = true)
public class LogWhatsAppGateway implements WhatsAppGateway {

    private static final Logger log = LoggerFactory.getLogger(LogWhatsAppGateway.class);

    public record MensagemEnviada(String telefone, String texto) {}

    private final List<MensagemEnviada> mensagensEnviadas = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean enviarMensagem(String telefone, String texto) {
        log.info("[LOG-WHATSAPP] Destinatário: {} | Mensagem:\n{}", telefone, texto);
        mensagensEnviadas.add(new MensagemEnviada(telefone, texto));
        return true;
    }

    public List<MensagemEnviada> getMensagensEnviadas() {
        return Collections.unmodifiableList(mensagensEnviadas);
    }

    public void limpar() {
        mensagensEnviadas.clear();
    }
}
