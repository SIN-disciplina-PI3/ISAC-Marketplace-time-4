import { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { Anuncio } from '../../types';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ShoppingBag, CheckCircle, XCircle } from 'lucide-react';

export default function AnunciosAdmin() {
  const [anuncios, setAnuncios] = useState<Anuncio[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [processando, setProcessando] = useState<number | null>(null);
  const [modalRejeitar, setModalRejeitar] = useState<Anuncio | null>(null);
  const [motivoRejeicao, setMotivoRejeicao] = useState('');

  useEffect(() => {
    carregarAnuncios();
  }, []);

  async function carregarAnuncios() {
    setCarregando(true);
    try {
      const data = await api.adminListarAnuncios({ status: 'PENDENTE_MODERACAO' });
      setAnuncios(data);
    } finally {
      setCarregando(false);
    }
  }

  async function aprovar(id: number) {
    setProcessando(id);
    try {
      await api.adminAprovarAnuncio(id);
      await carregarAnuncios();
    } finally {
      setProcessando(null);
    }
  }

  async function rejeitar() {
    if (!modalRejeitar || !motivoRejeicao.trim()) return;
    setProcessando(modalRejeitar.id);
    try {
      await api.adminRejeitarAnuncio(modalRejeitar.id, motivoRejeicao);
      setModalRejeitar(null);
      setMotivoRejeicao('');
      await carregarAnuncios();
    } finally {
      setProcessando(null);
    }
  }

  if (carregando) return <LoadingSpinner />;

  return (
    <div className="max-w-6xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-6">
        <ShoppingBag className="text-isac-green-600" size={26} />
        <h1 className="text-2xl font-bold text-gray-800">Moderação de Anúncios</h1>
        {anuncios.length > 0 && (
          <span className="ml-2 bg-yellow-100 text-yellow-700 text-xs font-bold px-2 py-1 rounded-full">
            {anuncios.length} pendente{anuncios.length > 1 ? 's' : ''}
          </span>
        )}
      </div>

      {anuncios.length === 0 ? (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-12 text-center text-gray-400">
          <ShoppingBag size={40} className="mx-auto mb-3 opacity-30" />
          <p>Nenhum anúncio pendente de moderação.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {anuncios.map((anuncio) => (
            <div key={anuncio.id} className="bg-white rounded-2xl border border-yellow-100 shadow-sm p-5">
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold text-gray-800 truncate">{anuncio.titulo}</h3>
                  <p className="text-sm text-gray-500 mt-1 line-clamp-2">{anuncio.descricao}</p>
                  <div className="flex gap-3 mt-2 text-xs text-gray-400">
                    <span>Loja: <strong className="text-gray-600">{anuncio.nomeLoja}</strong></span>
                    {anuncio.preco != null && (
                      <span>R$ {anuncio.preco.toFixed(2)}</span>
                    )}
                    <span>{anuncio.categoria}</span>
                  </div>
                </div>
                {anuncio.fotos?.[0] && (
                  <img
                    src={anuncio.fotos[0]}
                    alt={anuncio.titulo}
                    className="w-20 h-20 object-cover rounded-xl border border-gray-100 flex-shrink-0"
                  />
                )}
              </div>
              <div className="flex gap-3 mt-4">
                <button
                  onClick={() => aprovar(anuncio.id)}
                  disabled={processando === anuncio.id}
                  className="flex items-center gap-1 text-sm bg-green-100 text-green-700 hover:bg-green-200 px-4 py-2 rounded-lg font-semibold transition disabled:opacity-60"
                >
                  <CheckCircle size={16} /> Aprovar
                </button>
                <button
                  onClick={() => { setModalRejeitar(anuncio); setMotivoRejeicao(''); }}
                  disabled={processando === anuncio.id}
                  className="flex items-center gap-1 text-sm bg-red-100 text-red-700 hover:bg-red-200 px-4 py-2 rounded-lg font-semibold transition disabled:opacity-60"
                >
                  <XCircle size={16} /> Rejeitar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal rejeição */}
      {modalRejeitar && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6">
            <h3 className="font-bold text-gray-800 mb-1">Rejeitar anúncio</h3>
            <p className="text-sm text-gray-500 mb-4">"{modalRejeitar.titulo}"</p>
            <label className="block text-sm font-medium text-gray-700 mb-1">Motivo da rejeição *</label>
            <textarea
              value={motivoRejeicao}
              onChange={(e) => setMotivoRejeicao(e.target.value)}
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400 resize-none"
              placeholder="Descreva o motivo para o lojista..."
            />
            <div className="flex gap-3 mt-4 justify-end">
              <button
                onClick={() => setModalRejeitar(null)}
                className="px-4 py-2 text-sm text-gray-600 hover:text-gray-800 transition"
              >
                Cancelar
              </button>
              <button
                onClick={rejeitar}
                disabled={!motivoRejeicao.trim() || processando === modalRejeitar.id}
                className="flex items-center gap-1 text-sm bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg font-semibold transition disabled:opacity-60"
              >
                <XCircle size={15} /> Confirmar rejeição
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
