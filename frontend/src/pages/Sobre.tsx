import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowLeft,
  ExternalLink,
  GraduationCap,
  Trees,
  Users,
  Landmark,
  MapPin,
  Heart,
  ShieldCheck,
  Compass,
  Phone,
  Mail
} from 'lucide-react';
import { api } from '../services/api';
import { Conteudo } from '../types';
import { TextoFormatado } from '../components/TextoFormatado';

export const Sobre: React.FC = () => {
  const [conteudo, setConteudo] = useState<Conteudo | null>(null);

  useEffect(() => {
    api.obterConteudo('sobre')
      .then((data) => setConteudo(data))
      .catch((err) => console.warn('Conteúdo dinâmico sobre não carregado:', err));
  }, []);

  const pilares = [
    {
      titulo: 'Educação Ambiental',
      descricao:
        'Oficinas comunitárias, formação ecológica de jovens e adultos, sensibilização sobre descarte correto de resíduos e respeito aos ecossistemas costeiros.',
      icone: <GraduationCap className="w-8 h-8 text-emerald-600" />,
      bgCor: 'bg-emerald-50 text-emerald-900 border-emerald-100',
    },
    {
      titulo: 'Reflorestamento da Mata Atlântica',
      descricao:
        'Recuperação de nascentes, preservação de restingas e matas ciliares no Cabo de Santo Agostinho através de mutirões de plantio de espécies nativas.',
      icone: <Trees className="w-8 h-8 text-green-600" />,
      bgCor: 'bg-green-50 text-green-900 border-green-100',
    },
    {
      titulo: 'Inclusão Social',
      descricao:
        'Geração de renda sustentável, apoio a artesãos locais, valorização da pesca artesanal e agricultura familiar solidária em Enseada dos Corais e Gaibu.',
      icone: <Users className="w-8 h-8 text-sky-600" />,
      bgCor: 'bg-sky-50 text-sky-900 border-sky-100',
    },
    {
      titulo: 'Defesa de Políticas Públicas',
      descricao:
        'Atuação participativa nos conselhos de meio ambiente, planejamento urbano sustentável e garantia dos direitos das comunidades litorâneas.',
      icone: <Landmark className="w-8 h-8 text-amber-600" />,
      bgCor: 'bg-amber-50 text-amber-900 border-amber-100',
    },
  ];

  return (
    <div className="min-h-screen bg-slate-50 pb-20">
      {/* Hero Banner com Imagem de Enseada dos Corais / Cabo de Santo Agostinho */}
      <section className="relative bg-slate-900 text-white overflow-hidden">
        <div className="absolute inset-0 z-0">
          <img
            src="https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1600&q=80"
            alt="Litoral da Enseada dos Corais e Cabo de Santo Agostinho"
            className="w-full h-full object-cover opacity-35"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/60 to-transparent" />
        </div>

        <div className="relative z-10 max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-20 sm:py-28">
          <Link
            to="/"
            className="inline-flex items-center gap-2 text-sm text-emerald-300 hover:text-white transition mb-6"
          >
            <ArrowLeft className="w-4 h-4" /> Voltar ao Início
          </Link>

          <div className="max-w-3xl space-y-4">
            <span className="inline-flex items-center gap-1.5 bg-emerald-500/20 text-emerald-300 border border-emerald-400/30 text-xs font-bold uppercase tracking-wider px-3.5 py-1.5 rounded-full backdrop-blur-sm">
              <Compass className="w-3.5 h-3.5" /> Instituto Socioambiental
            </span>
            <h1 className="text-3xl sm:text-5xl font-extrabold tracking-tight text-white leading-tight">
              Instituto de Sustentabilidade e Ação Comunitária (ISAC)
            </h1>
            <p className="text-lg sm:text-xl text-slate-200 leading-relaxed font-light">
              Transformando a Enseada dos Corais, Gaibu e todo o litoral sul do Cabo de Santo Agostinho através da preservação ambiental, economia popular solidária e fortalecimento comunitário.
            </p>

            <div className="pt-4 flex flex-wrap gap-4 items-center">
              <a
                href="https://www.isac-instituto.org.br/"
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-bold px-6 py-3.5 rounded-xl shadow-lg shadow-emerald-950/40 transition active:scale-95 text-sm sm:text-base"
              >
                Conheça o site oficial do Instituto
                <ExternalLink className="w-4 h-4" />
              </a>

              <Link
                to="/vitrine"
                className="inline-flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white font-semibold px-5 py-3.5 rounded-xl backdrop-blur-sm border border-white/20 transition text-sm sm:text-base"
              >
                Explorar Vitrine Comunitária
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Conteúdo Institucional e Missão */}
      <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 -mt-8 relative z-20 space-y-12">
        {/* Bloco de Missão e Visão */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-8 sm:p-12 space-y-6">
          <div className="flex items-center gap-2 text-emerald-700 text-xs font-bold uppercase tracking-wider">
            <ShieldCheck className="w-4 h-4" />
            Nossa História & Propósito
          </div>

          <h2 className="text-2xl sm:text-3xl font-bold text-slate-900">
            Compromisso vivo com as pessoas e a natureza de Pernambuco
          </h2>

          {conteudo?.corpoMarkdown ? (
            <TextoFormatado texto={conteudo.corpoMarkdown} className="text-base sm:text-lg" />
          ) : (
            <div className="space-y-4 text-slate-700 leading-relaxed text-base sm:text-lg">
              <p>
                O <strong>ISAC (Instituto de Sustentabilidade e Ação Comunitária)</strong> nasceu da união de lideranças comunitárias, ambientalistas, artesãos e agricultores familiares do <strong>Cabo de Santo Agostinho (PE)</strong>, com o intuito de salvaguardar as belezas naturais da região da <strong>Enseada dos Corais</strong> e gerar oportunidades dignas para as famílias locais.
              </p>
              <p>
                Acreditamos que a conservação ecológica caminha lado a lado com a economia solidária. Por meio de feiras colaborativas, do marketplace comunitário e de programas de capacitação, aproximamos produtores conscientes dos moradores e visitantes, fortalecendo a rede produtiva regional.
              </p>
            </div>
          )}
        </div>

        {/* 4 Cards de Pilares de Atuação */}
        <section className="space-y-6">
          <div className="text-center max-w-2xl mx-auto space-y-2">
            <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900">
              Pilares de Atuação do ISAC
            </h2>
            <p className="text-sm sm:text-base text-slate-600">
              Ações concretas e continuadas que constroem um futuro mais sustentável para nossa região.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {pilares.map((pilar, idx) => (
              <div
                key={idx}
                className="bg-white rounded-2xl border border-slate-200 p-8 shadow-sm hover:shadow-md transition duration-200 flex flex-col justify-between space-y-4 group"
              >
                <div className="space-y-3">
                  <div className={`w-14 h-14 rounded-xl border flex items-center justify-center ${pilar.bgCor}`}>
                    {pilar.icone}
                  </div>
                  <h3 className="text-xl font-bold text-slate-900 group-hover:text-emerald-700 transition">
                    {pilar.titulo}
                  </h3>
                  <p className="text-sm sm:text-base text-slate-600 leading-relaxed">
                    {pilar.descricao}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Card Destaque: Endereço Oficial e Contato */}
        <section className="bg-gradient-to-br from-emerald-900 via-slate-900 to-sky-950 text-white rounded-3xl p-8 sm:p-12 shadow-xl border border-emerald-800/40 relative overflow-hidden">
          <div className="relative z-10 grid grid-cols-1 lg:grid-cols-2 gap-8 items-center">
            <div className="space-y-4">
              <span className="inline-flex items-center gap-1.5 bg-emerald-500/20 text-emerald-300 text-xs font-bold uppercase tracking-wider px-3 py-1 rounded-full border border-emerald-500/30">
                <MapPin className="w-3.5 h-3.5" /> Sede Oficial
              </span>
              <h3 className="text-2xl sm:text-3xl font-extrabold text-white">
                Venha nos visitar em Enseada dos Corais
              </h3>
              <p className="text-slate-300 text-sm sm:text-base leading-relaxed">
                Nossa sede comunitária está de portas abertas para receber voluntários, moradores e parceiros que queiram somar esforços com as iniciativas socioambientais do Instituto.
              </p>

              <div className="pt-2 space-y-2 text-sm text-slate-200">
                <div className="flex items-start gap-3">
                  <MapPin className="w-5 h-5 text-emerald-400 flex-shrink-0 mt-0.5" />
                  <span className="font-medium">
                    Rua VT 2 (Av. das Sereias), 135 - Setor IV, Enseada dos Corais, Cabo de Santo Agostinho/PE
                  </span>
                </div>
                <div className="flex items-center gap-3">
                  <Phone className="w-5 h-5 text-emerald-400 flex-shrink-0" />
                  <span>WhatsApp Comunitário: (81) 99999-0001</span>
                </div>
                <div className="flex items-center gap-3">
                  <Mail className="w-5 h-5 text-emerald-400 flex-shrink-0" />
                  <span>contato@isac-instituto.org.br</span>
                </div>
              </div>
            </div>

            <div className="flex flex-col sm:flex-row lg:flex-col gap-4 justify-center items-stretch lg:items-end">
              <a
                href="https://www.isac-instituto.org.br/"
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center justify-center gap-2 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold px-6 py-4 rounded-xl shadow-lg transition active:scale-95 text-center"
              >
                Conheça o site oficial do Instituto
                <ExternalLink className="w-4 h-4" />
              </a>

              <a
                href="https://wa.me/5581999990001?text=Ol%C3%A1%2C%20gostaria%20de%20saber%20mais%20sobre%20as%20a%C3%A7%C3%B5es%20do%20ISAC!"
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center justify-center gap-2 bg-white/10 hover:bg-white/20 text-white font-semibold px-6 py-3.5 rounded-xl border border-white/20 transition text-center"
              >
                Falar com o Instituto no WhatsApp
              </a>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
};

export default Sobre;
