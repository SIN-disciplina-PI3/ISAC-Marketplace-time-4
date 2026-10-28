package com.isac.marketplace.whatsapp;

/**
 * Gateway de envio de mensagens do WhatsApp.
 */
public interface WhatsAppGateway {

    /**
     * Envia mensagem de texto para o número informado.
     * @param telefone Número do destinatário (apenas dígitos, ex: 81999990001)
     * @param texto Conteúdo da mensagem
     * @return true se o envio foi aceito/realizado com sucesso, false caso contrário
     */
    boolean enviarMensagem(String telefone, String texto);
}
