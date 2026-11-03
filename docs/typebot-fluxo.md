# Fluxo Equivalente no Typebot (Documentação de Arquitetura)

Este documento descreve como o fluxo conversacional do **ISAC Marketplace** pode ser montado visualmente utilizando o **Typebot**, integrando-se via requisições HTTP REST ao back-end Spring Boot (`POST /api/chatbot/mensagens`).

---

## 1. Visão Geral da Arquitetura
No modelo híbrido/low-code:
- **Evolution API (v2):** Conecta o número de WhatsApp institucional do ISAC e repassa os eventos via webhook para o Typebot.
- **Typebot:** Gerencia a interface visual, coleta de entradas do usuário e exibição de mensagens.
- **Back-end Spring Boot:** Processa as regras de negócio, buscas no banco relacional, cálculo de cota gratuita e persistência de dados.

---

## 2. Variáveis Globais do Typebot

| Variável | Tipo | Descrição |
|---|---|---|
| `telefone` | String | Número do remetente (extraído de `{{event.from}}` ou `remoteJid`) |
| `textoEntrada` | String | Texto digitado pelo usuário no WhatsApp |
| `respostaBot` | String | Resposta gerada pela API do marketplace |
| `linksRetorno` | List / String | Links `wa.me` dos vendedores encontrados |

---

## 3. Estrutura de Blocos do Fluxo no Typebot

### Bloco 1: Início e Saudação
- **Gatilho (Start):** Mensagem recebida via WhatsApp.
- **Ação:** Coleta o identificador `telefone` do remetente.
- **Bloco de Chamada Webhook (HTTP Request):**
  - **Método:** `POST`
  - **URL:** `https://api.isac-marketplace.org.br/api/chatbot/mensagens`
  - **Headers:** `Content-Type: application/json`
  - **Body (JSON):**
    ```json
    {
      "telefone": "{{telefone}}",
      "texto": "{{textoEntrada}}"
    }
    ```
  - **Mapeamento de Resposta:** Salva `resposta` na variável `{{respostaBot}}`.

### Bloco 2: Exibição da Resposta
- **Bloco de Texto:** Exibe `{{respostaBot}}`.
- Se o usuário estiver no meio de um fluxo guiado (como `/anunciar`), o back-end mantém a máquina de estados através da tabela `conversas_whatsapp` vinculada ao hash do telefone.
- O Typebot simplesmente repassa cada resposta digitada para a API `POST /api/chatbot/mensagens`, mantendo a inteligência de negócios centralizada e versionada no back-end Java.

### Bloco 3: Opções Rápidas (Fallback em Texto)
- Para clientes em versões do WhatsApp sem suporte a botões interativos, o bot sempre provê as opções em texto numerado:
  - `1) Buscar`
  - `2) Anunciar`
  - `3) Meus anúncios`
  - `4) Ajuda`

---

## 4. Vantagens da Abordagem Adotada
- **Testabilidade:** Como o motor de diálogo (`ChatbotService`) reside no back-end Java, ele possui 100% de testes automatizados determinísticos com JUnit 5.
- **Portabilidade:** A qualquer momento a instituição pode alternar entre o webhook direto da Evolution API e o Typebot sem alterar uma única linha de regra de negócio.
