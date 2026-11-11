import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, BookOpen, Sprout, Leaf, Sun, Droplets, HeartHandshake, CheckCircle } from 'lucide-react';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { TextoFormatado } from '../components/TextoFormatado';
import { Sobre } from './Sobre';
import { Conteudo } from '../types';
import { api } from '../services/api';

export const Institucional: React.FC = () => {
  const { chave } = useParams<{ chave: string }>();
  const [conteudo, setConteudo] = useState<Conteudo | null>(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  // Se a chave for 'sobre', exibe o componente completo redesenhado Sobre
  if (chave === 'sobre') {
    return <Sobre />;
  }

  useEffect(() => {
    async function carregar() {
      if (!chave) return;
      setLoading(true);
      try {
        const dados = await api.obterConteudo(chave);
        setConteudo(dados);
        setErro(null);
      } catch (err: any) {
        setErro(err.message || 'Erro ao carregar conteúdo.');
      } finally {
        setLoading(false);
      }
    }
    carregar();
  }, [chave]);

  if (loading) {
    return <LoadingSpinner texto="Carregando informações institucionais..." />;
  }

  if (erro || !conteudo) {
    return (
      <div className="max-w-2xl mx-auto py-16 px-4 text-center space-y-4">
        <h2 className="text-xl font-bold text-slate-800">Conteúdo não disponível</h2>
        <p className="text-slate-500 text-sm">{erro || 'O conteúdo solicitado não foi encontrado.'}</p>
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
      {/* Banner Superior com Imagens Ilustrativas para Páginas Institucionais */}
      <section className="relative bg-slate-900 text-white overflow-hidden py-16 sm:py-20">
        <div className="absolute inset-0 z-0">
          <img
            src={
              ehAgricultura
                ? 'https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=1600&q=80' // Plantação verde / agricultura familiar
                : 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?auto=format&fit=crop&w=1600&q=80'
            }
            alt={conteudo.titulo}
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
              {conteudo.titulo}
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
            <TextoFormatado texto={conteudo.corpoMarkdown} />
          </div>

          <div className="border-t border-slate-100 pt-6 flex flex-wrap items-center justify-between text-xs text-slate-400 gap-2">
            <span>Última atualização: {new Date(conteudo.atualizadoEm ?? '').toLocaleDateString('pt-BR')}</span>
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
