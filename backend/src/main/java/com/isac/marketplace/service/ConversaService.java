package com.isac.marketplace.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.model.ConversaWhatsapp;
import com.isac.marketplace.repository.ConversaWhatsappRepository;
import com.isac.marketplace.util.TelefoneUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ConversaService {

    private static final Logger log = LoggerFactory.getLogger(ConversaService.class);
    public static final int TIMEOUT_MINUTOS = 30;

    private final ConversaWhatsappRepository repository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public record EstadoConversa(
            String telefoneHash,
            String etapa,
            Map<String, Object> dados,
            LocalDateTime atualizadoEm
    ) {}

    public ConversaService(ConversaWhatsappRepository repository, ObjectMapper objectMapper, Clock clock) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public Optional<EstadoConversa> obterEstado(String telefone) {
        String hash = TelefoneUtil.gerarHash(telefone);
        if (hash == null) return Optional.empty();

        Optional<ConversaWhatsapp> conversaOpt = repository.findById(hash);
        if (conversaOpt.isEmpty()) {
            return Optional.empty();
        }

        ConversaWhatsapp conversa = conversaOpt.get();
        LocalDateTime agora = LocalDateTime.now(clock);

        // Verifica expiração de 30 minutos de inatividade
        if (conversa.getAtualizadoEm().isBefore(agora.minusMinutes(TIMEOUT_MINUTOS))) {
            log.info("Conversa do hash {} expirada por inatividade (> 30 min). Resetando estado.", hash);
            repository.delete(conversa);
            return Optional.empty();
        }

        Map<String, Object> dados = deserializarDados(conversa.getDadosParciais());
        return Optional.of(new EstadoConversa(hash, conversa.getEtapa(), dados, conversa.getAtualizadoEm()));
    }

    public void salvarEstado(String telefone, String etapa, Map<String, Object> dados) {
        String hash = TelefoneUtil.gerarHash(telefone);
        if (hash == null) return;

        String jsonDados = serializarDados(dados != null ? dados : new HashMap<>());
        LocalDateTime agora = LocalDateTime.now(clock);

        ConversaWhatsapp conversa = repository.findById(hash)
                .orElse(new ConversaWhatsapp());

        conversa.setTelefoneHash(hash);
        conversa.setEtapa(etapa);
        conversa.setDadosParciais(jsonDados);
        conversa.setAtualizadoEm(agora);

        repository.save(conversa);
    }

    public void limparEstado(String telefone) {
        String hash = TelefoneUtil.gerarHash(telefone);
        if (hash != null) {
            repository.deleteById(hash);
        }
    }

    private Map<String, Object> deserializarDados(String json) {
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Erro ao deserializar dados parciais da conversa: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private String serializarDados(Map<String, Object> dados) {
        try {
            return objectMapper.writeValueAsString(dados);
        } catch (Exception e) {
            log.warn("Erro ao serializar dados parciais da conversa: {}", e.getMessage());
            return "{}";
        }
    }
}
