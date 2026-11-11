import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, BookOpen, Sprout, Leaf, Sun, Droplets, HeartHandshake } from 'lucide-react';
import { TextoFormatado } from '../components/TextoFormatado';
import { Sobre } from './Sobre';
import { Conteudo } from '../types';
import { api } from '../services/api';

const CONTEUDOS_FALLBACK: Record<string, { titulo: string; corpoMarkdown: string }> = {
  agricultura: {
    titulo: 'Agricultura Familiar e Meio Ambiente',
    corpoMarkdown:
      'O ISAC fomenta a agroecologia costeira e a soberania alimentar das comunidades do Cabo de Santo Agostinho.\n\nAtravés de hortas comunitárias, sistemas agroflorestais e feiras locais, conectamos pequenos agricultores e pescadores artesanais diretamente com moradores e visitantes.\n\nNossos projetos promovem o não uso de agrotóxicos, a preservação de nascentes, o reflorestamento de restingas e a valorização da produção sustentável regional.',
  },
  'como-anunciar': {
    titulo: 'Como Anunciar no ISAC Marketplace',
    corpoMarkdown:
      'Divulgue seus produtos e serviços na vitrine colaborativa do Cabo de Santo Agostinho!\n\n- **Plano Gratuito:** Cadastre até 2 anúncios ativos sem nenhum custo, gerencie pelo site ou WhatsApp.\n- **Plano Assinante:** Anúncios ilimitados, destaque visual nas buscas da vitrine e suporte prioritário por uma contribuição simbólica que apoia diretamente os projetos socioambientais do ISAC.\n- **Pelo WhatsApp:** Envie uma mensagem com /anunciar para nosso bot e publique em menos de 2 minutos!',
  },
};

