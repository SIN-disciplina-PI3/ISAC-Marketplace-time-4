import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, BookOpen, Sprout } from 'lucide-react';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Conteudo } from '../types';
import { api } from '../services/api';

export const Institucional: React.FC = () => {
  const { chave } = useParams<{ chave: string }>();
  const [conteudo, setConteudo] = useState<Conteudo | null>(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

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

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-6">
      <Link
        to="/"
        className="inline-flex items-center gap-1.5 text-sm font-medium text-slate-600 hover:text-emerald-700 transition"
      >
        <ArrowLeft className="w-4 h-4" /> Início
      </Link>

      <article className="bg-white rounded-2xl border border-slate-200 p-6 sm:p-10 shadow-sm space-y-6">
        <div className="flex items-center gap-2 text-emerald-700 text-xs font-bold uppercase tracking-wider">
          {chave === 'agricultura' ? <Sprout className="w-4 h-4" /> : <BookOpen className="w-4 h-4" />}
          Institucional ISAC • Cabo de Santo Agostinho
        </div>

        <h1 className="text-3xl font-extrabold text-slate-900 border-b border-slate-100 pb-4">
          {conteudo.titulo}
        </h1>

        <div className="prose prose-slate max-w-none text-slate-700 leading-relaxed whitespace-pre-line text-base">
          {conteudo.corpoMarkdown}
        </div>

        <div className="border-t border-slate-100 pt-6 text-xs text-slate-400">
          Última atualização: {new Date(conteudo.atualizadoEm).toLocaleDateString('pt-BR')}
        </div>
      </article>
    </div>
  );
};

export default Institucional;
