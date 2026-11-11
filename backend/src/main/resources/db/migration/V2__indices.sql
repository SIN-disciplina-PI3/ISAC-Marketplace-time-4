-- Ãndices para otimizacao de busca e integridade referencial
CREATE INDEX IF NOT EXISTS idx_anuncios_status_disp ON anuncios(status, disponivel);
CREATE INDEX IF NOT EXISTS idx_anuncios_loja ON anuncios(loja_id);
CREATE INDEX IF NOT EXISTS idx_anuncios_categoria ON anuncios(categoria_id);
CREATE INDEX IF NOT EXISTS idx_anuncios_expiracao ON anuncios(data_expiracao);
CREATE INDEX IF NOT EXISTS idx_lojas_telefone ON lojas(telefone_whatsapp);
CREATE INDEX IF NOT EXISTS idx_lojas_documento ON lojas(documento);
CREATE INDEX IF NOT EXISTS idx_buscas_criado_em ON buscas(criado_em);
