import React, { useEffect, useState } from 'react';
import { Calendar, MapPin, Clock } from 'lucide-react';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Evento } from '../types';
import { api } from '../services/api';

export const Eventos: React.FC = () => {
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function carregar() {
      try {
        const dados = await api.listarEventos();
        setEventos(dados);
      } catch (err) {
        console.error('Erro ao carregar eventos:', err);
      } finally {
        setLoading(false);
      }
    }
    carregar();
  }, []);

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      <div>
        <h1 className="text-3xl font-extrabold text-slate-900">Agenda de Eventos do ISAC</h1>
        <p className="text-sm text-slate-500 mt-1">
          Participe das nossas feiras comunitárias, oficinas de agricultura familiar e mutirões de preservação ambiental no Cabo de Santo Agostinho.
        </p>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : eventos.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {eventos.map((ev) => (
            <div key={ev.id} className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
              <div className="inline-flex items-center gap-1.5 bg-emerald-50 text-emerald-800 text-xs font-bold px-3 py-1 rounded-full">
                <Calendar className="w-3.5 h-3.5" />
                {new Date(ev.dataInicio).toLocaleDateString('pt-BR', {
                  weekday: 'short',
                  day: 'numeric',
                  month: 'long',
                })}
              </div>

              <h2 className="text-xl font-bold text-slate-900">{ev.titulo}</h2>

              {ev.descricao && (
                <p className="text-sm text-slate-600 leading-relaxed whitespace-pre-line">
                  {ev.descricao}
                </p>
              )}

              <div className="border-t border-slate-100 pt-3 flex flex-col gap-1.5 text-xs text-slate-500">
                <span className="flex items-center gap-1.5">
                  <MapPin className="w-3.5 h-3.5 text-slate-400" />
                  <strong>Local:</strong> {ev.local}
                </span>
                {ev.dataFim && (
                  <span className="flex items-center gap-1.5">
                    <Clock className="w-3.5 h-3.5 text-slate-400" />
                    <strong>Término:</strong> {new Date(ev.dataFim).toLocaleDateString('pt-BR')}
                  </span>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-12 bg-white rounded-2xl border border-slate-200 p-8">
          <Calendar className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <p className="text-slate-600 font-medium">Nenhum evento agendado no momento.</p>
        </div>
      )}
    </div>
  );
};

export default Eventos;
