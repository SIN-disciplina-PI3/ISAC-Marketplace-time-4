# ISAC Marketplace

> Marketplace Comunitário e Conversacional do **ISAC** (Instituto Social e Ambiental do Cabo de Santo Agostinho - PE).
> Desenvolvido com foco na comunidade de **Enseada dos Corais**, Gaibu e região.

---

## 📌 Visão Geral da Arquitetura
- **Back-end:** Java 17 + Spring Boot 3.2.5 + Spring Data JPA + Spring Security + Flyway
- **Banco de dados:**
  - Desenvolvimento e testes: **H2 em memória** (`MODE=PostgreSQL`)
  - Produção: **PostgreSQL** (Supabase)
- **Integração de Mensageria:** WhatsApp Gateway com abstração para **Evolution API (v2)** e **LogWhatsAppGateway** (dev/testes)
- **Testes:** JUnit 5, Mockito (strict stubs), MockMvc e Spring Boot Test com relatório de cobertura **JaCoCo**

---

## 🚀 Como Executar o Back-end

### Pré-requisitos
- JDK 17 LTS instalado (`java -version`)
- Maven 3.9+ ou utilize o wrapper `.\mvn.cmd` incluído no repositório

### Compilação e Execução dos Testes Automatizados
```powershell
cd backend
.\mvn.cmd test
```
*Gera o relatório de cobertura JaCoCo em `backend/target/site/jacoco/index.html`.*

### Iniciar a Aplicação Localmente
```powershell
cd backend
.\mvn.cmd spring-boot:run
```
A aplicação iniciará na porta `8080` com o perfil `dev` ativado e carga automática dos dados de demonstração (`dados-demo.sql`).

---

## 🧪 Testando o Simulador do Chatbot via REST

Com o back-end em execução, você pode enviar mensagens para o simulador via `POST /api/chatbot/mensagens`:

```powershell
curl -X POST http://localhost:8080/api/chatbot/mensagens `
  -H "Content-Type: application/json" `
  -d '{"telefone": "81999990001", "texto": "tapioca"}'
```

**Exemplo de Resposta:**
```json
{
  "resposta": "🛍️ *Encontrei os seguintes resultados para \"tapioca\":*\n\n🔹 *Tapioca Tradicional com Queijo Coalho e Coco*\n   💰 R$ 12,00\n   🏪 Tapiocaria da Enseada\n   📍 Enseada dos Corais\n   📲 Falar com vendedor: https://wa.me/5581999990001?text=Ol%C3%A1%21+Vi+seu+an%C3%BAncio+*Tapioca+Tradicional+com+Queijo+Coalho+e+Coco*+no+ISAC+Marketplace.\n\n💡 _Entre em contato diretamente com o vendedor clicando no link do WhatsApp!_",
  "links": [
    "https://wa.me/5581999990001?text=Ol%C3%A1%21+Vi+seu+an%C3%BAncio+*Tapioca+Tradicional+com+Queijo+Coalho+e+Coco*+no+ISAC+Marketplace."
  ]
}
```

---

## 📑 Principais Endpoints Disponíveis (Fase 1)
- `GET /api/busca?termo=&categoria=&bairro=&pagina=` — Busca vitrine com filtros
- `GET /api/anuncios/{id}` — Detalhes do anúncio público
- `GET /api/categorias` — Listagem de categorias ativas
- `GET /api/eventos` — Listagem de eventos comunitários
- `GET /api/conteudos/{chave}` — Textos institucionais do ISAC (ex: `/api/conteudos/sobre`)
- `POST /api/lojas` — Cadastro de loja/anunciante
- `POST /api/chatbot/mensagens` — Simulador interativo do chatbot
