import React from 'react';
import { Link } from 'react-router-dom';
import { MessageCircle, MapPin, Tag, Star } from 'lucide-react';
import { Anuncio } from '../types';

interface CardAnuncioProps {
  anuncio: Anuncio;
}

export const CardAnuncio: React.FC<CardAnuncioProps> = ({ anuncio }) => {
  const formatarPreco = (preco: number | null | undefined) => {
    if (preco == null) return 'Preço a consultar';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(preco);
  };

  const fotoFallback = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500';

  // Suporta fotoUrl (legado) ou fotos[] (novo)
  const imagemPrincipal =
    anuncio.fotos?.[0] ?? anuncio.fotoUrl ?? fotoFallback;

  // Suporta linkWaMe (legado) ou gera a partir do telefone
  const whatsappHref =
    anuncio.linkWaMe ??
    `https://wa.me/${anuncio.telefoneWhatsapp ?? anuncio.telefoneLoja ?? ''}?text=${encodeURIComponent(`Olá! Tenho interesse no anúncio: ${anuncio.titulo}`)}`;

  // nomeCategoria (legado) ou categoria (string)
  const nomeCategoria = anuncio.nomeCategoria ?? anuncio.categoria;

  return (
    <article
      className={`bg-white rounded-xl shadow-sm hover:shadow-md transition duration-200 overflow-hidden border flex flex-col justify-between ${
        anuncio.destaque ? 'border-amber-400 ring-1 ring-amber-400' : 'border-slate-200'
      }`}
    >
      <div>
        {/* Imagem com Badge */}
        <div className="relative aspect-[4/3] bg-slate-100 overflow-hidden">
          <img
            src={imagemPrincipal}
            alt={anuncio.titulo}
            className="w-full h-full object-cover group-hover:scale-105 transition duration-300"
            loading="lazy"
            onError={(e) => {
              (e.target as HTMLImageElement).src = fotoFallback;
            }}
          />
          {anuncio.destaque && (
            <span className="absolute top-2 left-2 bg-amber-500 text-white text-xs font-bold px-2 py-1 rounded shadow flex items-center gap-1">
              <Star className="w-3 h-3 fill-white" /> Destaque
            </span>
          )}
          {anuncio.tipo === 'SERVICO' && (
            <span className="absolute top-2 right-2 bg-isac-blue-dark text-white text-xs font-semibold px-2 py-1 rounded shadow">
              Serviço
            </span>
          )}
        </div>

        {/* Informações */}
        <div className="p-4 space-y-2">
          <div className="flex items-center gap-2 text-xs text-slate-500">
            {nomeCategoria && (
              <span className="flex items-center gap-1 bg-slate-100 px-2 py-0.5 rounded text-slate-700">
                <Tag className="w-3 h-3" /> {nomeCategoria}
              </span>
            )}
            <span className="flex items-center gap-1">
              <MapPin className="w-3 h-3 text-slate-400" /> {anuncio.bairro}
            </span>
          </div>

          <Link to={`/anuncios/${anuncio.id}`}>
            <h3 className="font-semibold text-slate-900 text-base line-clamp-2 hover:text-isac-green transition leading-snug">
              {anuncio.titulo}
            </h3>
          </Link>

          <p className="text-xl font-bold text-isac-green-dark">
            {anuncio.preco != null ? formatarPreco(anuncio.preco) : 'Preço a consultar'}
          </p>

          <p className="text-xs text-slate-500 truncate">
            Vendido por: <strong className="text-slate-700 font-medium">{anuncio.nomeLoja}</strong>
          </p>
        </div>
      </div>

      {/* Ação WhatsApp */}
      <div className="p-4 pt-0">
        <a
          href={whatsappHref}
          target="_blank"
          rel="noopener noreferrer"
          className="w-full inline-flex items-center justify-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-medium py-2.5 px-4 rounded-lg shadow-sm transition active:scale-[0.98] text-sm"
          aria-label={`Falar no WhatsApp com o vendedor de ${anuncio.titulo}`}
        >
          <MessageCircle className="w-4 h-4" />
          Falar com Vendedor
        </a>
      </div>
    </article>
  );
};
