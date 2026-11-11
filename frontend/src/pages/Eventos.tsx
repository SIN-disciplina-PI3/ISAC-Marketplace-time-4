import React, { useEffect, useState } from 'react';
import { Calendar, MapPin, Clock, Share2, Sparkles, CheckCircle2, MessageCircle } from 'lucide-react';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { TextoFormatado } from '../components/TextoFormatado';
import { Evento } from '../types';
import { api } from '../services/api';

const EVENTOS_FALLBACK: Evento[] = [
  {
    id: 1,
    titulo: 'Feira Agroecológica e Cultural de Enseada dos Corais',
    descricao:
      'Encontro mensal reunindo produtores familiares, artesãos locais e atrações culturais do Cabo de Santo Agostinho.\n\nVenha conhecer frutas, hortaliças orgânicas, tapiocas e artesanato de conchas e palha.',
    local: 'Praça Central de Enseada dos Corais',
    dataHora: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString(),
    fotoUrl: 'https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=800&q=80',
    publicado: true,
  },
  {
    id: 2,
    titulo: 'Mutirão Comunitário de Limpeza e Preservação das Praias',
    descricao:
      'Ação coletiva do ISAC com voluntários e comunidade para conscientização ambiental e preservação do litoral.\n\nFoco no recolhimento de microplásticos e sensibilização dos banhistas.',
    local: 'Orla da Enseada dos Corais e Gaibu',
    dataHora: new Date(Date.now() + 12 * 24 * 60 * 60 * 1000).toISOString(),
    fotoUrl: 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?auto=format&fit=crop&w=800&q=80',
    publicado: true,
  },
  {
    id: 3,
    titulo: 'Oficina de Agroecologia e Compostagem Doméstica',
    descricao:
      'Aprenda práticas simples de manejo sustentável do solo, adubação orgânica e reciclagem de resíduos alimentares no seu quintal.',
    local: 'Sede Comunitária do ISAC - Enseada dos Corais',
    dataHora: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
    fotoUrl: 'https://images.unsplash.com/photo-1509099836639-18ba1795216d?auto=format&fit=crop&w=800&q=80',
    publicado: true,
  },
];

