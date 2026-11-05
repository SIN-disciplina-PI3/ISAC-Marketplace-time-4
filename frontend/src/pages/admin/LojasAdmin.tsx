import { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { Loja } from '../../types';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { Store, CheckCircle, XCircle } from 'lucide-react';

export default function LojasAdmin() {
  const [lojas, setLojas] = useState<Loja[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState('');
  const [processando, setProcessando] = useState<number | null>(null);

  useEffect(() => {
    carregarLojas();
  }, []);

  async function carregarLojas() {
    setCarregando(true);
    try {
      const data = await api.adminListarLojas();
      setLojas(data);
    } finally {
      setCarregando(false);
    }
  }

  async function confirmarPagamento(lojaId: number) {
    setProcessando(lojaId);
    try {
      await api.adminConfirmarPagamento(lojaId);
      await carregarLojas();
    } finally {
      setProcessando(null);
    }
  }

  async function cancelarAssinatura(lojaId: number) {
    if (!confirm('Confirmar cancelamento da assinatura?')) return;
    setProcessando(lojaId);
    try {
      await api.adminCancelarAssinatura(lojaId);
      await carregarLojas();
    } finally {
      setProcessando(null);
    }
  }

  const lojasFiltradas = lojas.filter(
    (l) =>
      !filtro ||
      l.nomeLoja.toLowerCase().includes(filtro.toLowerCase()) ||
      l.cnpj.includes(filtro)
  );

  const corStatus: Record<string, string> = {
    ATIVA: 'bg-green-100 text-green-700',
    TRIAL: 'bg-blue-100 text-blue-700',
    INADIMPLENTE: 'bg-red-100 text-red-700',
    CANCELADA: 'bg-gray-100 text-gray-500',
    PENDENTE_PAGAMENTO: 'bg-yellow-100 text-yellow-700',
  };

  if (carregando) return <LoadingSpinner />;

  return (
    <div className="max-w-6xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-6">
        <Store className="text-isac-green-600" size={26} />
        <h1 className="text-2xl font-bold text-gray-800">Lojas & Assinaturas</h1>
      </div>

      <input
        value={filtro}
        onChange={(e) => setFiltro(e.target.value)}
        placeholder="Filtrar por nome ou CNPJ..."
        className="mb-5 w-full max-w-sm border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500"
      />

      <div className="overflow-x-auto bg-white rounded-2xl shadow-sm border border-gray-100">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-100">
              <th className="text-left px-4 py-3 font-semibold text-gray-600">Loja</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">CNPJ</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">Bairro</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">Assinatura</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">Plano</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody>
            {lojasFiltradas.map((loja) => (
              <tr key={loja.id} className="border-b border-gray-50 hover:bg-gray-50 transition">
                <td className="px-4 py-3 font-medium text-gray-800">{loja.nomeLoja}</td>
                <td className="px-4 py-3 text-gray-500">{loja.cnpj}</td>
                <td className="px-4 py-3 text-gray-500">{loja.bairro}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-1 rounded-full text-xs font-semibold ${corStatus[loja.statusAssinatura] ?? 'bg-gray-100 text-gray-500'}`}>
                    {loja.statusAssinatura}
                  </span>
                </td>
                <td className="px-4 py-3 text-gray-500">{loja.planoAssinatura ?? '—'}</td>
                <td className="px-4 py-3 flex gap-2 justify-end">
                  {loja.statusAssinatura === 'PENDENTE_PAGAMENTO' && (
                    <button
                      onClick={() => confirmarPagamento(loja.id)}
                      disabled={processando === loja.id}
                      className="flex items-center gap-1 text-xs bg-green-100 text-green-700 hover:bg-green-200 px-3 py-1 rounded-lg font-semibold transition disabled:opacity-60"
                    >
                      <CheckCircle size={14} /> Confirmar
                    </button>
                  )}
                  {(loja.statusAssinatura === 'ATIVA' || loja.statusAssinatura === 'TRIAL') && (
                    <button
                      onClick={() => cancelarAssinatura(loja.id)}
                      disabled={processando === loja.id}
                      className="flex items-center gap-1 text-xs bg-red-100 text-red-700 hover:bg-red-200 px-3 py-1 rounded-lg font-semibold transition disabled:opacity-60"
                    >
                      <XCircle size={14} /> Cancelar
                    </button>
                  )}
                </td>
              </tr>
            ))}
            {lojasFiltradas.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-400">Nenhuma loja encontrada.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
