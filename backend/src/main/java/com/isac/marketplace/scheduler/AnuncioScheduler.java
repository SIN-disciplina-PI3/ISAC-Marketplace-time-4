package com.isac.marketplace.scheduler;

import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.model.StatusAnuncio;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.whatsapp.WhatsAppGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AnuncioScheduler {

    private static final Logger log = LoggerFactory.getLogger(AnuncioScheduler.class);

    private final AnuncioRepository anuncioRepository;
    private final WhatsAppGateway whatsAppGateway;
    private final Clock clock;
    private final int diasAvisoPrevio;

    public AnuncioScheduler(
            AnuncioRepository anuncioRepository,
            WhatsAppGateway whatsAppGateway,
            Clock clock,
            @Value("${anuncio.dias-aviso-previo:2}") int diasAvisoPrevio
    ) {
        this.anuncioRepository = anuncioRepository;
        this.whatsAppGateway = whatsAppGateway;
        this.clock = clock;
        this.diasAvisoPrevio = diasAvisoPrevio;
    }

    @Scheduled(cron = "${anuncio.cron-expiracao:0 0 2 * * ?}")
    @Transactional
    public int expirarAnunciosVencidos() {
        LocalDateTime agora = LocalDateTime.now(clock);
        List<Anuncio> vencidos = anuncioRepository.findByStatusAndDataExpiracaoBefore(StatusAnuncio.ATIVO, agora);

        for (Anuncio anuncio : vencidos) {
            anuncio.setStatus(StatusAnuncio.EXPIRADO);
            anuncioRepository.save(anuncio);
            log.info("Anúncio '{}' (ID {}) marcado como EXPIRADO.", anuncio.getTitulo(), anuncio.getId());
        }

        return vencidos.size();
    }

    @Scheduled(cron = "0 0 10 * * ?")
    @Transactional
    public int enviarAvisosPreExpiracao() {
        LocalDateTime agora = LocalDateTime.now(clock);
        LocalDateTime limiteAviso = agora.plusDays(diasAvisoPrevio);

        List<Anuncio> prestesAExpirar = anuncioRepository.findByStatusAndAvisoExpiracaoEnviadoFalseAndDataExpiracaoBetween(
                StatusAnuncio.ATIVO, agora, limiteAviso
        );

        int totalAvisos = 0;
        for (Anuncio anuncio : prestesAExpirar) {
            if (anuncio.getLoja() != null && anuncio.getLoja().getTelefoneWhatsapp() != null) {
                String mensagem = String.format(
                        "Olá, %s! Seu anúncio *%s* no ISAC Marketplace vai expirar em menos de 2 dias. " +
                        "Para mantê-lo ativo, envie /renovar %d ou acesse o painel da sua loja.",
                        anuncio.getLoja().getNomeFantasia(),
                        anuncio.getTitulo(),
                        anuncio.getId()
                );
                boolean enviado = whatsAppGateway.enviarMensagem(anuncio.getLoja().getTelefoneWhatsapp(), mensagem);
                if (enviado) {
                    anuncio.setAvisoExpiracaoEnviado(true);
                    anuncioRepository.save(anuncio);
                    totalAvisos++;
                }
            }
        }

        return totalAvisos;
    }
}
