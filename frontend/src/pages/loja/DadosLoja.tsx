import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../services/api';
import { Loja } from '../../types';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { Save, Store } from 'lucide-react';

export default function DadosLoja() {
  const { usuario } = useAuth();
  const [loja, setLoja] = useState<Loja | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [sucesso, setSucesso] = useState(false);
  const [erro, setErro] = useState('');

  const [form, setForm] = useState({
    nomeLoja: '',
    descricao: '',
    bairro: '',
    telefoneWhatsapp: '',
    logoUrl: '',
    categoriaId: '',
  });

  useEffect(() => {
    api.obterMinhaLoja()
      .then((data) => {
        setLoja(data);
        setForm({
          nomeLoja: data.nomeLoja ?? '',
          descricao: data.descricao ?? '',
          bairro: data.bairro ?? '',
          telefoneWhatsapp: data.telefoneWhatsapp ?? '',
          logoUrl: data.logoUrl ?? '',
          categoriaId: data.categoria?.id?.toString() ?? '',
        });
      })
      .catch(() => setErro('Erro ao carregar dados da loja.'))
      .finally(() => setCarregando(false));
  }, []);

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    setForm({ ...form, [e.target.name]: e.target.value });
    setSucesso(false);
    setErro('');
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSalvando(true);
    setErro('');
    try {
      await api.atualizarMinhaLoja({
        nomeLoja: form.nomeLoja,
        descricao: form.descricao,
        bairro: form.bairro,
        telefoneWhatsapp: form.telefoneWhatsapp,
        logoUrl: form.logoUrl || undefined,
      });
      setSucesso(true);
    } catch {
      setErro('Erro ao salvar. Verifique os dados e tente novamente.');
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) return <LoadingSpinner />;

  return (
    <div className="max-w-2xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-8">
        <Store className="text-isac-green-600" size={28} />
        <h1 className="text-2xl font-bold text-gray-800">Dados da Loja</h1>
      </div>

      {erro && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
          {erro}
        </div>
      )}
      {sucesso && (
        <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg text-sm">
          Dados salvos com sucesso!
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Nome da loja *</label>
          <input
            name="nomeLoja"
            value={form.nomeLoja}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Descrição</label>
          <textarea
            name="descricao"
            value={form.descricao}
            onChange={handleChange}
            rows={3}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500 resize-none"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Bairro *</label>
          <input
            name="bairro"
            value={form.bairro}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">WhatsApp (com DDD, apenas números) *</label>
          <input
            name="telefoneWhatsapp"
            value={form.telefoneWhatsapp}
            onChange={handleChange}
            required
            placeholder="81999990000"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">URL do logotipo</label>
          <input
            name="logoUrl"
            value={form.logoUrl}
            onChange={handleChange}
            type="url"
            placeholder="https://..."
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500"
          />
          {form.logoUrl && (
            <img src={form.logoUrl} alt="Logo" className="mt-2 h-16 w-16 object-contain rounded border" />
          )}
        </div>

        <div className="pt-2">
          <button
            type="submit"
            disabled={salvando}
            className="flex items-center gap-2 bg-isac-green-600 hover:bg-isac-green-700 text-white font-semibold px-6 py-2 rounded-lg transition disabled:opacity-60"
          >
            <Save size={16} />
            {salvando ? 'Salvando...' : 'Salvar alterações'}
          </button>
        </div>
      </form>

      {loja && (
        <p className="mt-4 text-xs text-gray-400 text-center">
          Usuário: {usuario?.email} · CNPJ cadastrado: {loja.cnpj}
        </p>
      )}
    </div>
  );
}
