import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { MessageCircle, MapPin, Tag, Star, ArrowLeft, Store, Calendar, ShieldCheck } from 'lucide-react';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Anuncio } from '../types';
import { api } from '../services/api';

export const AnuncioDetalhes: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [anuncio, setAnuncio] = useState<Anuncio | null>(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar() {
      if (!id) return;
      try {
        const dados = await api.obterAnuncio(Number(id));
        setAnuncio(dados);
      } catch (err: any) {
        setErro(err.message || 'Erro ao carregar anúncio.');
      } finally {
        setLoading(false);
      }
    }
    carregar();
  }, [id]);

  if (loading) {
    return <LoadingSpinner texto="Carregando anúncio..." />;
  }

  if (erro || !anuncio) {
    return (
      <div className="max-w-2xl mx-auto py-16 px-4 text-center space-y-4">
        <h2 className="text-xl font-bold text-slate-800">Anúncio não encontrado</h2>
        <p className="text-slate-500 text-sm">{erro || 'Este item pode ter expirado ou foi removido.'}</p>
        <Link
          to="/vitrine"
          className="inline-flex items-center gap-2 bg-emerald-600 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-emerald-700"
        >
          <ArrowLeft className="w-4 h-4" /> Voltar à vitrine
        </Link>
      </div>
    );
  }

  const fotoFallback = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=700';

  const formatarPreco = (preco: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(preco);
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      <Link
        to="/vitrine"
        className="inline-flex items-center gap-1.5 text-sm font-medium text-slate-600 hover:text-emerald-700 transition"
      >
        <ArrowLeft className="w-4 h-4" /> Voltar para a vitrine
      </Link>

      <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm grid grid-cols-1 md:grid-cols-2 gap-8 p-6 md:p-8">
        {/* Imagem */}
        <div className="relative aspect-square rounded-xl overflow-hidden bg-slate-100 border border-slate-100">
          <img
            src={anuncio.fotoUrl || fotoFallback}
            alt={anuncio.titulo}
            className="w-full h-full object-cover"
            onError={(e) => {
              (e.target as HTMLImageElement).src = fotoFallback;
            }}
          />
          {anuncio.destaque && (
            <span className="absolute top-3 left-3 bg-amber-500 text-white text-xs font-bold px-3 py-1 rounded-full shadow flex items-center gap-1">
              <Star className="w-3.5 h-3.5 fill-white" /> Destaque da Comunidade
            </span>
          )}
        </div>

        {/* Detalhes */}
        <div className="flex flex-col justify-between space-y-6">
          <div className="space-y-4">
            <div className="flex flex-wrap items-center gap-2 text-xs">
              {anuncio.nomeCategoria && (
                <span className="bg-slate-100 text-slate-700 px-2.5 py-1 rounded-full flex items-center gap-1">
                  <Tag className="w-3 h-3" /> {anuncio.nomeCategoria}
                </span>
              )}
              <span className="bg-slate-100 text-slate-700 px-2.5 py-1 rounded-full flex items-center gap-1">
                <MapPin className="w-3 h-3 text-slate-400" /> {anuncio.bairro}
              </span>
              <span className="text-slate-400 flex items-center gap-1">
                <Calendar className="w-3 h-3" /> Publicado em {new Date(anuncio.dataPublicacao).toLocaleDateString('pt-BR')}
              </span>
            </div>

            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 leading-tight">
              {anuncio.titulo}
            </h1>

            <p className="text-3xl font-extrabold text-emerald-700">
              {formatarPreco(anuncio.preco)}
            </p>

            {anuncio.descricao && (
              <div className="border-t border-slate-100 pt-4">
                <h3 className="text-sm font-bold text-slate-800 mb-1">Descrição</h3>
                <p className="text-sm text-slate-600 leading-relaxed whitespace-pre-line">
                  {anuncio.descricao}
                </p>
              </div>
            )}

            {/* Vendedor */}
            <div className="border-t border-slate-100 pt-4 bg-slate-50 p-4 rounded-xl space-y-1">
              <div className="flex items-center gap-2 text-sm font-semibold text-slate-800">
                <Store className="w-4 h-4 text-emerald-600" />
                Vendido por: {anuncio.nomeLoja}
              </div>
              <p className="text-xs text-slate-500">
                Localização: {anuncio.bairro} • Negociação direta
              </p>
            </div>
          </div>

          {/* Botão de contato direto */}
          <div className="space-y-3 pt-4">
            <a
              href={anuncio.linkWaMe}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full inline-flex items-center justify-center gap-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-3.5 px-6 rounded-xl shadow transition active:scale-[0.98] text-base"
            >
              <MessageCircle className="w-5 h-5" />
              Falar com o Vendedor no WhatsApp
            </a>
            <p className="text-center text-xs text-slate-400 flex items-center justify-center gap-1">
              <ShieldCheck className="w-4 h-4 text-emerald-600" />
              Apoie o produtor local de Enseada dos Corais
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnuncioDetalhes;
