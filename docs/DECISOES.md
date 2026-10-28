# Registro de Decisões Técnicas e Arquiteturais (ADR Simplificado)

Formato: `[Data] Decisão: O que foi decidido — Por quê foi decidido`.

- [2026-10-28] Estrutura monorepo: diretórios `backend`, `frontend`, `docs` na raiz do projeto — organização centralizada recomendada na especificação do projeto.
- [2026-10-28] Java 17 LTS e Spring Boot 3.2.5 como versão de framework — compatibilidade de longo suporte e atendimento às regras da seção 4.
- [2026-10-28] H2 em modo PostgreSQL (`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE`) para desenvolvimento e testes determinísticos sem dependência externa.
- [2026-10-28] Flyway para migrações com sintaxe SQL ANSI/PostgreSQL compatível com H2 e PostgreSQL.
- [2026-10-28] Reset de sequences identity no `dados-demo.sql` (`RESTART WITH 100`) para evitar conflito de chaves primárias nos inserts subsequentes de testes e dev.
- [2026-10-28] Injeção de `java.time.Clock` em todos os componentes temporais para garantir que os testes de agendamento e expiração sejam 100% determinísticos.
- [2026-10-28] Ajuste em um único passo no `AssinaturaService.ajustarCotaGratuitaParaLoja` para otimizar updates e evitar chamadas redundantes ao repositório.
- [2026-10-28] Separação entre saudação inicial e comando de ajuda no `ChatbotService` para que comandos `/ajuda` e opção `4` entreguem o texto instrutivo do marketplace.
