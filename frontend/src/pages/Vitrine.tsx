import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Search, Filter, ShoppingBag, X } from 'lucide-react';
import { CardAnuncio } from '../components/CardAnuncio';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Anuncio, Categoria } from '../types';
import { api } from '../services/api';

const ANUNCIOS_FALLBACK: Anuncio[] = [
  {
    id: 1,
    nomeLoja: 'Tapiocaria da Enseada',
    titulo: 'Tapioca Tradicional com Queijo Coalho e Coco',
    descricao: 'Feita na hora com goma fresca e queijo coalho assado no ponto.',
    preco: 12.00,
    categoria: 'Alimentação',
    nomeCategoria: 'Alimentação',
    bairro: 'Enseada dos Corais',
    status: 'PUBLICADO',
    destaque: true,
    fotoUrl: 'https://images.unsplash.com/photo-1598514983318-2f64f8f4796c?w=500',
    fotos: ['https://images.unsplash.com/photo-1598514983318-2f64f8f4796c?w=500'],
    telefoneWhatsapp: '81999990001',
    linkWaMe: 'https://wa.me/5581999990001?text=Ol%C3%A1!%20Vi%20seu%20an%C3%BAncio%20de%20Tapioca%20no%20ISAC%20Marketplace',
  },
  {
    id: 2,
    nomeLoja: 'Tapiocaria da Enseada',
    titulo: 'Bolo de Rolo Pernambucano Artesanal (500g)',
    descricao: 'Massa fina tradicional enrolada com goiabada cascão cremosa.',
    preco: 25.00,
    categoria: 'Alimentação',
    nomeCategoria: 'Alimentação',
    bairro: 'Enseada dos Corais',
    status: 'PUBLICADO',
    destaque: true,
    fotoUrl: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500',
    fotos: ['https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500'],
    telefoneWhatsapp: '81999990001',
    linkWaMe: 'https://wa.me/5581999990001?text=Ol%C3%A1!%20Vi%20seu%20an%C3%BAncio%20de%20Bolo%20de%20Rolo%20no%20ISAC%20Marketplace',
  },
  {
    id: 5,
    nomeLoja: 'Artesanato Corais',
    titulo: 'Luminária Rústica em Conchas e Fibras Naturais',
    descricao: 'Peça única decorativa feita à mão com materiais coletados no litoral.',
    preco: 45.00,
    categoria: 'Artesanato',
    nomeCategoria: 'Artesanato',
    bairro: 'Enseada dos Corais',
    status: 'PUBLICADO',
    destaque: false,
    fotoUrl: 'https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500',
    fotos: ['https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500'],
    telefoneWhatsapp: '81999990002',
    linkWaMe: 'https://wa.me/5581999990002?text=Ol%C3%A1!%20Vi%20seu%20an%C3%BAncio%20de%20Lumin%C3%A1ria%20no%20ISAC%20Marketplace',
  },
  {
    id: 7,
    nomeLoja: 'Horta Agroecológica Gaibu',
    titulo: 'Cesta de Verduras Orgânicas da Semana',
    descricao: 'Alface, rúcula, coentro, couve e cheiro verde colhidos na manhã.',
    preco: 35.00,
    categoria: 'Agroecologia',
    nomeCategoria: 'Agroecologia',
    bairro: 'Gaibu',
    status: 'PUBLICADO',
    destaque: false,
    fotoUrl: 'https://images.unsplash.com/photo-1610348725531-843dff563e2c?w=500',
    fotos: ['https://images.unsplash.com/photo-1610348725531-843dff563e2c?w=500'],
    telefoneWhatsapp: '81999990003',
    linkWaMe: 'https://wa.me/5581999990003?text=Ol%C3%A1!%20Vi%20seu%20an%C3%BAncio%20de%20Cesta%20Org%C3%A2nica%20no%20ISAC%20Marketplace',
  },
];

