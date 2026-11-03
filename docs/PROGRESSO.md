# Progresso do Projeto ISAC Marketplace

Este documento registra o estado atual do desenvolvimento, o que foi concluído, o que está pendente e como executar o sistema em cada fase.

## Status Geral: Fase 3 Concluída com Sucesso ✅

| Fase | Descrição | Status | Detalhes |
|---|---|---|---|
| **Fase 1** | Núcleo do Back-end (Spring Boot, Domínio, Flyway, H2, Serviços, Chatbot básico, Testes) | ✅ Concluída | 88 testes unitários/integração verdes, build success, JaCoCo gerado, dados demo e simulador REST ativos |
| **Fase 2** | WhatsApp Conversacional & Armazenamento (Máquina de estados, Evolution API, Fotos) | ✅ Concluída | 111 testes verdes, máquina de estados completa (`/anunciar`, `/buscar`, `/meusanuncios`, `/renovar`, `/pausar`, `/reativar`, `/cancelar`), Webhook Evolution API com validação de segredo, armazenamento local e Supabase Storage, doc Typebot |
| **Fase 3** | Segurança & Área Logada (Spring Security, JWT, Admin e Loja) | ✅ Concluída | 144 testes verdes (0 falhas, 0 erros, 0 ignorados). Spring Security + JWT, Login, Minha Loja, Ações de Anúncio, Painel Admin completo (moderação, lojas, assinaturas, métricas, CRUD de categorias/eventos/conteúdos), testes 401/403 |
| **Fase 4** | Front-end React (Vite, TS, Tailwind, Vitrine, Telas de Gestão, Vitest) | ⏳ Próxima | Interface SPA mobile-first e testes Vitest |
| **Fase 5** | Importador de WhatsApp & Métricas Admin (Parser de chat .txt, Heurísticas, Revisão) | ⚪ Não iniciada | Processamento de exportação do grupo .txt |
| **Fase 6** | Entrega & Infraestrutura (Docker Compose, CI, JaCoCo, Documentação Final) | ⚪ Não iniciada | Docker Compose, GitHub Actions, documentação completa |

---

## O que foi entregue na Fase 3

1. **Camada de Autenticação & Segurança (Spring Security + JWT):**
   - `JwtService`: geração e validação de tokens JWT com assinatura HMAC-SHA256, extração de claims (`papel`, `email`, `usuarioId`, `lojaId`).
   - `UsuarioDetailsService`: autenticação de usuários via banco de dados (`usuarios`), concedendo `ROLE_ADMIN` ou `ROLE_LOJA`.
   - `JwtAuthenticationFilter`: interceptador stateless que valida o cabeçalho `Authorization: Bearer <token>` e injeta o `SecurityContext`.
   - `SecurityConfig`: configuração completa de rotas públicas, rotas autenticadas da loja (`ROLE_LOJA` ou `ROLE_ADMIN`) e rotas restritas de administração (`ROLE_ADMIN`), desabilitação de frame-options para H2 e CORS liberado para o front-end.
   - `AuthController`: endpoint `POST /api/auth/login` validando credenciais com BCrypt e emitindo token JWT com metadados do usuário.
   - Carga inicial automática no `DataLoader` dos usuários de demonstração:
     - Administrador: `admin@isac.org.br` / `admin123` (`ROLE_ADMIN`)
     - Loja Tapiocaria: `tapiocaria@isac-demo.local` / `loja123` (`ROLE_LOJA`)

2. **Área da Loja Autenticada (`MinhaLojaController` & `AnuncioLojaController`):**
   - `GET /api/minha-loja`: consulta dados cadastrais da loja associada ao usuário autenticado.
   - `PUT /api/minha-loja`: atualização de informações cadastrais da loja.
   - `GET /api/minha-loja/anuncios`: listagem dos anúncios pertencentes à loja.
   - `POST /api/minha-loja/anuncios`: criação de novos anúncios vinculados automaticamente à loja, respeitando a cota do plano gratuito.
   - `GET /api/minha-loja/assinatura`: resumo de status do plano (`GRATUITO` ou `ASSINANTE`), vigência e contagem de cota ativa (X de 2).
   - `PUT /api/anuncios/{id}`: edição de anúncios com validação estrita de titularidade.
   - `PATCH /api/anuncios/{id}/disponibilidade`: alternância de disponibilidade imediata.
   - `POST /api/anuncios/{id}/renovar`: renovação de prazo de expiração (+15 dias).
   - `POST /api/anuncios/{id}/pausar` e `POST /api/anuncios/{id}/reativar`.

3. **Painel Administrativo Completo (`AdminController`):**
   - `GET /api/admin/lojas?status=`: listagem e filtragem de lojas por status de assinatura.
   - `GET /api/admin/lojas/{id}`: detalhamento de loja cadastrada.
   - `POST /api/admin/lojas/{id}/assinatura/pagamento`: confirmação manual de pagamento de assinatura com extensão do ciclo (15 dias) e ativação de destaque.
   - `POST /api/admin/lojas/{id}/assinatura/cancelamento`: cancelamento de assinatura e readequação de cota.
   - `POST /api/admin/assinaturas/vencidas/inativacao`: disparo manual da rotina de inativação de assinaturas expiradas.
   - `GET /api/admin/anuncios?status=`: moderação de anúncios por status (`PENDENTE_MODERACAO`, `ATIVO`, `REJEITADO`).
   - `POST /api/admin/anuncios/{id}/aprovar`: aprovação de anúncio para publicação imediata.
   - `POST /api/admin/anuncios/{id}/rejeitar`: rejeição fundamentada com registro de motivo.
   - CRUD de Categorias (`/api/admin/categorias`).
   - CRUD de Eventos (`/api/admin/eventos`).
   - Gestão de Conteúdos Institucionais (`/api/admin/conteudos`).
   - `GET /api/admin/metricas`: métricas consolidadas de anúncios ativos, lojas por status, total de buscas, termos mais buscados e buscas sem resultado.

4. **Testes e Qualidade da Fase 3:**
   - Total de **144 testes automatizados** passando com `BUILD SUCCESS` (0 falhas, 0 erros, 0 ignorados).
   - Testes unitários e de integração adicionados: `JwtServiceTest`, `AuthControllerTest`, `MinhaLojaControllerTest`, `AnuncioLojaControllerTest`, `AdminControllerTest` e `SegurancaAutorizacaoTest` (verificando 401 Unauthorized e 403 Forbidden).
   - Relatório JaCoCo atualizado gerado em `target/site/jacoco/index.html`.

---

## Como Rodar os Testes
```powershell
cd backend
.\mvn.cmd test
```
