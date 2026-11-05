import {
  Anuncio,
  AssinaturaInfo,
  Categoria,
  Conteudo,
  Evento,
  Loja,
  MetricasAdmin,
  PaginaAnuncios,
  UsuarioAutenticado,
} from '../types';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

function getAuthHeader(): Record<string, string> {
  const token = localStorage.getItem('isac_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const headers = {
    'Content-Type': 'application/json',
    ...getAuthHeader(),
    ...options.headers,
  };

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorMsg = `Erro na requisição (${response.status})`;
    try {
      const errorJson = await response.json();
      if (errorJson.mensagem) {
        errorMsg = errorJson.mensagem;
      }
    } catch {
      // fallback em texto puro
    }
    throw new Error(errorMsg);
  }

  if (response.status === 204) {
    return {} as T;
  }

  return response.json();
}

export const api = {
  // Públicos
  async buscar(params: {
    termo?: string;
    categoria?: string;
    bairro?: string;
    pagina?: number;
    tamanho?: number;
  } = {}): Promise<PaginaAnuncios> {
    const q = new URLSearchParams();
    if (params.termo) q.append('termo', params.termo);
    if (params.categoria) q.append('categoria', params.categoria);
    if (params.bairro) q.append('bairro', params.bairro);
    if (params.pagina != null) q.append('pagina', String(params.pagina));
    if (params.tamanho != null) q.append('tamanho', String(params.tamanho));
    return request<PaginaAnuncios>(`/api/busca?${q.toString()}`);
  },

  async obterAnuncio(id: number): Promise<Anuncio> {
    return request<Anuncio>(`/api/anuncios/${id}`);
  },

  async listarCategorias(): Promise<Categoria[]> {
    return request<Categoria[]>('/api/categorias');
  },

  async listarEventos(): Promise<Evento[]> {
    return request<Evento[]>('/api/eventos');
  },

  async obterConteudo(chave: string): Promise<Conteudo> {
    return request<Conteudo>(`/api/conteudos/${chave}`);
  },

  async login(email: string, senha: string): Promise<UsuarioAutenticado> {
    return request<UsuarioAutenticado>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
    });
  },

  async cadastrarLoja(dados: any): Promise<Loja> {
    return request<Loja>('/api/lojas', {
      method: 'POST',
      body: JSON.stringify(dados),
    });
  },

  // Área da Loja
  async obterMinhaLoja(): Promise<Loja> {
    return request<Loja>('/api/minha-loja');
  },

  async atualizarMinhaLoja(dados: any): Promise<Loja> {
    return request<Loja>('/api/minha-loja', {
      method: 'PUT',
      body: JSON.stringify(dados),
    });
  },

  async listarMeusAnuncios(): Promise<Anuncio[]> {
    return request<Anuncio[]>('/api/minha-loja/anuncios');
  },

  async criarAnuncio(dados: any): Promise<Anuncio> {
    return request<Anuncio>('/api/minha-loja/anuncios', {
      method: 'POST',
      body: JSON.stringify(dados),
    });
  },

  async obterMinhaAssinatura(): Promise<AssinaturaInfo> {
    return request<AssinaturaInfo>('/api/minha-loja/assinatura');
  },

  async atualizarAnuncio(id: number, dados: any): Promise<Anuncio> {
    return request<Anuncio>(`/api/anuncios/${id}`, {
      method: 'PUT',
      body: JSON.stringify(dados),
    });
  },

  async alterarDisponibilidade(id: number, disponivel?: boolean): Promise<Anuncio> {
    return request<Anuncio>(`/api/anuncios/${id}/disponibilidade`, {
      method: 'PATCH',
      body: JSON.stringify({ disponivel }),
    });
  },

  async renovarAnuncio(id: number): Promise<Anuncio> {
    return request<Anuncio>(`/api/anuncios/${id}/renovar`, {
      method: 'POST',
    });
  },

  async pausarAnuncio(id: number): Promise<Anuncio> {
    return request<Anuncio>(`/api/anuncios/${id}/pausar`, {
      method: 'POST',
    });
  },

  async reativarAnuncio(id: number): Promise<Anuncio> {
    return request<Anuncio>(`/api/anuncios/${id}/reativar`, {
      method: 'POST',
    });
  },

  async uploadFotoAnuncio(anuncioId: number, file: File): Promise<{ fotoUrl: string }> {
    const formData = new FormData();
    formData.append('foto', file);

    const token = localStorage.getItem('isac_token');
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};

    const response = await fetch(`${BASE_URL}/api/anuncios/${anuncioId}/foto`, {
      method: 'POST',
      headers,
      body: formData,
    });

    if (!response.ok) {
      let errorMsg = 'Falha no upload da foto.';
      try {
        const errorJson = await response.json();
        if (errorJson.mensagem) errorMsg = errorJson.mensagem;
      } catch {}
      throw new Error(errorMsg);
    }

    return response.json();
  },

  // Área Administrativa
  async adminListarLojas(status?: string): Promise<Loja[]> {
    const url = status ? `/api/admin/lojas?status=${status}` : '/api/admin/lojas';
    return request<Loja[]>(url);
  },

  async adminConfirmarPagamento(lojaId: number): Promise<Loja> {
    return request<Loja>(`/api/admin/lojas/${lojaId}/assinatura/pagamento`, {
      method: 'POST',
    });
  },

  async adminCancelarAssinatura(lojaId: number): Promise<Loja> {
    return request<Loja>(`/api/admin/lojas/${lojaId}/assinatura/cancelamento`, {
      method: 'POST',
    });
  },

  async adminInativarAssinaturasVencidas(): Promise<{ inativadas: number }> {
    return request<{ inativadas: number }>('/api/admin/assinaturas/vencidas/inativacao', {
      method: 'POST',
    });
  },

  async adminListarAnuncios(filtros?: { status?: string }): Promise<Anuncio[]> {
    const q = new URLSearchParams();
    if (filtros?.status) q.append('status', filtros.status);
    const url = q.toString() ? `/api/admin/anuncios?${q}` : '/api/admin/anuncios';
    return request<Anuncio[]>(url);
  },

  async adminAprovarAnuncio(id: number): Promise<Anuncio> {
    return request<Anuncio>(`/api/admin/anuncios/${id}/aprovar`, {
      method: 'POST',
    });
  },

  async adminRejeitarAnuncio(id: number, motivo: string): Promise<Anuncio> {
    return request<Anuncio>(`/api/admin/anuncios/${id}/rejeitar`, {
      method: 'POST',
      body: JSON.stringify({ motivo }),
    });
  },

  async adminListarCategorias(): Promise<Categoria[]> {
    return request<Categoria[]>('/api/admin/categorias');
  },

  async adminSalvarCategoria(dados: { nome: string; ativa?: boolean }): Promise<Categoria> {
    return request<Categoria>('/api/admin/categorias', {
      method: 'POST',
      body: JSON.stringify(dados),
    });
  },

  /** Alias para criação (aceita ícone) */
  async adminCriarCategoria(dados: { nome: string; icone?: string }): Promise<Categoria> {
    return request<Categoria>('/api/admin/categorias', {
      method: 'POST',
      body: JSON.stringify(dados),
    });
  },

  async adminToggleCategoria(id: number, ativa: boolean): Promise<Categoria> {
    return request<Categoria>(`/api/admin/categorias/${id}`, {
      method: 'PATCH',
      body: JSON.stringify({ ativa }),
    });
  },

  async adminDesativarCategoria(id: number): Promise<void> {
    return request<void>(`/api/admin/categorias/${id}`, {
      method: 'DELETE',
    });
  },

  async adminCriarEvento(dados: any): Promise<Evento> {
    return request<Evento>('/api/admin/eventos', {
      method: 'POST',
      body: JSON.stringify(dados),
    });
  },

  async adminListarEventos(): Promise<Evento[]> {
    return request<Evento[]>('/api/admin/eventos');
  },

  async adminSalvarEvento(dados: any): Promise<Evento> {
    return request<Evento>('/api/admin/eventos', {
      method: 'POST',
      body: JSON.stringify(dados),
    });
  },

  async adminExcluirEvento(id: number): Promise<void> {
    return request<void>(`/api/admin/eventos/${id}`, {
      method: 'DELETE',
    });
  },

  async adminListarConteudos(): Promise<Conteudo[]> {
    return request<Conteudo[]>('/api/admin/conteudos');
  },

  async adminAtualizarConteudo(idOuChave: number | string, corpoMarkdownOuDados: string | { titulo?: string; corpoMarkdown?: string }): Promise<Conteudo> {
    const dados = typeof corpoMarkdownOuDados === 'string'
      ? { corpoMarkdown: corpoMarkdownOuDados }
      : corpoMarkdownOuDados;
    return request<Conteudo>(`/api/admin/conteudos/${idOuChave}`, {
      method: 'PUT',
      body: JSON.stringify(dados),
    });
  },

  async adminObterMetricas(): Promise<MetricasAdmin> {
    return request<MetricasAdmin>('/api/admin/metricas');
  },
};
