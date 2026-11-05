import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Search, ShoppingBag, Calendar, ArrowRight, ShieldCheck, MessageCircle, Sprout, Store } from 'lucide-react';
import { CardAnuncio } from '../components/CardAnuncio';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Anuncio, Evento } from '../types';
import { api } from '../services/api';

export const Home: React.FC = () => {
  const [destaques, setDestaques] = useState<Anuncio[]>([]);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);
  const [termoBusca, setTermoBusca] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    async function carregarDados() {
      try {
        const [buscaRes, eventosRes] = await Promise.all([
          api.buscar('', '', '', 0),
          api.listarEventos(),
        ]);
        setDestaques(buscaRes.anuncios.slice(0, 4));
        setEventos(eventosRes.slice(0, 3));
      } catch (err) {
        console.error('Erro ao carregar dados da Home:', err);
      } finally {
        setLoading(false);
      }
    }
    carregarDados();
  }, []);

  const handleBuscar = (e: React.FormEvent) => {
    e.preventDefault();
    if (termoBusca.trim()) {
      navigate(`/vitrine?termo=${encodeURIComponent(termoBusca.trim())}`);
    } else {
      navigate('/vitrine');
    }
  };

  return (
    <div className="space-y-16">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-b from-emerald-900 via-emerald-800 to-teal-900 text-white py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-5xl mx-auto text-center space-y-6">
          <div className="inline-flex items-center gap-2 bg-emerald-700/60 backdrop-blur-sm border border-emerald-500/30 text-emerald-200 text-xs font-semibold px-4 py-1.5 rounded-full shadow-sm">
            <Sprout className="w-4 h-4 text-emerald-300" />
            Instituto Socioambiental do Cabo — Enseada dos Corais
          </div>

          <h1 className="text-3xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight leading-tight">
            Compre de quem produz na <span className="text-emerald-400">nossa comunidade</span>
          </h1>

          <p className="max-w-2xl mx-auto text-base sm:text-lg text-emerald-100 font-normal leading-relaxed">
            Conectamos moradores, pequenos produtores, artesãos e prestadores de serviços de Enseada dos Corais, Gaibu e região.
          </p>

          {/* Barra de Busca rápida */}
          <form onSubmit={handleBuscar} className="max-w-2xl mx-auto flex flex-col sm:flex-row gap-2 pt-4">
            <div className="relative flex-1">
              <Search className="w-5 h-5 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                value={termoBusca}
                onChange={(e) => setTermoBusca(e.target.value)}
                placeholder="O que você está procurando hoje? (ex: tapioca, mel, eletricista)"
                className="w-full pl-11 pr-4 py-3.5 rounded-xl bg-white text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-400 shadow-md text-sm"
              />
            </div>
            <button
              type="submit"
              className="bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold px-6 py-3.5 rounded-xl shadow-md transition active:scale-95 text-sm"
            >
              Buscar Agora
            </button>
          </form>

          {/* Badges de destaque */}
          <div className="pt-6 flex flex-wrap justify-center gap-6 text-xs text-emerald-200">
            <span className="flex items-center gap-1.5">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              Comércio Comunitário Seguro
            </span>
            <span className="flex items-center gap-1.5">
              <MessageCircle className="w-4 h-4 text-emerald-400" />
              Contato direto no WhatsApp
            </span>
            <span className="flex items-center gap-1.5">
              <Store className="w-4 h-4 text-emerald-400" />
              Grátis para produtores locais
            </span>
          </div>
        </div>
      </section>

      {/* Destaques da Vitrine */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end mb-8 gap-4">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Destaques da Vitrine</h2>
            <p className="text-sm text-slate-500 mt-1">Produtos frescos e serviços oferecidos por vizinhos</p>
          </div>
          <Link
            to="/vitrine"
            className="inline-flex items-center gap-1 text-sm font-semibold text-emerald-700 hover:text-emerald-800 transition"
          >
            Ver toda a vitrine <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {loading ? (
          <LoadingSpinner />
        ) : destaques.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {destaques.map((anuncio) => (
              <CardAnuncio key={anuncio.id} anuncio={anuncio} />
            ))}
          </div>
        ) : (
          <div className="text-center py-12 bg-white rounded-xl border border-slate-200 p-8">
            <ShoppingBag className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <p className="text-slate-600 font-medium">Nenhum anúncio disponível no momento.</p>
          </div>
        )}
      </section>

      {/* Como Funciona o WhatsApp Bot */}
      <section className="bg-emerald-50 border-y border-emerald-100 py-16 px-4 sm:px-6 lg:px-8">
        <div className="max-w-5xl mx-auto">
          <div className="text-center space-y-2 mb-12">
            <h2 className="text-2xl sm:text-3xl font-bold text-slate-900">
              Praticidade total pelo WhatsApp
            </h2>
            <p className="text-sm sm:text-base text-slate-600 max-w-xl mx-auto">
              Sem precisar baixar nenhum aplicativo novo. Use o WhatsApp para encontrar ou divulgar o seu trabalho.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-2xl shadow-sm border border-emerald-100 space-y-3">
              <div className="w-10 h-10 rounded-lg bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold">1</div>
              <h3 className="font-bold text-slate-900">Envie uma mensagem</h3>
              <p className="text-sm text-slate-600">
                Mande <strong>/anunciar</strong> para cadastrar um produto ou <strong>/buscar</strong> para achar o que precisa em segundos.
              </p>
            </div>

            <div className="bg-white p-6 rounded-2xl shadow-sm border border-emerald-100 space-y-3">
              <div className="w-10 h-10 rounded-lg bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold">2</div>
              <h3 className="font-bold text-slate-900">Responda ao robô</h3>
              <p className="text-sm text-slate-600">
                Nosso assistente pergunta título, preço, bairro e foto. Em menos de 2 minutos seu item está no ar.
              </p>
            </div>

            <div className="bg-white p-6 rounded-2xl shadow-sm border border-emerald-100 space-y-3">
              <div className="w-10 h-10 rounded-lg bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold">3</div>
              <h3 className="font-bold text-slate-900">Fechamento Direto</h3>
              <p className="text-sm text-slate-600">
                Os clientes clicam no link e conversam diretamente com você no WhatsApp, sem intermediários.
              </p>
            </div>
          </div>

          <div className="text-center mt-10">
            <a
              href="https://wa.me/5581999990001?text=Ol%C3%A1%2C%20gostaria%20de%20anunciar%20no%20ISAC%20Marketplace"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold px-6 py-3 rounded-xl shadow transition"
            >
              <MessageCircle className="w-5 h-5" />
              Experimentar Robô no WhatsApp
            </a>
          </div>
        </div>
      </section>

      {/* Próximos Eventos */}
      {eventos.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-end mb-8">
            <div>
              <h2 className="text-2xl font-bold text-slate-900">Agenda Comunitária ISAC</h2>
              <p className="text-sm text-slate-500 mt-1">Feiras, oficinas e mutirões de preservação ambiental</p>
            </div>
            <Link to="/eventos" className="text-sm font-semibold text-emerald-700 hover:text-emerald-800 transition flex items-center gap-1">
              Ver todos <ArrowRight className="w-4 h-4" />
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {eventos.map((evento) => (
              <div key={evento.id} className="bg-white border border-slate-200 rounded-xl p-6 shadow-sm hover:shadow transition">
                <div className="flex items-center gap-2 text-xs font-semibold text-emerald-700 mb-2">
                  <Calendar className="w-4 h-4" />
                  {new Date(evento.dataInicio).toLocaleDateString('pt-BR')}
                </div>
                <h3 className="font-bold text-slate-900 text-base mb-2">{evento.titulo}</h3>
                <p className="text-sm text-slate-600 line-clamp-3 mb-4">{evento.descricao}</p>
                <p className="text-xs text-slate-500">📍 {evento.local}</p>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
};

export default Home;