export const Vitrine: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [anuncios, setAnuncios] = useState<Anuncio[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [loading, setLoading] = useState(true);

  const termoAtual = searchParams.get('termo') || '';
  const categoriaAtual = searchParams.get('categoria') || '';
  const bairroAtual = searchParams.get('bairro') || '';
  const paginaAtual = Number(searchParams.get('pagina')) || 0;

  const [inputTermo, setInputTermo] = useState(termoAtual);
  const [inputCategoria, setInputCategoria] = useState(categoriaAtual);
  const [inputBairro, setInputBairro] = useState(bairroAtual);

  const bairrosComuns = [
    'Enseada dos Corais',
    'Gaibu',
    'Paiva',
    'Suape',
    'Cabo Centro',
    'Nazaré',
  ];

  useEffect(() => {
    async function carregarCategorias() {
      try {
        const cats = await api.listarCategorias();
        if (cats && Array.isArray(cats) && cats.length > 0) {
          setCategorias(cats);
        }
      } catch (err) {
        console.warn('Categorias não carregadas da API, usando padrão:', err);
      }
    }
    carregarCategorias();
  }, []);

  useEffect(() => {
    async function carregarAnuncios() {
      setLoading(true);
      try {
        const res = await api.buscar({
          termo: termoAtual,
          categoria: categoriaAtual,
          bairro: bairroAtual,
          pagina: paginaAtual,
        });

        const lista = res?.content ?? [];
        setAnuncios(lista);
        setTotal(res?.totalElements ?? lista.length);
        setTotalPaginas(res?.totalPages ?? (lista.length > 0 ? 1 : 0));
      } catch (err) {
        console.warn('API indisponível na vitrine, usando catálogo de demonstração:', err);
        // Fallback apenas quando a requisição falha (erro de rede/500)
        setAnuncios(ANUNCIOS_FALLBACK);
        setTotal(ANUNCIOS_FALLBACK.length);
        setTotalPaginas(1);
      } finally {
        setLoading(false);
      }
    }
    carregarAnuncios();
  }, [termoAtual, categoriaAtual, bairroAtual, paginaAtual]);

  const aplicarFiltros = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    const params: Record<string, string> = {};
    if (inputTermo.trim()) params.termo = inputTermo.trim();
    if (inputCategoria) params.categoria = inputCategoria;
    if (inputBairro) params.bairro = inputBairro;
    params.pagina = '0';
    setSearchParams(params);
  };

  const limparFiltros = () => {
    setInputTermo('');
    setInputCategoria('');
    setInputBairro('');
    setSearchParams({});
  };

  const mudarPagina = (novaPagina: number) => {
    const params = new URLSearchParams(searchParams);
    params.set('pagina', String(novaPagina));
    setSearchParams(params);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Cabeçalho */}
      <div>
        <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
          Vitrine de Produtos e Serviços
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Explore o comércio local da Enseada dos Corais, Gaibu e região.
        </p>
      </div>

      {/* Barra de Filtros */}
      <form onSubmit={aplicarFiltros} className="bg-white rounded-2xl border border-slate-200 p-4 sm:p-6 shadow-sm space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
          {/* Termo */}
          <div className="sm:col-span-2 relative">
            <Search className="w-5 h-5 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={inputTermo}
              onChange={(e) => setInputTermo(e.target.value)}
              placeholder="Buscar por nome ou descrição (ex: tapioca, mel, bolo)..."
              className="w-full pl-10 pr-4 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          {/* Categoria */}
          <div>
            <select
              value={inputCategoria}
              onChange={(e) => setInputCategoria(e.target.value)}
              className="w-full py-2.5 px-3 rounded-lg border border-slate-200 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500 text-slate-700"
            >
              <option value="">Todas as Categorias</option>
              {categorias.map((cat) => (
                <option key={cat.id} value={cat.nome}>
                  {cat.nome}
                </option>
              ))}
              {categorias.length === 0 && (
                <>
                  <option value="Alimentação">Alimentação</option>
                  <option value="Artesanato">Artesanato</option>
                  <option value="Agroecologia">Agroecologia</option>
                  <option value="Serviços">Serviços</option>
                  <option value="Moda e Vestuário">Moda e Vestuário</option>
                </>
              )}
            </select>
          </div>

          {/* Bairro */}
          <div>
            <select
              value={inputBairro}
              onChange={(e) => setInputBairro(e.target.value)}
              className="w-full py-2.5 px-3 rounded-lg border border-slate-200 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500 text-slate-700"
            >
              <option value="">Todos os Bairros</option>
              {bairrosComuns.map((b) => (
                <option key={b} value={b}>
                  {b}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Botões */}
        <div className="flex flex-wrap items-center justify-between gap-3 pt-2 border-t border-slate-100">
          <div className="text-xs text-slate-500">
            {total > 0 ? (
              <span>Exibindo <strong>{anuncios.length}</strong> de <strong>{total}</strong> anúncios</span>
            ) : (
              <span>Nenhum filtro ativo</span>
            )}
          </div>

          <div className="flex items-center gap-2">
            {(termoAtual || categoriaAtual || bairroAtual) && (
              <button
                type="button"
                onClick={limparFiltros}
                className="inline-flex items-center gap-1 text-xs font-semibold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 px-3 py-2 rounded-lg transition"
              >
                <X className="w-3.5 h-3.5" /> Limpar filtros
              </button>
            )}
            <button
              type="submit"
              className="inline-flex items-center gap-1.5 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 px-4 py-2 rounded-lg shadow-sm transition active:scale-95"
            >
              <Filter className="w-3.5 h-3.5" /> Filtrar resultados
            </button>
          </div>
        </div>
      </form>

      {/* Resultados */}
      {loading ? (
        <LoadingSpinner texto="Consultando anúncios da comunidade..." />
      ) : anuncios.length > 0 ? (
        <div className="space-y-8">
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {anuncios.map((anuncio) => (
              <CardAnuncio key={anuncio.id} anuncio={anuncio} />
            ))}
          </div>

          {/* Paginação */}
          {totalPaginas > 1 && (
            <div className="flex justify-center items-center gap-2 pt-6">
              <button
                onClick={() => mudarPagina(paginaAtual - 1)}
                disabled={paginaAtual <= 0}
                className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40"
              >
                Anterior
              </button>

              <span className="text-xs text-slate-500 px-2">
                Página {paginaAtual + 1} de {totalPaginas}
              </span>

              <button
                onClick={() => mudarPagina(paginaAtual + 1)}
                disabled={paginaAtual >= totalPaginas - 1}
                className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40"
              >
                Próxima
              </button>
            </div>
          )}
        </div>
      ) : (
        <div className="text-center py-16 bg-white rounded-2xl border border-slate-200 p-8 space-y-3">
          <div className="w-12 h-12 bg-slate-100 text-slate-400 rounded-full flex items-center justify-center mx-auto">
            <ShoppingBag className="w-6 h-6" />
          </div>
          <h2 className="text-lg font-bold text-slate-800">Nenhum anúncio encontrado</h2>
          <p className="text-xs sm:text-sm text-slate-500 max-w-sm mx-auto">
            Não encontramos nenhum produto ou serviço correspondente aos critérios informados. Tente buscar por outros termos ou limpar os filtros.
          </p>
          {(termoAtual || categoriaAtual || bairroAtual) && (
            <button
              onClick={limparFiltros}
              className="text-xs font-bold text-emerald-600 hover:text-emerald-700 underline mt-2"
            >
              Ver todos os anúncios
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default Vitrine;
