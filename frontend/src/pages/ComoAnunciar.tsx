import React from 'react';
import { Link } from 'react-router-dom';
import { MessageCircle, CheckCircle2, Star, Store, ArrowRight, ShieldCheck } from 'lucide-react';

export const ComoAnunciar: React.FC = () => {
  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-12">
      {/* Cabeçalho */}
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="text-xs font-bold text-emerald-700 bg-emerald-50 px-3.5 py-1.5 rounded-full uppercase tracking-wider">
          Divulgue seu trabalho
        </span>
        <h1 className="text-3xl sm:text-4xl font-extrabold text-slate-900 leading-tight">
          Como Anunciar no ISAC Marketplace
        </h1>
        <p className="text-slate-600 text-base leading-relaxed">
          Nossa vitrine foi criada para fortalecer os produtores rurais, artesãos e comerciantes locais de Enseada dos Corais e do Cabo de Santo Agostinho.
        </p>
      </div>

      {/* Dois canais: WhatsApp e Site */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Pelo WhatsApp */}
        <div className="bg-white rounded-2xl border-2 border-emerald-500/20 p-8 shadow-sm space-y-6 relative overflow-hidden">
          <div className="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
            <MessageCircle className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-xl font-bold text-slate-900">Pelo WhatsApp (Ultra-rápido)</h2>
            <p className="text-sm text-slate-500 mt-1">Ideal para quem quer publicar direto pelo celular em menos de 2 minutos.</p>
          </div>

          <ol className="space-y-3 text-sm text-slate-600">
            <li className="flex items-start gap-2">
              <span className="font-bold text-emerald-600">1.</span>
              Envie <strong>/anunciar</strong> para o nosso robô no WhatsApp.
            </li>
            <li className="flex items-start gap-2">
              <span className="font-bold text-emerald-600">2.</span>
              Responda às perguntas: Título, Categoria, Preço, Bairro e Foto.
            </li>
            <li className="flex items-start gap-2">
              <span className="font-bold text-emerald-600">3.</span>
              Confirme o resumo com <strong>sim</strong> e seu anúncio é publicado imediatamente!
            </li>
          </ol>

          <a
            href="https://wa.me/5581999990001?text=%2Fanunciar"
            target="_blank"
            rel="noopener noreferrer"
            className="w-full inline-flex items-center justify-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-3 px-4 rounded-xl shadow transition"
          >
            <MessageCircle className="w-4 h-4" />
            Anunciar pelo WhatsApp
          </a>
        </div>

        {/* Pelo Site */}
        <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-sm space-y-6">
          <div className="w-12 h-12 rounded-xl bg-sky-100 text-sky-700 flex items-center justify-center">
            <Store className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-xl font-bold text-slate-900">Pelo Site com Painel de Gestão</h2>
            <p className="text-sm text-slate-500 mt-1">Para quem quer gerenciar fotos, catálogo e status em tempo real.</p>
          </div>

          <ol className="space-y-3 text-sm text-slate-600">
            <li className="flex items-start gap-2">
              <span className="font-bold text-sky-600">1.</span>
              Faça seu cadastro informando seus dados básicos (Nome, Bairro, WhatsApp).
            </li>
            <li className="flex items-start gap-2">
              <span className="font-bold text-sky-600">2.</span>
              Acesse a área <strong>Minha Loja</strong> e clique em <strong>Novo Anúncio</strong>.
            </li>
            <li className="flex items-start gap-2">
              <span className="font-bold text-sky-600">3.</span>
              Acompanhe suas visualizações e renove a cada 15 dias com um clique.
            </li>
          </ol>

          <Link
            to="/cadastro-loja"
            className="w-full inline-flex items-center justify-center gap-2 bg-slate-900 hover:bg-slate-800 text-white font-semibold py-3 px-4 rounded-xl shadow transition"
          >
            Cadastrar Minha Loja no Site
          </Link>
        </div>
      </div>

      {/* Planos e Cotas */}
      <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-sm space-y-8">
        <div className="text-center space-y-2 max-w-xl mx-auto">
          <h2 className="text-2xl font-bold text-slate-900">Planos Transparentes</h2>
          <p className="text-sm text-slate-500">
            Comece 100% grátis e apoie as ações socioambientais do ISAC quando seu negócio crescer.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-4xl mx-auto">
          {/* Gratuito */}
          <div className="border border-slate-200 rounded-xl p-6 space-y-4">
            <h3 className="text-lg font-bold text-slate-900">Plano Comunitário Gratuito</h3>
            <p className="text-3xl font-extrabold text-slate-900">R$ 0 <span className="text-xs text-slate-500 font-normal">/ para sempre</span></p>
            <ul className="space-y-2 text-sm text-slate-600">
              <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-600" /> Até 2 anúncios ativos simultâneos</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-600" /> Publicação no Site e no Bot de WhatsApp</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-600" /> Contato direto pelo link do WhatsApp</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-600" /> Renovação gratuita a cada 15 dias</li>
            </ul>
          </div>

          {/* Assinante */}
          <div className="border-2 border-amber-400 bg-amber-50/30 rounded-xl p-6 space-y-4 relative">
            <span className="absolute top-3 right-3 bg-amber-500 text-white text-[10px] font-bold px-2 py-0.5 rounded shadow">
              DESTAQUE
            </span>
            <h3 className="text-lg font-bold text-slate-900">Plano Assinante Apoiador</h3>
            <p className="text-3xl font-extrabold text-amber-800">Contribuição <span className="text-xs text-slate-500 font-normal">quinzenal</span></p>
            <ul className="space-y-2 text-sm text-slate-700">
              <li className="flex items-center gap-2"><Star className="w-4 h-4 text-amber-600 fill-amber-600" /> <strong>Anúncios ilimitados</strong> sem trava de cota</li>
              <li className="flex items-center gap-2"><Star className="w-4 h-4 text-amber-600 fill-amber-600" /> <strong>Prioridade e selo Destaque</strong> nas buscas</li>
              <li className="flex items-center gap-2"><Star className="w-4 h-4 text-amber-600 fill-amber-600" /> Indicação prioritária no WhatsApp bot</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-600" /> Apoia oficinas e projetos do ISAC</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ComoAnunciar;
