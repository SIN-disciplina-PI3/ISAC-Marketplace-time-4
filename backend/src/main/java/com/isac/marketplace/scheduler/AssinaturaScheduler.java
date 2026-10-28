package com.isac.marketplace.scheduler;

import com.isac.marketplace.service.AssinaturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AssinaturaScheduler {

    private static final Logger log = LoggerFactory.getLogger(AssinaturaScheduler.class);

    private final AssinaturaService assinaturaService;

    public AssinaturaScheduler(AssinaturaService assinaturaService) {
        this.assinaturaService = assinaturaService;
    }

    @Scheduled(cron = "${assinatura.cron:0 0 3 * * ?}")
    public int executarRotinaInativacao() {
        log.info("Executando rotina diária de verificação de assinaturas vencidas...");
        int total = assinaturaService.inativarAssinaturasVencidas();
        log.info("Rotina de assinaturas concluída. Total inativadas: {}", total);
        return total;
    }
}
