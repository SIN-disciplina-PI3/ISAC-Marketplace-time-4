# Progresso do Projeto ISAC Marketplace

Este documento registra o estado atual do desenvolvimento, o que foi concluído, o que está pendente e como executar o sistema em cada fase.

## Status Geral: Fase 4 Concluída com Sucesso ✅

| Fase | Descrição | Status | Detalhes |
|---|---|---|---|
| **Fase 1** | Núcleo do Back-end (Spring Boot, Domínio, Flyway, H2, Serviços, Chatbot básico, Testes) | ✅ Concluída | 88 testes unitários/integração verdes, build success, JaCoCo gerado, dados demo e simulador REST ativos |
| **Fase 2** | WhatsApp Conversacional & Armazenamento (Máquina de estados, Evolution API, Fotos) | ✅ Concluída | 111 testes verdes, máquina de estados completa (`/anunciar`, `/buscar`, `/meusanuncios`, `/renovar`, `/pausar`, `/reativar`, `/cancelar`), Webhook Evolution API com validação de segredo, armazenamento local e Supabase Storage, doc Typebot |
| **Fase 3** | Segurança & Área Logada (Spring Security, JWT, Admin e Loja) | ✅ Concluída | 144 testes verdes (0 falhas, 0 erros, 0 ignorados). Spring Security + JWT, Login, Minha Loja, Ações de Anúncio, Painel Admin completo (moderação, lojas, assinaturas, métricas, CRUD de categorias/eventos/conteúdos), testes 401/403 |
| **Fase 4** | Front-end React (Vite, TS, Tailwind, Vitrine, Telas de Gestão, Vitest) | ✅ Concluída | npm run build ✅ (1524 módulos), npm test ✅ (21 testes, 4 arquivos), SPA mobile-first completa |
| **Fase 5** | Importador de WhatsApp & Métricas Admin (Parser de chat .txt, Heurísticas, Revisão) | ⏳ Próxima | Processamento de exportação do grupo .txt |
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

---

## O que foi entregue na Fase 4

### Front-end: SPA React mobile-first completa

**Stack:** React 18, Vite 5, TypeScript strict, Tailwind CSS (paleta ISAC customizada), React Router v6, Vitest + Testing Library.

**Páginas públicas (sem autenticação):**
- `/` — `Home.tsx`: hero com buscador, grid de destaques, seção WhatsApp bot, lista de eventos
- `/vitrine` — `Vitrine.tsx`: busca por termo/categoria/bairro com paginação via Spring Page
- `/anuncios/:id` — `AnuncioDetalhes.tsx`: página de detalhes com galeria, preço, link WhatsApp
- `/eventos` — `Eventos.tsx`: lista de próximos eventos da comunidade
- `/institucional/:chave` — `Institucional.tsx`: conteúdo markdown por chave (sobre, missao, etc.)
- `/como-anunciar` — `ComoAnunciar.tsx`: canais WhatsApp + site, tabela de planos
- `/login` — `Login.tsx`: formulário com feedback de erro
- `/cadastro-loja` — `CadastroLoja.tsx`: formulário de cadastro com CNPJ

**Área da loja (autenticação LOJA ou ADMIN):**
- `/loja` — `DashboardLoja.tsx`: resumo de assinatura e atalhos
- `/loja/anuncios` — `MeusAnuncios.tsx`: CRUD completo com modal e upload de foto
- `/loja/dados` — `DadosLoja.tsx`: formulário de edição dos dados cadastrais

**Painel administrativo (autenticação ADMIN):**
- `/admin` — `DashboardAdmin.tsx`: métricas e navegação rápida
- `/admin/lojas` — `LojasAdmin.tsx`: tabela com confirmar pagamento / cancelar assinatura
- `/admin/anuncios` — `AnunciosAdmin.tsx`: moderação com modal de rejeição (motivo obrigatório)
- `/admin/categorias` — `CategoriasAdmin.tsx`: adicionar e ativar/inativar categorias
- `/admin/eventos` — `EventosAdmin.tsx`: CRUD de eventos
- `/admin/conteudos` — `ConteudosAdmin.tsx`: edição inline de conteúdos institucionais (Markdown)

**Componentes reutilizáveis:**
- `Navbar.tsx`, `Footer.tsx`, `CardAnuncio.tsx`, `LoadingSpinner.tsx`, `ProtectedRoute.tsx`

**Autenticação:**
- `AuthContext.tsx` com `AuthProvider` e hook `useAuth` — persiste token/papel/lojaId em `localStorage`
- `ProtectedRoute` aceita `papeis[]` ou `papelRequerido` para controle de acesso por papel

**Testes Vitest:**

| Arquivo | Testes | Resultado |
|---|---|---|
| `CardAnuncio.test.tsx` | 9 | ✅ |
| `Vitrine.test.tsx` | 4 | ✅ |
| `Login.test.tsx` | 5 | ✅ |
| `ProtectedRoute.test.tsx` | 3 | ✅ |
| **Total** | **21** | **✅ PASSANDO** |

**Build Vite:**
```
✓ 1524 modules transformed
dist/assets/index.js  279 kB (gzip: 76 kB)
dist/assets/index.css  34 kB (gzip: 6 kB)
✓ built in 10.32s
```

---

## Como Rodar o Front-end (desenvolvimento)

```powershell
cd frontend
npm run dev
# Acesse http://localhost:5173
# (o proxy /api encaminha para http://localhost:8080)
```

## Como Rodar os Testes
```powershell
cd backend
.\mvn.cmd test

cd ..\frontend
npm test
```
