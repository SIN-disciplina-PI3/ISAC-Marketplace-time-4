export type PapelUsuario = 'ADMIN' | 'LOJA';

export type StatusAssinatura =
  | 'ATIVA'
  | 'TRIAL'
  | 'INADIMPLENTE'
  | 'CANCELADA'
  | 'PENDENTE_PAGAMENTO'
  | 'INATIVA';

export type PlanoLoja = 'BASICO' | 'PROFISSIONAL' | 'PREMIUM' | 'GRATUITO' | 'ASSINANTE';

export type StatusAnuncio =
  | 'PUBLICADO'
  | 'ATIVO'
  | 'PAUSADO'
  | 'EXPIRADO'
  | 'PENDENTE_MODERACAO'
  | 'REJEITADO';

export type TipoAnuncio = 'PRODUTO' | 'SERVICO';

export interface UsuarioAutenticado {
  token: string;
  email: string;
  papel: PapelUsuario;
  lojaId?: number | null;
  nome?: string;
}

export interface Categoria {
  id: number;
  nome: string;
  icone?: string;
  ativa: boolean;
}

export interface Loja {
  id: number;
  /** Nome da loja (alias: nomeFantasia) */
  nomeLoja: string;
  nomeFantasia?: string;
  cnpj: string;
  documento?: string;
  tipoDocumento?: 'CPF' | 'CNPJ';
  categoria?: { id: number; nome: string } | null;
  telefoneWhatsapp: string;
  descricao?: string;
  logoUrl?: string;
  email?: string;
  bairro: string;
  endereco?: string;
  planoAssinatura?: PlanoLoja;
  plano?: PlanoLoja;
  statusAssinatura: StatusAssinatura;
  dataVencimentoAssinatura?: string;
  dataCadastro?: string;
}

export interface Anuncio {
  id: number;
  lojaId?: number;
  nomeLoja: string;
  telefoneLoja?: string;
  telefoneWhatsapp?: string;
  titulo: string;
  descricao?: string;
  /** Preço em reais; null = a consultar */
  preco: number | null;
  categoriaId?: number;
  nomeCategoria?: string;
  /** Categoria como string (alias de nomeCategoria) */
  categoria?: string;
  bairro: string;
  tipo?: TipoAnuncio;
  fotoUrl?: string;
  fotos?: string[];
  status: StatusAnuncio;
  disponivel?: boolean;
  destaque: boolean;
  dataPublicacao?: string;
  dataExpiracao?: string;
  linkWaMe?: string;
}

export interface PaginaAnuncios {
  content: Anuncio[];
  totalPages: number;
  totalElements: number;
  number: number;
}

export interface Evento {
  id: number;
  titulo: string;
  descricao?: string;
  local: string;
  /** ISO string da data/hora do evento */
  dataHora: string;
  dataInicio?: string;
  dataFim?: string;
  fotoUrl?: string;
  linkInscricao?: string;
  publicado?: boolean;
}

export interface Conteudo {
  id: number;
  chave: string;
  titulo: string;
  corpoMarkdown: string;
  atualizadoEm?: string;
}

export interface AssinaturaInfo {
  plano: PlanoLoja;
  statusAssinatura: StatusAssinatura;
  dataVencimento?: string;
  cotaUsada: number;
  cotaLimite: number;
  ilimitado: boolean;
}

export interface TermoMetrica {
  termo: string;
  total: number;
}

export interface MetricasAdmin {
  totalAnunciosAtivos?: number;
  totalAnunciosPublicados?: number;
  totalLojasAtivas?: number;
  totalLojasPendentes?: number;
  totalLojasInativas?: number;
  totalAssinaturasAtivas?: number;
  anunciosPendentesModeracao?: number;
  totalBuscas?: number;
  buscasUltimas24h?: number;
  cliquesWhatsappUltimas24h?: number;
  termosMaisBuscados?: TermoMetrica[];
  buscasSemResultado?: TermoMetrica[];
}
