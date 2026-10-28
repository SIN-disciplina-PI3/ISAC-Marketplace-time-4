-- ==========================================================
-- Dados de demonstração do ISAC Marketplace (perfil dev)
-- Todos os dados são fictícios e compatíveis com a LGPD
-- ==========================================================

-- Categorias
INSERT INTO categorias (id, nome, ativa) VALUES
(1, 'Alimentação', true),
(2, 'Artesanato', true),
(3, 'Agroecologia', true),
(4, 'Serviços', true),
(5, 'Moda e Vestuário', true);

-- Lojas Fictícias
-- Loja 1: Ativa, Assinante com vencimento em +15 dias (CNPJ 11.222.333/0001-81 válido)
INSERT INTO lojas (id, nome_fantasia, documento, tipo_documento, categoria, telefone_whatsapp, email, bairro, endereco, plano, status_assinatura, data_vencimento_assinatura, data_cadastro) VALUES
(1, 'Tapiocaria da Enseada', '11222333000181', 'CNPJ', 'Alimentação', '81999990001', 'tapiocaria@isac-demo.local', 'Enseada dos Corais', 'Av. Beira Mar, 120', 'ASSINANTE', 'ATIVA', DATEADD('DAY', 15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

-- Loja 2: Ativa, Plano Gratuito (CPF 529.982.247-25 válido)
INSERT INTO lojas (id, nome_fantasia, documento, tipo_documento, categoria, telefone_whatsapp, email, bairro, endereco, plano, status_assinatura, data_vencimento_assinatura, data_cadastro) VALUES
(2, 'Artesanato Corais', '52998224725', 'CPF', 'Artesanato', '81999990002', 'artesanato@isac-demo.local', 'Enseada dos Corais', 'Rua das Conchas, 45', 'GRATUITO', 'INATIVA', null, CURRENT_TIMESTAMP);

-- Loja 3: Assinante com pagamento Pendente (CNPJ 11.222.333/0005-05 válido)
INSERT INTO lojas (id, nome_fantasia, documento, tipo_documento, categoria, telefone_whatsapp, email, bairro, endereco, plano, status_assinatura, data_vencimento_assinatura, data_cadastro) VALUES
(3, 'Horta Agroecológica Gaibu', '11222333000505', 'CNPJ', 'Agroecologia', '81999990003', 'horta@isac-demo.local', 'Gaibu', 'Sítio Verde, Km 3', 'ASSINANTE', 'PENDENTE', null, CURRENT_TIMESTAMP);

-- Loja 4: Assinante Inativa (Vencida há 5 dias) (CNPJ 11.222.333/0018-20 válido)
INSERT INTO lojas (id, nome_fantasia, documento, tipo_documento, categoria, telefone_whatsapp, email, bairro, endereco, plano, status_assinatura, data_vencimento_assinatura, data_cadastro) VALUES
(4, 'Reformas e Elétrica Litoral', '11222333001820', 'CNPJ', 'Serviços', '81999990004', 'eletrica@isac-demo.local', 'Paiva', 'Rua Principal, 88', 'ASSINANTE', 'INATIVA', DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

-- Loja 5: Ativa, Gratuita
INSERT INTO lojas (id, nome_fantasia, documento, tipo_documento, categoria, telefone_whatsapp, email, bairro, endereco, plano, status_assinatura, data_vencimento_assinatura, data_cadastro) VALUES
(5, 'Peixaria Enseada Viva', null, null, 'Alimentação', '81999990005', 'peixaria@isac-demo.local', 'Enseada dos Corais', 'Vila dos Pescadores, Box 2', 'GRATUITO', 'INATIVA', null, CURRENT_TIMESTAMP);

-- Anúncios Fictícios (~15 anúncios)
INSERT INTO anuncios (id, loja_id, categoria_id, titulo, descricao, preco, bairro, tipo, foto_url, status, disponivel, destaque, data_publicacao, data_expiracao, aviso_expiracao_enviado) VALUES
(1, 1, 1, 'Tapioca Tradicional com Queijo Coalho e Coco', 'Feita na hora com goma fresca e queijo coalho assado.', 12.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1598514983318-2f64f8f4796c?w=500', 'ATIVO', true, true, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(2, 1, 1, 'Bolo de Rolo Pernambucano Artesanal (500g)', 'Massa fina tradicional recheada com goiabada cascão cremosa.', 25.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500', 'ATIVO', true, true, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(3, 1, 1, 'Cuscuz Nordestino com Carne de Sol na Manteiga de Garrafa', 'Porção individual bem servida com tempero caseiro.', 18.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500', 'ATIVO', true, true, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(4, 1, 1, 'Tapioca Especial de Frango com Queijo e Catupiry', 'Recheio farto de peito de frango desfiado temperado.', 15.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500', 'ATIVO', true, true, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),

(5, 2, 2, 'Luminária Rústica em Conchas e Fibras Naturais', 'Peça única decorativa feita à mão com materiais coletados na praia.', 45.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500', 'ATIVO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(6, 2, 2, 'Bolsa de Palha Trançada à Mão', 'Bolsa artesanal estilosa, acabamento resistente e sustentável.', 60.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=500', 'ATIVO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),

(7, 3, 3, 'Cesta de Verduras Orgânicas da Semana', 'Alface, rúcula, coentro, couve e cheiro verde colhidos no dia.', 35.00, 'Gaibu', 'PRODUTO', 'https://images.unsplash.com/photo-1610348725531-843dff563e2c?w=500', 'PAUSADO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(8, 3, 3, 'Mel Puro de Abelhas Nativas (500ml)', 'Produção agroecológica sem agrotóxicos ou aditivos.', 28.00, 'Gaibu', 'PRODUTO', 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=500', 'PAUSADO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),

(9, 4, 4, 'Serviço de Manutenção Elétrica e Instalações', 'Consertos em geral, troca de fiação, disjuntores e chuveiros.', 120.00, 'Paiva', 'SERVICO', null, 'PAUSADO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', -2, CURRENT_TIMESTAMP), true),
(10, 4, 4, 'Pintura e Reparo de Paredes', 'Orçamento sem compromisso para casas e apartamentos no litoral.', 250.00, 'Paiva', 'SERVICO', null, 'PAUSADO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', -2, CURRENT_TIMESTAMP), true),

(11, 5, 1, 'Peixe Vermelho Fresco do Dia (Kg)', 'Pescado fresco direto dos pescadores de Enseada dos Corais.', 38.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1534482421-64566f976cfa?w=500', 'ATIVO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(12, 5, 1, 'Camarão Médio Fresco Limpo (Kg)', 'Tratado, pronto para preparo.', 55.00, 'Enseada dos Corais', 'PRODUTO', 'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=500', 'PAUSADO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(13, 2, 2, 'Kit de Porta Copos Artesanais', 'Kit com 6 unidades em macramê artesanal.', 25.00, 'Enseada dos Corais', 'PRODUTO', null, 'PAUSADO', true, false, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(14, 1, 1, 'Suco Natural de Graviola (500ml)', 'Fruta fresca colhida no pomar.', 8.00, 'Enseada dos Corais', 'PRODUTO', null, 'ATIVO', true, true, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false),
(15, 1, 1, 'Açai na Tigela com Banana e Granola', 'Tigela de 400ml bem servida.', 16.00, 'Enseada dos Corais', 'PRODUTO', null, 'ATIVO', true, true, CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), false);

-- Eventos do ISAC
INSERT INTO eventos (id, titulo, descricao, local, data_inicio, data_fim, publicado) VALUES
(1, 'Feira Agroecológica e Cultural de Enseada dos Corais', 'Encontro mensal reunindo produtores familiares, artesãos locais e atrações culturais do Cabo de Santo Agostinho.', 'Praça Central de Enseada dos Corais', DATEADD('DAY', 5, CURRENT_TIMESTAMP), DATEADD('DAY', 5, CURRENT_TIMESTAMP), true),
(2, 'Mutirão Comunitário de Limpeza e Preservação das Praias', 'Ação coletiva do ISAC com voluntários e comunidade para conscientização ambiental e preservação do litoral.', 'Orla da Enseada dos Corais e Gaibu', DATEADD('DAY', 12, CURRENT_TIMESTAMP), DATEADD('DAY', 12, CURRENT_TIMESTAMP), true);

-- Conteúdos Institucionais
INSERT INTO conteudos (id, chave, titulo, corpo_markdown, atualizado_em) VALUES
(1, 'sobre', 'Sobre o ISAC', 'O **ISAC** é uma instituição social e ambiental atuante no **Cabo de Santo Agostinho (PE)**, com ações voltadas à comunidade de **Enseada dos Corais**, Gaibu e região.\n\nNossa missão é fortalecer a economia local solidária, preservar a rica sociobiodiversidade costeira e proporcionar desenvolvimento sustentável para as famílias da nossa comunidade.\n\n[CONTEÚDO A DEFINIR PELO ISAC]', CURRENT_TIMESTAMP),
(2, 'agricultura', 'Agricultura Familiar e Meio Ambiente', 'O ISAC desenvolve projetos e oficinas práticas voltadas à agroecologia, incentivo ao plantio comunitário, conservação dos recursos hídricos e manejo responsável dos solos.\n\n[CONTEÚDO A DEFINIR PELO ISAC]', CURRENT_TIMESTAMP),
(3, 'como-anunciar', 'Como Anunciar no ISAC Marketplace', 'Divulgue seus produtos e serviços na nossa vitrine comunitária!\n\n- **Plano Gratuito:** Cadastre até 2 anúncios ativos sem nenhum custo.\n- **Plano Assinante:** Anúncios ilimitados, destaque nas buscas e suporte prioritário por uma contribuição quinzenal que apoia os projetos socioambientais do ISAC.\n- **Pelo WhatsApp:** Envie /anunciar no nosso WhatsApp e publique seu item em menos de 2 minutos!', CURRENT_TIMESTAMP);

-- Ajusta os contadores de identidade para evitar conflito de chave primária em novos cadastros
ALTER TABLE categorias ALTER COLUMN id RESTART WITH 100;
ALTER TABLE lojas ALTER COLUMN id RESTART WITH 100;
ALTER TABLE anuncios ALTER COLUMN id RESTART WITH 100;
ALTER TABLE eventos ALTER COLUMN id RESTART WITH 100;
ALTER TABLE conteudos ALTER COLUMN id RESTART WITH 100;
