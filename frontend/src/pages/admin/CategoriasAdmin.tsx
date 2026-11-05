import { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { Categoria } from '../../types';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { Tag, Plus, ToggleLeft, ToggleRight } from 'lucide-react';

export default function CategoriasAdmin() {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [novoNome, setNovoNome] = useState('');
  const [novoIcone, setNovoIcone] = useState('🛍️');
  const [adicionando, setAdicionando] = useState(false);
  const [processando, setProcessando] = useState<number | null>(null);

  useEffect(() => {
    carregarCategorias();
  }, []);

  async function carregarCategorias() {
    setCarregando(true);
    try {
      const data = await api.listarCategorias();
      setCategorias(data);
    } finally {
      setCarregando(false);
    }
  }

  async function adicionarCategoria(e: React.FormEvent) {
    e.preventDefault();
    if (!novoNome.trim()) return;
    setAdicionando(true);
    try {
      await api.adminCriarCategoria({ nome: novoNome.trim(), icone: novoIcone.trim() });
      setNovoNome('');
      setNovoIcone('🛍️');
      await carregarCategorias();
    } finally {
      setAdicionando(false);
    }
  }

  async function toggleAtiva(cat: Categoria) {
    setProcessando(cat.id);
    try {
      await api.adminToggleCategoria(cat.id, !cat.ativa);
      await carregarCategorias();
    } finally {
      setProcessando(null);
    }
  }

  if (carregando) return <LoadingSpinner />;

  return (
    <div className="max-w-2xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-6">
        <Tag className="text-isac-green-600" size={26} />
        <h1 className="text-2xl font-bold text-gray-800">Categorias</h1>
      </div>

      {/* Formulário nova categoria */}
      <form onSubmit={adicionarCategoria} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 mb-6 flex gap-3 items-end">
        <div className="flex-1">
          <label className="block text-sm font-medium text-gray-700 mb-1">Nova categoria</label>
          <input
            value={novoNome}
            onChange={(e) => setNovoNome(e.target.value)}
            placeholder="Ex: Alimentação"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Ícone</label>
          <input
            value={novoIcone}
            onChange={(e) => setNovoIcone(e.target.value)}
            className="w-16 border border-gray-300 rounded-lg px-2 py-2 text-lg text-center focus:outline-none focus:ring-2 focus:ring-isac-green-500"
          />
        </div>
        <button
          type="submit"
          disabled={adicionando || !novoNome.trim()}
          className="flex items-center gap-1 bg-isac-green-600 hover:bg-isac-green-700 text-white px-4 py-2 rounded-lg text-sm font-semibold transition disabled:opacity-60"
        >
          <Plus size={16} /> Adicionar
        </button>
      </form>

      {/* Lista */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        {categorias.map((cat, idx) => (
          <div
            key={cat.id}
            className={`flex items-center justify-between px-5 py-3 ${idx < categorias.length - 1 ? 'border-b border-gray-50' : ''} ${!cat.ativa ? 'opacity-50' : ''}`}
          >
            <div className="flex items-center gap-3">
              <span className="text-xl">{cat.icone}</span>
              <span className="font-medium text-gray-800">{cat.nome}</span>
            </div>
            <button
              onClick={() => toggleAtiva(cat)}
              disabled={processando === cat.id}
              className={`flex items-center gap-1 text-xs px-3 py-1 rounded-lg font-semibold transition disabled:opacity-60 ${cat.ativa ? 'bg-green-100 text-green-700 hover:bg-green-200' : 'bg-gray-100 text-gray-500 hover:bg-gray-200'}`}
            >
              {cat.ativa ? <ToggleRight size={15} /> : <ToggleLeft size={15} />}
              {cat.ativa ? 'Ativa' : 'Inativa'}
            </button>
          </div>
        ))}
        {categorias.length === 0 && (
          <p className="px-5 py-8 text-center text-gray-400">Nenhuma categoria cadastrada.</p>
        )}
      </div>
    </div>
  );
}
