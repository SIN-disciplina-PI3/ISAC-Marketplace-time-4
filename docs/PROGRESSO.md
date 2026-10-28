# Progresso do Projeto ISAC Marketplace

Este documento registra o estado atual do desenvolvimento, o que foi concluído, o que está pendente e como executar o sistema em cada fase.

## Status Geral: Fase 1 Concluída com Sucesso ✅

| Fase | Descrição | Status | Detalhes |
|---|---|---|---|
| **Fase 1** | Núcleo do Back-end (Spring Boot, Domínio, Flyway, H2, Serviços, Chatbot básico, Testes) | ✅ Concluída | 88 testes unitários/integração verdes, build success, JaCoCo gerado, dados demo e simulador REST ativos |
| **Fase 2** | WhatsApp Conversacional & Armazenamento (Máquina de estados, Evolution API, Fotos) | ⏳ Próxima | Máquina de estados conversacional, webhook da Evolution API, ArmazenamentoFotos |
| **Fase 3** | Segurança & Área Logada (Spring Security, JWT, Admin e Loja) | ⚪ Não iniciada | Autenticação JWT, endpoints protegidos |
| **Fase 4** | Front-end React (Vite, TS, Tailwind, Vitrine, Telas de Gestão, Vitest) | ⚪ Não iniciada | Interface SPA mobile-first e testes Vitest |
| **Fase 5** | Importador de WhatsApp & Métricas Admin (Parser de chat .txt, Heurísticas, Revisão) | ⚪ Não iniciada | Processamento de exportação do grupo .txt |
| **Fase 6** | Entrega & Infraestrutura (Docker Compose, CI, JaCoCo, Documentação Final) | ⚪ Não iniciada | Docker Compose, GitHub Actions, documentação completa |

---

## O que foi entregue na Fase 1
1. **Estrutura Base do Projeto:**
   - Projeto Maven (`backend/pom.xml`) com Java 17 e Spring Boot 3.2.5.
   - Migrações Flyway SQL ANSI (`V1__criacao_tabelas.sql`, `V2__indices.sql`) compatíveis com PostgreSQL e H2.
   - Banco de dados em memória H2 em modo PostgreSQL (`application-dev.yml`) e perfil PostgreSQL para produção (`application-prod.yml`).
   - Dados de demonstração fictícios com 5 lojas, 15 anúncios, 5 categorias, 2 eventos e conteúdos institucionais (`dados-demo.sql` carregado via `DataLoader`).

2. **Domínio e Utilitários:**
   - Entidades completas: `Loja`, `Anuncio`, `Categoria`, `Usuario`, `Pagamento`, `ConversaWhatsapp`, `Busca`, `Evento`, `Conteudo`, `Importacao`, `AnuncioImportado`.
   - Validadores de dígitos verificadores `CpfValidator` e `CnpjValidator` com suporte aos vetores do projeto (inclusive DV 0).
   - `TelefoneUtil` com hashing seguro SHA-256 para anonimização LGPD e geração de link `wa.me`.
   - `TextoUtil` para busca insensível a caixa e sem acentuação ("feijao" encontra "Feijão").

3. **Regras de Negócio e Serviços:**
   - `LojaService`: cadastro formal e simplificado via WhatsApp, unicidade de telefone/documento.
   - `AnuncioService`: regras de cota gratuita (máx. 2 anúncios ativos), expiração automática em 15 dias, renovação, moderação e disponibilidade.
   - `BuscaProdutoService`: ordenação com destaque de assinantes ativos em primeiro, paginação para web, e resultado único por loja para bot com links `wa.me`.
   - `AssinaturaService`: fluxo de upgrade, confirmação manual de pagamento por admin, inativação de assinaturas vencidas e pausa de anúncios excedentes da cota gratuita.
   - `AssinaturaScheduler` e `AnuncioScheduler`: rotinas diárias com `Clock` injetado e aviso de expiração prévia 2 dias antes.
   - `WhatsAppGateway`: interface com `LogWhatsAppGateway` (dev) e `EvolutionApiGateway` (prod).
   - `ChatbotService`: menu de opções, comandos e busca direta com remoção de prefixos conversacionais ("quero", "procuro", "tem").

4. **Controllers Públicos e Tratamento de Erros:**
   - `BuscaController` (`GET /api/busca`)
   - `AnuncioPublicoController` (`GET /api/anuncios/{id}`)
   - `CategoriaController` (`GET /api/categorias`)
   - `EventoController` (`GET /api/eventos`)
   - `ConteudoController` (`GET /api/conteudos/{chave}`)
   - `LojaController` (`POST /api/lojas`, `GET /api/lojas/{id}`)
   - `ChatbotController` (`POST /api/chatbot/mensagens`)
   - `GlobalExceptionHandler` padronizado sem vazamento de stack trace.

5. **Testes e Qualidade:**
   - **88 testes automatizados** (JUnit 5 + Mockito strict stubs + MockMvc + Spring Boot Test com H2).
   - **0 falhas, 0 erros, 0 ignorados**.
   - Relatório de cobertura JaCoCo configurado e gerado em `backend/target/site/jacoco/index.html`.

---

## Como Rodar o Projeto

```powershell
cd backend
.\mvn.cmd test
.\mvn.cmd spring-boot:run
```