export const Eventos: React.FC = () => {
  const [eventos, setEventos] = useState<Evento[]>(EVENTOS_FALLBACK);
  const [loading, setLoading] = useState(true);

  const fallbackImagens = [
    'https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1511556532299-8f662fc26c06?auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1509099836639-18ba1795216d?auto=format&fit=crop&w=800&q=80',
  ];

  useEffect(() => {
    api.listarEventos()
      .then((dados) => {
        if (dados && Array.isArray(dados) && dados.length > 0) {
          const ordenados = [...dados].sort((a, b) => {
            const dateA = new Date(a.dataHora ?? a.dataInicio ?? 0).getTime();
            const dateB = new Date(b.dataHora ?? b.dataInicio ?? 0).getTime();
            return dateB - dateA;
          });
          setEventos(ordenados);
        }
      })
      .catch((err) => {
        console.warn('Backend de eventos indisponível, usando fallback estático:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const ehEventoFuturo = (dataString?: string) => {
    if (!dataString) return true;
    const dataEvento = new Date(dataString).getTime();
    const agora = new Date().getTime();
    return dataEvento >= agora - 24 * 60 * 60 * 1000;
  };

  return (
    <div className="min-h-screen bg-slate-50 pb-20">
      {/* Hero Banner de Eventos */}
      <section className="relative bg-gradient-to-r from-emerald-900 via-teal-900 to-slate-900 text-white py-16 px-4 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto space-y-4">
          <span className="inline-flex items-center gap-1.5 bg-emerald-500/20 text-emerald-300 border border-emerald-400/30 text-xs font-bold uppercase tracking-wider px-3.5 py-1.5 rounded-full">
            <Sparkles className="w-3.5 h-3.5" /> Agenda Comunitária
          </span>
          <h1 className="text-3xl sm:text-4xl font-extrabold tracking-tight">
            Eventos, Feiras e Ações do ISAC
          </h1>
          <p className="text-base sm:text-lg text-emerald-100/90 max-w-2xl leading-relaxed font-light">
            Participe das nossas feiras agroecológicas, oficinas sustentáveis e mutirões de preservação no litoral do Cabo de Santo Agostinho.
          </p>
        </div>
      </section>

      {/* Lista de Eventos */}
      <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 -mt-6">
        {loading && eventos.length === 0 ? (
          <div className="bg-white rounded-2xl p-12 shadow-sm border border-slate-200">
            <LoadingSpinner texto="Carregando eventos do ISAC..." />
          </div>
        ) : eventos.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {eventos.map((ev, index) => {
              const dataRef = ev.dataHora ?? ev.dataInicio ?? '';
              const proximo = ehEventoFuturo(dataRef);
              const imagemCapa =
                ev.fotoUrl || fallbackImagens[index % fallbackImagens.length];

              return (
                <article
                  key={ev.id}
                  className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-md transition duration-200 flex flex-col justify-between group"
                >
                  <div>
                    {/* Imagem de Capa com Badge de Status */}
                    <div className="relative aspect-[16/9] w-full overflow-hidden bg-slate-100">
                      <img
                        src={imagemCapa}
                        alt={ev.titulo}
                        className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                        loading="lazy"
                      />
                      <div className="absolute inset-0 bg-gradient-to-t from-slate-950/70 via-transparent to-transparent" />

                      {/* Badge de Status */}
                      <div className="absolute top-3 left-3">
                        {proximo ? (
                          <span className="inline-flex items-center gap-1.5 bg-emerald-600 text-white text-xs font-bold px-3 py-1 rounded-full shadow-md">
                            <span className="w-2 h-2 rounded-full bg-emerald-200 animate-pulse" />
                            Próximo Evento
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 bg-slate-800/90 text-slate-300 text-xs font-medium px-3 py-1 rounded-full backdrop-blur-sm shadow">
                            <CheckCircle2 className="w-3.5 h-3.5 text-slate-400" />
                            Realizado
                          </span>
                        )}
                      </div>

                      {/* Data em destaque no canto inferior da imagem */}
                      <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between text-white text-xs font-semibold">
                        <span className="inline-flex items-center gap-1.5 bg-slate-900/80 px-2.5 py-1 rounded-lg backdrop-blur-sm">
                          <Calendar className="w-3.5 h-3.5 text-emerald-400" />
                          {dataRef
                            ? new Date(dataRef).toLocaleDateString('pt-BR', {
                                weekday: 'short',
                                day: 'numeric',
                                month: 'long',
                                year: 'numeric',
                              })
                            : 'Data a definir'}
                        </span>

                        {ev.local && (
                          <span className="hidden sm:inline-flex items-center gap-1 bg-slate-900/80 px-2.5 py-1 rounded-lg backdrop-blur-sm truncate max-w-[200px]">
                            <MapPin className="w-3.5 h-3.5 text-emerald-400 flex-shrink-0" />
                            <span className="truncate">{ev.local}</span>
                          </span>
                        )}
                      </div>
                    </div>

                    {/* Conteúdo textual */}
                    <div className="p-6 space-y-4">
                      <h2 className="text-xl font-bold text-slate-900 group-hover:text-emerald-700 transition leading-snug">
                        {ev.titulo}
                      </h2>

                      {ev.descricao && (
                        <div className="text-slate-600 text-sm leading-relaxed">
                          <TextoFormatado texto={ev.descricao} />
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Detalhes e Ações de rodapé do card */}
                  <div className="px-6 pb-6 pt-2 border-t border-slate-100 flex flex-col gap-3">
                    <div className="flex flex-col gap-1.5 text-xs text-slate-500">
                      <div className="flex items-start gap-1.5">
                        <MapPin className="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5" />
                        <span><strong>Local:</strong> {ev.local}</span>
                      </div>
                      {ev.dataFim && (
                        <div className="flex items-center gap-1.5">
                          <Clock className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                          <span><strong>Encerramento:</strong> {new Date(ev.dataFim).toLocaleDateString('pt-BR')}</span>
                        </div>
                      )}
                    </div>

                    <div className="pt-2 flex items-center justify-between gap-3">
                      <a
                        href={`https://wa.me/5581999990001?text=${encodeURIComponent(`Olá! Gostaria de mais informações sobre o evento: ${ev.titulo}`)}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center gap-1.5 text-xs font-bold text-emerald-700 hover:text-emerald-800 bg-emerald-50 hover:bg-emerald-100 px-3.5 py-2 rounded-lg transition"
                      >
                        <MessageCircle className="w-3.5 h-3.5" /> Dúvidas no WhatsApp
                      </a>

                      <button
                        onClick={() => {
                          if (navigator.share) {
                            navigator.share({
                              title: ev.titulo,
                              text: `Confira este evento do ISAC: ${ev.titulo}`,
                              url: window.location.href,
                            }).catch(() => {});
                          } else {
                            navigator.clipboard.writeText(`${ev.titulo} - ${window.location.href}`);
                            alert('Link copiado para a área de transferência!');
                          }
                        }}
                        className="inline-flex items-center gap-1 text-xs text-slate-500 hover:text-slate-800 p-2 rounded-lg hover:bg-slate-100 transition"
                        title="Compartilhar evento"
                      >
                        <Share2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        ) : (
          <div className="text-center py-16 bg-white rounded-2xl border border-slate-200 p-8 space-y-3">
            <Calendar className="w-12 h-12 text-slate-300 mx-auto" />
            <h3 className="text-lg font-bold text-slate-800">Nenhum evento agendado no momento</h3>
            <p className="text-sm text-slate-500 max-w-md mx-auto">
              Fique atento aos nossos canais comunitários e redes sociais para as próximas datas de feiras e mutirões.
            </p>
          </div>
        )}
      </main>
    </div>
  );
};

export default Eventos;
