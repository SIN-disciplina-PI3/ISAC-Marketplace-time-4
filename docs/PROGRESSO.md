# Progresso do Projeto ISAC Marketplace

Este documento registra o estado atual do desenvolvimento, o que foi concluído, o que está pendente e como executar o sistema em cada fase.

## Status Geral: Fase 2 Concluída com Sucesso ✅

| Fase | Descrição | Status | Detalhes |
|---|---|---|---|
| **Fase 1** | Núcleo do Back-end (Spring Boot, Domínio, Flyway, H2, Serviços, Chatbot básico, Testes) | ✅ Concluída | 88 testes unitários/integração verdes, build success, JaCoCo gerado, dados demo e simulador REST ativos |
| **Fase 2** | WhatsApp Conversacional & Armazenamento (Máquina de estados, Evolution API, Fotos) | ✅ Concluída | 111 testes verdes, máquina de estados completa (`/anunciar`, `/buscar`, `/meusanuncios`, `/renovar`, `/pausar`, `/reativar`, `/cancelar`), Webhook Evolution API com validação de segredo, armazenamento local e Supabase Storage, doc Typebot |
| **Fase 3** | Segurança & Área Logada (Spring Security, JWT, Admin e Loja) | ⏳ Próxima | Autenticação JWT, login, endpoints de loja e admin |
| **Fase 4** | Front-end React (Vite, TS, Tailwind, Vitrine, Telas de Gestão, Vitest) | ⚪ Não iniciada | Interface SPA mobile-first e testes Vitest |
| **Fase 5** | Importador de WhatsApp & Métricas Admin (Parser de chat .txt, Heurísticas, Revisão) | ⚪ Não iniciada | Processamento de exportação do grupo .txt |
| **Fase 6** | Entrega & Infraestrutura (Docker Compose, CI, JaCoCo, Documentação Final) | ⚪ Não iniciada | Docker Compose, GitHub Actions, documentação completa |

---

## O que foi entregue na Fase 2
1. **Máquina de Estados Conversacional (`ConversaService` & `ChatbotService`):**
   - Fluxo guiado completo para `/anunciar` etapa por etapa: Título -> Categoria numerada -> Preço com parse flexível ("12,50", "R$ 12,50", "12.50") -> Bairro -> Foto (com opção "pular") -> Resumo e confirmação ("sim"/"não").
   - Suporte a novos anunciantes pelo WhatsApp: fluxo prévio de cadastro simplificado (Nome -> Bairro) antes do primeiro anúncio.
   - Verificação preventiva de cota gratuita (máx. 2 anúncios ativos).
   - Comandos conversacionais: `/meusanuncios`, `/renovar <id>`, `/pausar <id>`, `/reativar <id>`, `/cancelar` (em qualquer etapa) e `/ajuda`.
   - Expiração do estado de conversa por inatividade após 30 minutos com fallback amigável.
   - Normalização de remetente e armazenamento de hash SHA-256 no banco para conformidade LGPD.

2. **Webhook Oficial da Evolution API (`WebhookWhatsAppController`):**
   - Endpoint `POST /api/webhook/whatsapp` com proteção por token/segredo configurável (`apikey` ou `x-webhook-secret`).
   - Filtragem e descarte de eventos desnecessários: ignora mensagens enviadas pela própria instância (`fromMe`), mensagens de grupos (`@g.us`) e eventos sem texto.
   - Respostas assíncronas 200 imediatas e envio das respostas do chatbot diretamente via `WhatsAppGateway`.

3. **Armazenamento de Fotos (`ArmazenamentoFotos`):**
   - Interface com implementações desacopladas:
     - `ArmazenamentoLocalFotos` (dev/testes em disco local, mapeado em `/uploads/anuncios/**`).
     - `ArmazenamentoSupabaseFotos` (produção via API REST do Supabase Storage).
   - Validações estritas de upload: tipos MIME permitidos (`jpg`, `jpeg`, `png`, `webp`) e limite máximo de 5 MB.
   - Controller de upload `POST /api/anuncios/{id}/foto` para envio de imagem multipart.

4. **Documentação Técnica do Fluxo Visual:**
   - Criação de `docs/typebot-fluxo.md` descrevendo blocos, variáveis e chamadas REST equivalentes para montagem no Typebot.

5. **Testes e Qualidade da Fase 2:**
   - Total de **111 testes automatizados** passando com `BUILD SUCCESS` (0 falhas, 0 erros, 0 ignorados).
   - Testes unitários para `ConversaService`, fluxo completo de `/anunciar`, `ArmazenamentoLocalFotos`, `ArmazenamentoSupabaseFotos`, `AnuncioFotoController` e `WebhookWhatsAppController`.

---

## Como Rodar os Testes
```powershell
cd backend
.\mvn.cmd test
```
