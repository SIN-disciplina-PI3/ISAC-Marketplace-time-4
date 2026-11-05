import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Store, ShoppingBag, PlusCircle, CheckCircle2, AlertTriangle, ArrowRight, Star } from 'lucide-react';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { Loja, AssinaturaInfo } from '../../types';
import { api } from '../../services/api';

export const DashboardLoja: React.FC = () => {
  const [loja, setLoja] = useState<Loja | null>(null);
  const [assinatura, setAssinatura] = useState<AssinaturaInfo | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function carregar() {
      try {
        const [lojaRes, assRes] = await Promise.all([
          api.obterMinhaLoja(),
          api.obterMinhaAssinatura(),
        ]);
        setLoja(lojaRes);
        setAssinatura(assRes);
      } catch (err) {
        console.error('Erro ao carregar dados da loja:', err);
      } finally {
        setLoading(false);
      }
    }
    carregar();
  }, []);

  if (loading) {
    return <LoadingSpinner texto="Carregando painel da loja..." />;
  }

  const atingiuLimite = assinatura ? (!assinatura.ilimitado && assinatura.cotaUsada >= assinatura.cotaLimite) : false;

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Boas-vindas */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 sm:p-8 shadow-sm flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
        <div className="space-y-1">
          <div className="flex items-center gap-2 text-xs font-semibold text-emerald-700 uppercase tracking-wider">
            <Store className="w-4 h-4" /> Painel do Vendedor
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900">
            {loja?.nomeFantasia}
          </h1>
          <p className="text-sm text-slate-500">
            Bairro: <strong>{loja?.bairro}</strong> • WhatsApp: <strong>{loja?.telefoneWhatsapp}</strong>
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link
            to="/loja/anuncios"
            className="inline-flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold px-4 py-2.5 rounded-xl shadow text-sm transition"
          >
            <PlusCircle className="w-4 h-4" /> Gerenciar Anúncios
          </Link>
          <Link
            to="/loja/dados"
            className="inline-flex items-center gap-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold px-4 py-2.5 rounded-xl text-sm transition"
          >
            Editar Loja
          </Link>
        </div>
      </div>

      {/* Cards de Status do Plano & Cota */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Card Plano */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-3">
          <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Plano Atual</span>
          <div className="flex items-center gap-2">
            <h2 className="text-2xl font-extrabold text-slate-900">{assinatura?.plano}</h2>
            {assinatura?.ilimitado && (
              <span className="bg-amber-500 text-white text-xs font-bold px-2 py-0.5 rounded shadow flex items-center gap-1">
                <Star className="w-3 h-3 fill-white" /> Destaque
              </span>
            )}
          </div>
          <p className="text-xs text-slate-500">
            Status: <strong className="uppercase">{assinatura?.statusAssinatura}</strong>
            {assinatura?.dataVencimento && (
              <span> • Vencimento: {new Date(assinatura.dataVencimento).toLocaleDateString('pt-BR')}</span>
            )}
          </p>
        </div>

        {/* Card Cota Usada */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-3">
          <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Cota de Anúncios Ativos</span>
          <div className="text-2xl font-extrabold text-slate-900">
            {assinatura?.cotaUsada} <span className="text-slate-400 text-base font-normal">/ {assinatura?.ilimitado ? 'Ilimitado' : assinatura?.cotaLimite}</span>
          </div>
          <p className="text-xs text-slate-500">
            {atingiuLimite ? (
              <span className="text-rose-600 font-medium flex items-center gap-1">
                <AlertTriangle className="w-3.5 h-3.5" /> Limite gratuito atingido (2 anúncios)
              </span>
            ) : (
              <span className="text-emerald-700 font-medium flex items-center gap-1">
                <CheckCircle2 className="w-3.5 h-3.5" /> Dentro da cota permitida
              </span>
            )}
          </p>
        </div>

        {/* Card Dica / Upgrade */}
        <div className="bg-gradient-to-br from-emerald-50 to-teal-50 border border-emerald-100 rounded-2xl p-6 shadow-sm space-y-3">
          <span className="text-xs font-bold text-emerald-800 uppercase tracking-wider">Potencialize suas Vendas</span>
          <h2 className="text-base font-bold text-slate-900">Plano Assinante ISAC</h2>
          <p className="text-xs text-slate-600 leading-relaxed">
            Tenha anúncios ilimitados e destaque garantido na vitrine e no robô do WhatsApp.
          </p>
          <a
            href="https://wa.me/5581999990001?text=Ol%C3%A1%2C%20gostaria%20de%20assinar%20o%20plano%20comunit%C3%A1rio%20do%20ISAC"
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-1 text-xs font-bold text-emerald-800 hover:text-emerald-900 pt-1"
          >
            Falar com a equipe ISAC <ArrowRight className="w-3.5 h-3.5" />
          </a>
        </div>
      </div>

      {/* Ações Rápidas */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
        <h2 className="text-lg font-bold text-slate-900">Ações Rápidas</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Link
            to="/loja/anuncios"
            className="p-4 rounded-xl border border-slate-200 hover:border-emerald-500 hover:bg-emerald-50/30 transition flex items-center justify-between"
          >
            <div className="flex items-center gap-3">
              <ShoppingBag className="w-6 h-6 text-emerald-600" />
              <div>
                <h3 className="font-bold text-sm text-slate-800">Meus Anúncios</h3>
                <p className="text-xs text-slate-500">Publicar, pausar, renovar ou editar preços</p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-slate-400" />
          </Link>

          <Link
            to="/loja/dados"
            className="p-4 rounded-xl border border-slate-200 hover:border-emerald-500 hover:bg-emerald-50/30 transition flex items-center justify-between"
          >
            <div className="flex items-center gap-3">
              <Store className="w-6 h-6 text-sky-600" />
              <div>
                <h3 className="font-bold text-sm text-slate-800">Dados da Loja</h3>
                <p className="text-xs text-slate-500">Atualizar telefone de WhatsApp, bairro e endereço</p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-slate-400" />
          </Link>
        </div>
      </div>
    </div>
  );
};

export default DashboardLoja;
