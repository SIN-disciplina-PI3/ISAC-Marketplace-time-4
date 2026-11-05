import { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { Conteudo } from '../../types';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { FileText, Save, Edit3, X } from 'lucide-react';

export default function ConteudosAdmin() {
  const [conteudos, setConteudos] = useState<Conteudo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [editando, setEditando] = useState<number | null>(null);
  const [rascunhos, setRascunhos] = useState<Record<number, string>>({});
  const [salvando, setSalvando] = useState<number | null>(null);

  useEffect(() => {
    api.adminListarConteudos()
      .then(setConteudos)
      .finally(() => setCarregando(false));
  }, []);

  function iniciarEdicao(c: Conteudo) {
    setEditando(c.id);
    setRascunhos({ ...rascunhos, [c.id]: c.corpoMarkdown });
  }

  function cancelarEdicao() {
    setEditando(null);
  }

  async function salvar(id: number) {
    setSalvando(id);
    const conteudo = conteudos.find((c) => c.id === id);
    if (!conteudo) return;
    try {
      const atualizado = await api.adminAtualizarConteudo(conteudo.chave, {
        corpoMarkdown: rascunhos[id] ?? '',
      });
      setConteudos(conteudos.map((c) => (c.id === id ? atualizado : c)));
      setEditando(null);
    } finally {
      setSalvando(null);
    }
  }

  if (carregando) return <LoadingSpinner />;

  return (
    <div className="max-w-3xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-6">
        <FileText className="text-isac-green-600" size={26} />
        <h1 className="text-2xl font-bold text-gray-800">Conteúdos Institucionais</h1>
      </div>
      <p className="text-sm text-gray-500 mb-6">
        Edite os textos institucionais exibidos no site. Use sintaxe Markdown.
      </p>

      <div className="space-y-4">
        {conteudos.map((c) => (
          <div key={c.id} className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-100">
              <div>
                <p className="font-semibold text-gray-800">{c.titulo}</p>
                <p className="text-xs text-gray-400 font-mono">{c.chave}</p>
              </div>
              {editando === c.id ? (
                <div className="flex gap-2">
                  <button
                    onClick={cancelarEdicao}
                    className="flex items-center gap-1 text-xs text-gray-500 hover:text-gray-700 px-3 py-1 rounded-lg transition"
                  >
                    <X size={14} /> Cancelar
                  </button>
                  <button
                    onClick={() => salvar(c.id)}
                    disabled={salvando === c.id}
                    className="flex items-center gap-1 text-xs bg-isac-green-600 hover:bg-isac-green-700 text-white px-3 py-1 rounded-lg font-semibold transition disabled:opacity-60"
                  >
                    <Save size={14} /> {salvando === c.id ? 'Salvando...' : 'Salvar'}
                  </button>
                </div>
              ) : (
                <button
                  onClick={() => iniciarEdicao(c)}
                  className="flex items-center gap-1 text-xs text-isac-blue-600 hover:text-isac-blue-700 px-3 py-1 rounded-lg border border-isac-blue-200 hover:border-isac-blue-400 transition"
                >
                  <Edit3 size={14} /> Editar
                </button>
              )}
            </div>
            <div className="p-5">
              {editando === c.id ? (
                <textarea
                  value={rascunhos[c.id] ?? ''}
                  onChange={(e) => setRascunhos({ ...rascunhos, [c.id]: e.target.value })}
                  rows={10}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm font-mono focus:outline-none focus:ring-2 focus:ring-isac-green-500 resize-y"
                />
              ) : (
                <pre className="text-sm text-gray-600 whitespace-pre-wrap font-mono bg-gray-50 rounded-lg p-3 max-h-48 overflow-y-auto">
                  {c.corpoMarkdown || <span className="text-gray-300 italic">[sem conteúdo]</span>}
                </pre>
              )}
            </div>
          </div>
        ))}

        {conteudos.length === 0 && (
          <p className="text-center text-gray-400 py-8">Nenhum conteúdo institucional cadastrado.</p>
        )}
      </div>
    </div>
  );
}
