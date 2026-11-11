import React from 'react';
import { Link } from 'react-router-dom';
import { MessageCircle, CheckCircle2, Star, Store, ShieldCheck, Sparkles, Smartphone, BarChart3, HelpCircle } from 'lucide-react';

export const ComoAnunciar: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-50 pb-20">
      {/* Hero Banner com Imagem Ilustrativa de Negócios e Comunidade */}
      <section className="relative bg-slate-900 text-white overflow-hidden py-16 sm:py-24">
        <div className="absolute inset-0 z-0">
          <img
            src="https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1600&q=80"
            alt="Empreendedores locais e feirantes"
            className="w-full h-full object-cover opacity-30"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/70 to-transparent" />
        </div>

        <div className="relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 text-center space-y-4">
          <span className="inline-flex items-center gap-1.5 bg-emerald-500/20 text-emerald-300 border border-emerald-400/30 text-xs font-bold uppercase tracking-wider px-3.5 py-1.5 rounded-full backdrop-blur-sm">
            <Sparkles className="w-3.5 h-3.5" /> Vitrine Colaborativa do Litoral Sul
          </span>
          <h1 className="text-3xl sm:text-5xl font-extrabold tracking-tight text-white leading-tight">
            Como Anunciar no ISAC Marketplace
          </h1>
          <p className="text-base sm:text-lg text-slate-200 max-w-2xl mx-auto font-light leading-relaxed">
            Dê visibilidade ao seu negócio, alimentos agroecológicos ou artesanato no Cabo de Santo Agostinho. Divulgue direto pelo WhatsApp ou gerencie seu catálogo online.
          </p>
        </div>
      </section>

      {/* Conteúdo Principal */}
      <main className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 -mt-8 relative z-20 space-y-12">
        {/* Dois canais: WhatsApp e Site */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Pelo WhatsApp */}
          <div className="bg-white rounded-2xl border-2 border-emerald-500/30 p-8 shadow-sm space-y-6 relative overflow-hidden flex flex-col justify-between">
            <div className="space-y-4">
              <div className="w-14 h-14 rounded-2xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
                <Smartphone className="w-7 h-7" />
              </div>
              <div>
                <span className="text-[11px] font-bold uppercase tracking-wider text-emerald-600 bg-emerald-50 px-2.5 py-0.5 rounded-full">
                  Sem burocracia
                </span>
                <h2 className="text-2xl font-bold text-slate-900 mt-1">Pelo WhatsApp (Ultra-rápido)</h2>
                <p className="text-sm text-slate-500 mt-1">
                  Ideal para publicar direto do seu celular em menos de 2 minutos.
                </p>
              </div>

              <ol className="space-y-3 text-sm text-slate-600 pt-2">
                <li className="flex items-start gap-2.5">
                  <span className="flex-shrink-0 w-6 h-6 rounded-full bg-emerald-100 text-emerald-700 font-bold flex items-center justify-center text-xs">
                    1
                  </span>
                  <span>Envie a mensagem <strong>/anunciar</strong> para o nosso robô oficial.</span>
                </li>
                <li className="flex items-start gap-2.5">
                  <span className="flex-shrink-0 w-6 h-6 rounded-full bg-emerald-100 text-emerald-700 font-bold flex items-center justify-center text-xs">
                    2
                  </span>
                  <span>Informe nome do produto, categoria, preço, bairro e envie uma foto.</span>
                </li>
                <li className="flex items-start gap-2.5">
                  <span className="flex-shrink-0 w-6 h-6 rounded-full bg-emerald-100 text-emerald-700 font-bold flex items-center justify-center text-xs">
                    3
                  </span>
                  <span>Responda <strong>sim</strong> para confirmar e seu anúncio estará ativo!</span>
                </li>
              </ol>
            </div>

            <a
              href="https://wa.me/5581999990001?text=Ol%C3%A1!%20Gostaria%20de%20cadastrar%20o%20meu%20neg%C3%B3cio%20no%20ISAC%20Marketplace."
              target="_blank"
              rel="noopener noreferrer"
              className="w-full inline-flex items-center justify-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-3.5 px-4 rounded-xl shadow-md transition active:scale-95"
            >
              <MessageCircle className="w-5 h-5" />
              Anunciar pelo WhatsApp
            </a>
          </div>

          {/* Pelo Site */}
          <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-sm space-y-6 flex flex-col justify-between">
            <div className="space-y-4">
              <div className="w-14 h-14 rounded-2xl bg-sky-100 text-sky-700 flex items-center justify-center">
                <Store className="w-7 h-7" />
              </div>
              <div>
                <span className="text-[11px] font-bold uppercase tracking-wider text-sky-600 bg-sky-50 px-2.5 py-0.5 rounded-full">
                  Gestão completa
                </span>
                <h2 className="text-2xl font-bold text-slate-900 mt-1">Pelo Painel Web</h2>
                <p className="text-sm text-slate-500 mt-1">
                  Para quem quer administrar fotos, dados da loja e múltiplos itens simultâneos.
                </p>
              </div>

              <ol className="space-y-3 text-sm text-slate-600 pt-2">
                <li className="flex items-start gap-2.5">
                  <span className="flex-shrink-0 w-6 h-6 rounded-full bg-sky-100 text-sky-700 font-bold flex items-center justify-center text-xs">
                    1
                  </span>
                  <span>Cadastre sua loja informando Nome Fantasia, Bairro e WhatsApp.</span>
                </li>
                <li className="flex items-start gap-2.5">
                  <span className="flex-shrink-0 w-6 h-6 rounded-full bg-sky-100 text-sky-700 font-bold flex items-center justify-center text-xs">
                    2
                  </span>
                  <span>Acesse a área <strong>Minha Loja</strong> e gerencie seus anúncios com facilidade.</span>
                </li>
                <li className="flex items-start gap-2.5">
                  <span className="flex-shrink-0 w-6 h-6 rounded-full bg-sky-100 text-sky-700 font-bold flex items-center justify-center text-xs">
                    3
                  </span>
                  <span>Pause, reative ou renove anúncios a qualquer momento com um clique.</span>
                </li>
              </ol>
            </div>

            <Link
              to="/cadastro-loja"
              className="w-full inline-flex items-center justify-center gap-2 bg-slate-900 hover:bg-slate-800 text-white font-bold py-3.5 px-4 rounded-xl shadow-md transition active:scale-95"
            >
              <Store className="w-5 h-5" />
              Cadastrar Minha Loja no Site
            </Link>
          </div>
        </div>

        {/* Planos e Cotas */}
        <section className="bg-white rounded-2xl border border-slate-200 p-8 sm:p-12 shadow-sm space-y-8">
          <div className="text-center space-y-2 max-w-xl mx-auto">
            <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900">Planos Transparentes</h2>
            <p className="text-sm text-slate-500">
              Comece 100% grátis e fortaleça os projetos socioambientais do ISAC quando seu negócio crescer.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-4xl mx-auto">
            {/* Gratuito */}
            <div className="border border-slate-200 rounded-2xl p-6 sm:p-8 space-y-5 bg-slate-50/50">
              <div className="space-y-1">
                <h3 className="text-lg font-bold text-slate-900">Plano Comunitário Gratuito</h3>
                <p className="text-xs text-slate-500">Para pequenos produtores e artesãos locais</p>
              </div>
              <p className="text-3xl font-extrabold text-slate-900">
                R$ 0 <span className="text-xs text-slate-500 font-normal">/ para sempre</span>
              </p>
              <ul className="space-y-2.5 text-sm text-slate-600">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                  Até 2 anúncios ativos simultâneos
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                  Divulgação no site e bot do WhatsApp
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                  Link direto para seu WhatsApp de vendas
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                  Renovação gratuita a cada 15 dias
                </li>
              </ul>
            </div>

            {/* Assinante */}
            <div className="border-2 border-amber-400 bg-amber-50/40 rounded-2xl p-6 sm:p-8 space-y-5 relative">
              <span className="absolute top-4 right-4 bg-amber-500 text-white text-[10px] font-extrabold px-2.5 py-1 rounded-full shadow">
                DESTAQUE
              </span>
              <div className="space-y-1">
                <h3 className="text-lg font-bold text-slate-900">Plano Assinante Apoiador</h3>
                <p className="text-xs text-slate-500">Para comércio consolidado e maior alcance</p>
              </div>
              <p className="text-3xl font-extrabold text-amber-900">
                Contribuição <span className="text-xs text-slate-600 font-normal">quinzenal simbólica</span>
              </p>
              <ul className="space-y-2.5 text-sm text-slate-700">
                <li className="flex items-center gap-2">
                  <Star className="w-4 h-4 text-amber-600 fill-amber-600 flex-shrink-0" />
                  <strong>Anúncios ilimitados</strong> no catálogo
                </li>
                <li className="flex items-center gap-2">
                  <Star className="w-4 h-4 text-amber-600 fill-amber-600 flex-shrink-0" />
                  <strong>Selo Destaque visual</strong> nas buscas
                </li>
                <li className="flex items-center gap-2">
                  <Star className="w-4 h-4 text-amber-600 fill-amber-600 flex-shrink-0" />
                  Recomendação prioritária no WhatsApp bot
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                  Apoia projetos ecológicos e sociais do ISAC
                </li>
              </ul>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
};

export default ComoAnunciar;
