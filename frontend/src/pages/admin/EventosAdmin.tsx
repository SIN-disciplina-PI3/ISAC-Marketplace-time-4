import { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { Evento } from '../../types';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { CalendarDays, Plus, Trash2 } from 'lucide-react';

const VAZIO = {
  titulo: '',
  descricao: '',
  dataHora: '',
  local: '',
  linkInscricao: '',
};

export default function EventosAdmin() {
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [form, setForm] = useState(VAZIO);
  const [adicionando, setAdicionando] = useState(false);
  const [removendo, setRemovendo] = useState<number | null>(null);

  useEffect(() => {
    carregarEventos();
  }, []);

  async function carregarEventos() {
    setCarregando(true);
    try {
      const data = await api.listarEventos();
      setEventos(data);
    } finally {
      setCarregando(false);
    }
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function adicionar(e: React.FormEvent) {
    e.preventDefault();
    setAdicionando(true);
    try {
      await api.adminCriarEvento({
        titulo: form.titulo,
        descricao: form.descricao,
        dataHora: form.dataHora,
        local: form.local,
        linkInscricao: form.linkInscricao || undefined,
      });
      setForm(VAZIO);
      await carregarEventos();
    } finally {
      setAdicionando(false);
    }
  }

  async function remover(id: number) {
    if (!confirm('Remover este evento?')) return;
    setRemovendo(id);
    try {
      await api.adminExcluirEvento(id);
      await carregarEventos();
    } finally {
      setRemovendo(null);
    }
  }

  if (carregando) return <LoadingSpinner />;

  return (
    <div className="max-w-3xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-6">
        <CalendarDays className="text-isac-green-600" size={26} />
        <h1 className="text-2xl font-bold text-gray-800">Eventos</h1>
      </div>

      {/* Formulário novo evento */}
      <form onSubmit={adicionar} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 mb-8 space-y-4">
        <h2 className="font-semibold text-gray-700 flex items-center gap-2"><Plus size={16} /> Novo evento</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">Título *</label>
            <input name="titulo" value={form.titulo} onChange={handleChange} required className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500" />
          </div>
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">Descrição</label>
            <textarea name="descricao" value={form.descricao} onChange={handleChange} rows={2} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500 resize-none" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Data e hora *</label>
            <input name="dataHora" type="datetime-local" value={form.dataHora} onChange={handleChange} required className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Local *</label>
            <input name="local" value={form.local} onChange={handleChange} required className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500" />
          </div>
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">Link de inscrição</label>
            <input name="linkInscricao" value={form.linkInscricao} onChange={handleChange} type="url" placeholder="https://..." className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-isac-green-500" />
          </div>
        </div>
        <button
          type="submit"
          disabled={adicionando}
          className="flex items-center gap-1 bg-isac-green-600 hover:bg-isac-green-700 text-white px-5 py-2 rounded-lg text-sm font-semibold transition disabled:opacity-60"
        >
          <Plus size={15} /> {adicionando ? 'Adicionando...' : 'Adicionar evento'}
        </button>
      </form>

      {/* Lista */}
      <div className="space-y-3">
        {eventos.map((ev) => (
          <div key={ev.id} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex items-start justify-between gap-4">
            <div className="flex-1 min-w-0">
              <p className="font-semibold text-gray-800 truncate">{ev.titulo}</p>
              <p className="text-xs text-gray-400 mt-0.5">
                {new Date(ev.dataHora).toLocaleDateString('pt-BR', { day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' })}
                {' · '}{ev.local}
              </p>
              {ev.descricao && <p className="text-sm text-gray-500 mt-1 line-clamp-2">{ev.descricao}</p>}
            </div>
            <button
              onClick={() => remover(ev.id)}
              disabled={removendo === ev.id}
              className="text-red-400 hover:text-red-600 transition p-1 flex-shrink-0 disabled:opacity-60"
              title="Remover evento"
            >
              <Trash2 size={18} />
            </button>
          </div>
        ))}
        {eventos.length === 0 && (
          <p className="text-center text-gray-400 py-8">Nenhum evento cadastrado.</p>
        )}
      </div>
    </div>
  );
}