export const Institucional: React.FC = () => {
  const { chave } = useParams<{ chave: string }>();
  const [conteudo, setConteudo] = useState<Conteudo | null>(null);

  // Se a chave for 'sobre', exibe o componente completo Sobre
  if (chave === 'sobre') {
    return <Sobre />;
  }

  useEffect(() => {
    if (!chave) return;
    const fallback = CONTEUDOS_FALLBACK[chave];

    api.obterConteudo(chave)
      .then((dados) => {
        if (dados && dados.titulo) {
          setConteudo(dados);
        } else if (fallback) {
          setConteudo({
            id: 0,
            chave,
            titulo: fallback.titulo,
            corpoMarkdown: fallback.corpoMarkdown,
            atualizadoEm: new Date().toISOString(),
          });
        }
      })
      .catch(() => {
        // Se a API falhar (ex: erro 500 ou offline), usa o fallback estático
        if (fallback) {
          setConteudo({
            id: 0,
            chave,
            titulo: fallback.titulo,
            corpoMarkdown: fallback.corpoMarkdown,
            atualizadoEm: new Date().toISOString(),
          });
        }
      });
  }, [chave]);

  const fallback = chave ? CONTEUDOS_FALLBACK[chave] : null;
  const dadosExibicao = conteudo || (fallback ? {
    id: 0,
    chave: chave || '',
    titulo: fallback.titulo,
    corpoMarkdown: fallback.corpoMarkdown,
    atualizadoEm: new Date().toISOString(),
  } : null);

  if (!dadosExibicao) {
    return (
      <div className="max-w-2xl mx-auto py-16 px-4 text-center space-y-4">
        <h2 className="text-xl font-bold text-slate-800">Conteúdo em atualização</h2>
        <p className="text-slate-500 text-sm">Este conteúdo institucional está sendo preparado pelo Instituto ISAC.</p>
        <Link
          to="/"
          className="inline-flex items-center gap-2 bg-emerald-600 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-emerald-700"
        >
          <ArrowLeft className="w-4 h-4" /> Voltar ao Início
        </Link>
      </div>
    );
  }

  const ehAgricultura = chave === 'agricultura';

  return (
    <div className="min-h-screen bg-slate-50 pb-20">
      {/* Banner Superior com Imagens Ilustrativas */}
      <section className="relative bg-slate-900 text-white overflow-hidden py-16 sm:py-20">
        <div className="absolute inset-0 z-0">
          <img
            src={
              ehAgricultura
                ? 'https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=1600&q=80'
                : 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?auto=format&fit=crop&w=1600&q=80'
            }
            alt={dadosExibicao.titulo}
            className="w-full h-full object-cover opacity-35"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/60 to-transparent" />
        </div>

        <div className="relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 space-y-4">
          <Link
            to="/"
            className="inline-flex items-center gap-2 text-sm text-emerald-300 hover:text-white transition"
          >
            <ArrowLeft className="w-4 h-4" /> Início
          </Link>

          <div className="space-y-2">
            <span className="inline-flex items-center gap-1.5 bg-emerald-500/20 text-emerald-300 border border-emerald-400/30 text-xs font-bold uppercase tracking-wider px-3.5 py-1 rounded-full">
              {ehAgricultura ? <Sprout className="w-3.5 h-3.5" /> : <BookOpen className="w-3.5 h-3.5" />}
              Institucional ISAC • Cabo de Santo Agostinho
            </span>
            <h1 className="text-3xl sm:text-5xl font-extrabold tracking-tight text-white">
              {dadosExibicao.titulo}
            </h1>
            <p className="text-base sm:text-lg text-slate-200 max-w-2xl font-light">
              {ehAgricultura
                ? 'Fortalecendo as comunidades rurais e litorâneas através de técnicas sustentáveis, agroecologia e respeito aos ciclos naturais.'
                : 'Conheça as ações, projetos e diretrizes do Instituto de Sustentabilidade e Ação Comunitária.'}
            </p>
          </div>
        </div>
      </section>

      {/* Conteúdo Principal com tratamento de quebras de linha em parágrafos */}
      <main className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 -mt-8 relative z-20 space-y-8">
        <article className="bg-white rounded-2xl border border-slate-200 p-6 sm:p-10 shadow-sm space-y-6">
          <div className="prose prose-slate max-w-none text-slate-700 leading-relaxed text-base sm:text-lg">
            <TextoFormatado texto={dadosExibicao.corpoMarkdown} />
          </div>

          <div className="border-t border-slate-100 pt-6 flex flex-wrap items-center justify-between text-xs text-slate-400 gap-2">
            <span>Última atualização: {new Date(dadosExibicao.atualizadoEm ?? '').toLocaleDateString('pt-BR')}</span>
            <span>Instituto ISAC • Enseada dos Corais</span>
          </div>
        </article>

        {/* Destaque Visual Específico para Agricultura Familiar */}
        {ehAgricultura && (
          <section className="space-y-6">
            <div className="text-center max-w-2xl mx-auto space-y-2">
              <h2 className="text-2xl font-bold text-slate-900">
                Como apoiamos o produtor local
              </h2>
              <p className="text-sm text-slate-500">
                Práticas agroecológicas que geram alimentos saudáveis e preservam a biodiversidade costeira.
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm space-y-2 text-center">
                <div className="w-12 h-12 bg-emerald-100 text-emerald-700 rounded-xl flex items-center justify-center mx-auto">
                  <Leaf className="w-6 h-6" />
                </div>
                <h3 className="font-bold text-slate-900 text-sm">Sem Agrotóxicos</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Incentivo ao manejo biológico e biofertilizantes produzidos com matéria orgânica regional.
                </p>
              </div>

              <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm space-y-2 text-center">
                <div className="w-12 h-12 bg-sky-100 text-sky-700 rounded-xl flex items-center justify-center mx-auto">
                  <Droplets className="w-6 h-6" />
                </div>
                <h3 className="font-bold text-slate-900 text-sm">Proteção das Águas</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Preservação de nascentes e sistemas de irrigação eficientes para evitar o desperdício hídrico.
                </p>
              </div>

              <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm space-y-2 text-center">
                <div className="w-12 h-12 bg-amber-100 text-amber-700 rounded-xl flex items-center justify-center mx-auto">
                  <Sun className="w-6 h-6" />
                </div>
                <h3 className="font-bold text-slate-900 text-sm">Comércio Justo</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Venda direta sem intermediários através do marketplace e das feiras comunitárias mensais.
                </p>
              </div>

              <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm space-y-2 text-center">
                <div className="w-12 h-12 bg-green-100 text-green-700 rounded-xl flex items-center justify-center mx-auto">
                  <HeartHandshake className="w-6 h-6" />
                </div>
                <h3 className="font-bold text-slate-900 text-sm">Comunidade Viva</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Capacitação continuada e troca solidária de mudas e sementes crioulas entre agricultores.
                </p>
              </div>
            </div>

            {/* Banner com foto ilustrativa de produtos do campo */}
            <div className="rounded-2xl overflow-hidden border border-slate-200 shadow-sm relative h-64 sm:h-80">
              <img
                src="https://images.unsplash.com/photo-1610348725531-843dff563e2c?auto=format&fit=crop&w=1200&q=80"
                alt="Cesta com alimentos frescos da agricultura familiar"
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-slate-950/30 to-transparent flex items-end p-6 sm:p-8">
                <div className="text-white space-y-2 max-w-2xl">
                  <span className="text-xs uppercase tracking-wider font-bold text-emerald-400">
                    Da terra para a sua mesa
                  </span>
                  <h3 className="text-xl sm:text-2xl font-bold">
                    Alimentos com sabor de verdade e respeito ao ecossistema
                  </h3>
                  <p className="text-xs sm:text-sm text-slate-200">
                    Ao comprar pelo ISAC Marketplace, você apoia diretamente os produtores da Enseada dos Corais, Gaibu e Cabo de Santo Agostinho.
                  </p>
                </div>
              </div>
            </div>
          </section>
        )}
      </main>
    </div>
  );
};

export default Institucional;
