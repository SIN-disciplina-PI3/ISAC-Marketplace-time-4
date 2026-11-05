import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Search, Filter, ShoppingBag, X } from 'lucide-react';
import { CardAnuncio } from '../components/CardAnuncio';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Anuncio, Categoria } from '../types';
import { api } from '../services/api';

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
        setCategorias(cats);
      } catch (err) {
        console.error('Erro ao carregar categorias:', err);
      }
    }
    carregarCategorias();
  }, []);

  useEffect(() => {
    async function carregarAnuncios() {
      setLoading(true);
      try {
        const res = await api.buscar(termoAtual, categoriaAtual, bairroAtual, paginaAtual) as any;
        setAnuncios(res.content ?? res.anuncios ?? []);
        setTotal(res.totalElements ?? res.total ?? 0);
        setTotalPaginas(res.totalPages ?? res.totalPaginas ?? 0);
      } catch (err) {
        console.error('Erro ao buscar anúncios:', err);
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
        <h1 className="text-3xl font-extrabold text-slate-900">Vitrine Comunitária</h1>
        <p className="text-slate-500 text-sm mt-1">
          Explore os produtos, artesanatos e serviços oferecidos pela comunidade de Enseada dos Corais e região.
        </p>
      </div>

      {/* Formulário de Busca e Filtros */}
      <form onSubmit={aplicarFiltros} className="bg-white p-5 rounded-2xl shadow-sm border border-slate-200 space-y-4">
        <div className="flex flex-col md:flex-row gap-3">
          {/* Busca por termo */}
          <div className="relative flex-1">
            <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={inputTermo}
              onChange={(e) => setInputTermo(e.target.value)}
              placeholder="Buscar por nome ou descrição (ex: tapioca, mel, bolo)..."
              className="w-full pl-10 pr-4 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          {/* Filtro por Categoria */}
          <select
            value={inputCategoria}
            onChange={(e) => setInputCategoria(e.target.value)}
            className="py-2.5 px-3 rounded-lg border border-slate-200 text-sm text-slate-700 bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
          >
            <option value="">Todas as categorias</option>
            {categorias.map((cat) => (
              <option key={cat.id} value={cat.nome}>
                {cat.nome}
              </option>
            ))}
          </select>

          {/* Filtro por Bairro */}
          <select
            value={inputBairro}
            onChange={(e) => setInputBairro(e.target.value)}
            className="py-2.5 px-3 rounded-lg border border-slate-200 text-sm text-slate-700 bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
          >
            <option value="">Todos os bairros</option>
            {bairrosComuns.map((b) => (
              <option key={b} value={b}>
                {b}
              </option>
            ))}
          </select>

          {/* Botões */}
          <button
            type="submit"
            className="bg-emerald-600 hover:bg-emerald-700 text-white font-semibold px-6 py-2.5 rounded-lg text-sm shadow-sm transition active:scale-95 flex items-center justify-center gap-1.5"
          >
            <Filter className="w-4 h-4" /> Filtrar
          </button>

          {(termoAtual || categoriaAtual || bairroAtual) && (
            <button
              type="button"
              onClick={limparFiltros}
              className="text-slate-500 hover:text-slate-800 border border-slate-200 px-3 py-2.5 rounded-lg text-sm transition flex items-center justify-center gap-1"
              title="Limpar filtros"
            >
              <X className="w-4 h-4" /> Limpar
            </button>
          )}
        </div>
      </form>

      {/* Resultados */}
      {loading ? (
        <LoadingSpinner texto="Buscando na vitrine comunitária..." />
      ) : anuncios.length > 0 ? (
        <div className="space-y-6">
          <div className="text-sm text-slate-500">
            Encontramos <strong className="text-slate-800">{total}</strong> {total === 1 ? 'anúncio' : 'anúncios'} disponíveis
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {anuncios.map((anuncio) => (
              <CardAnuncio key={anuncio.id} anuncio={anuncio} />
            ))}
          </div>

          {/* Paginação */}
          {totalPaginas > 1 && (
            <div className="flex justify-center items-center gap-2 pt-8">
              <button
                disabled={paginaAtual === 0}
                onClick={() => mudarPagina(paginaAtual - 1)}
                className="px-4 py-2 rounded-lg border border-slate-200 text-sm font-medium disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50"
              >
                Anterior
              </button>
              <span className="text-sm text-slate-600 px-3">
                Página {paginaAtual + 1} de {totalPaginas}
              </span>
              <button
                disabled={paginaAtual >= totalPaginas - 1}
                onClick={() => mudarPagina(paginaAtual + 1)}
                className="px-4 py-2 rounded-lg border border-slate-200 text-sm font-medium disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50"
              >
                Próxima
              </button>
            </div>
          )}
        </div>
      ) : (
        <div className="text-center py-16 bg-white rounded-2xl border border-slate-200 p-8 space-y-4">
          <ShoppingBag className="w-16 h-16 text-slate-300 mx-auto" />
          <h2 className="text-lg font-bold text-slate-800">Nenhum anúncio encontrado</h2>
          <p className="text-sm text-slate-500 max-w-md mx-auto">
            Não encontramos nenhum produto ou serviço correspondente aos critérios informados. Tente buscar por outros termos ou limpar os filtros.
          </p>
          <button
            onClick={limparFiltros}
            className="inline-flex items-center gap-1.5 text-sm font-semibold text-emerald-600 hover:text-emerald-700 bg-emerald-50 px-4 py-2 rounded-lg"
          >
            Ver todos os anúncios
          </button>
        </div>
      )}
    </div>
  );
};

export default Vitrine;
