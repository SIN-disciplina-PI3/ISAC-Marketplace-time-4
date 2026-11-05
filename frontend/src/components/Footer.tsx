import React from 'react';
import { Link } from 'react-router-dom';
import { Heart, MapPin, MessageCircle, Mail } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-slate-900 text-slate-300 mt-20 border-t border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Coluna 1: Sobre */}
          <div className="space-y-3">
            <h3 className="text-white text-lg font-bold flex items-center gap-1.5">
              ISAC<span className="text-emerald-500">.</span>
            </h3>
            <p className="text-sm text-slate-400 leading-relaxed">
              Instituto Socioambiental do Cabo de Santo Agostinho (PE). Fortalecendo a economia solidária, agricultura familiar e a preservação ecológica da Enseada dos Corais e região.
            </p>
            <div className="flex items-center gap-2 text-xs text-slate-400 pt-1">
              <MapPin className="w-4 h-4 text-emerald-400 flex-shrink-0" />
              <span>Enseada dos Corais, Cabo de Santo Agostinho - PE</span>
            </div>
          </div>

          {/* Coluna 2: Navegação */}
          <div>
            <h4 className="text-white font-semibold mb-3 text-sm tracking-wider uppercase">Navegação</h4>
            <ul className="space-y-2 text-sm text-slate-400">
              <li><Link to="/vitrine" className="hover:text-emerald-400 transition">Vitrine Comunitária</Link></li>
              <li><Link to="/eventos" className="hover:text-emerald-400 transition">Agenda de Eventos</Link></li>
              <li><Link to="/como-anunciar" className="hover:text-emerald-400 transition">Como Anunciar</Link></li>
              <li><Link to="/cadastro-loja" className="hover:text-emerald-400 transition">Cadastrar Vendedor</Link></li>
            </ul>
          </div>

          {/* Coluna 3: Institucional */}
          <div>
            <h4 className="text-white font-semibold mb-3 text-sm tracking-wider uppercase">Institucional</h4>
            <ul className="space-y-2 text-sm text-slate-400">
              <li><Link to="/institucional/sobre" className="hover:text-emerald-400 transition">História e Missão do ISAC</Link></li>
              <li><Link to="/institucional/agricultura" className="hover:text-emerald-400 transition">Agricultura Familiar & Meio Ambiente</Link></li>
              <li><Link to="/login" className="hover:text-emerald-400 transition">Área Restrita (Login)</Link></li>
            </ul>
          </div>

          {/* Coluna 4: WhatsApp Conversacional */}
          <div className="space-y-3">
            <h4 className="text-white font-semibold text-sm tracking-wider uppercase">Bot no WhatsApp</h4>
            <p className="text-xs text-slate-400">
              Busque produtos, anuncie ou consulte vendedores direto pelo WhatsApp oficial da comunidade:
            </p>
            <a
              href="https://wa.me/5581999990001?text=Ol%C3%A1%2C%20gostaria%20de%20conhecer%20o%20ISAC%20Marketplace"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 bg-emerald-700 hover:bg-emerald-600 text-white text-xs font-semibold py-2 px-3.5 rounded-lg shadow transition"
            >
              <MessageCircle className="w-4 h-4" />
              Chamar no WhatsApp
            </a>
          </div>
        </div>

        <div className="border-t border-slate-800 mt-10 pt-6 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-4">
          <p>© {new Date().getFullYear()} ISAC - Instituto Socioambiental do Cabo. Todos os direitos reservados.</p>
          <p className="flex items-center gap-1">
            Desenvolvido com <Heart className="w-3.5 h-3.5 text-rose-500 fill-rose-500" /> para a comunidade de Enseada dos Corais
          </p>
        </div>
      </div>
    </footer>
  );
};
