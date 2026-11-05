import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../../services/api';
import { MetricasAdmin } from '../../types';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { Store, ShoppingBag, Tag, CalendarDays, FileText, BarChart3, Users } from 'lucide-react';

interface CardMetricaProps {
  titulo: string;
  valor: number | string;
  icone: React.ReactNode;
  cor: string;
}

function CardMetrica({ titulo, valor, icone, cor }: CardMetricaProps) {
  return (
    <div className={`bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex items-center gap-4`}>
      <div className={`p-3 rounded-xl ${cor}`}>{icone}</div>
      <div>
        <p className="text-sm text-gray-500">{titulo}</p>
        <p className="text-2xl font-bold text-gray-800">{valor}</p>
      </div>
    </div>
  );
}

export default function DashboardAdmin() {
  const [metricas, setMetricas] = useState<MetricasAdmin | null>(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    api.adminObterMetricas()
      .then(setMetricas)
      .finally(() => setCarregando(false));
  }, []);

  if (carregando) return <LoadingSpinner />;

  const links = [
    { to: '/admin/lojas', label: 'Lojas & Assinaturas', icone: <Store size={20} />, cor: 'bg-blue-50 text-blue-600' },
    { to: '/admin/anuncios', label: 'Moderação de Anúncios', icone: <ShoppingBag size={20} />, cor: 'bg-yellow-50 text-yellow-600' },
    { to: '/admin/categorias', label: 'Categorias', icone: <Tag size={20} />, cor: 'bg-purple-50 text-purple-600' },
    { to: '/admin/eventos', label: 'Eventos', icone: <CalendarDays size={20} />, cor: 'bg-green-50 text-green-600' },
    { to: '/admin/conteudos', label: 'Conteúdos Institucionais', icone: <FileText size={20} />, cor: 'bg-rose-50 text-rose-600' },
  ];

  return (
    <div className="max-w-5xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-8">
        <BarChart3 className="text-isac-green-600" size={28} />
        <h1 className="text-2xl font-bold text-gray-800">Painel Administrativo</h1>
      </div>

      {metricas && (
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-10">
          <CardMetrica
            titulo="Lojas ativas"
            valor={metricas.totalLojasAtivas ?? '—'}
            icone={<Store size={22} className="text-blue-600" />}
            cor="bg-blue-50"
          />
          <CardMetrica
            titulo="Anúncios publicados"
            valor={metricas.totalAnunciosPublicados ?? '—'}
            icone={<ShoppingBag size={22} className="text-isac-green-600" />}
            cor="bg-green-50"
          />
          <CardMetrica
            titulo="Pendentes moderação"
            valor={metricas.anunciosPendentesModeracao ?? '—'}
            icone={<Users size={22} className="text-yellow-600" />}
            cor="bg-yellow-50"
          />
          <CardMetrica
            titulo="Buscas hoje"
            valor={metricas.buscasUltimas24h ?? '—'}
            icone={<BarChart3 size={22} className="text-purple-600" />}
            cor="bg-purple-50"
          />
          <CardMetrica
            titulo="Cliques WhatsApp"
            valor={metricas.cliquesWhatsappUltimas24h ?? '—'}
            icone={<Users size={22} className="text-rose-600" />}
            cor="bg-rose-50"
          />
          <CardMetrica
            titulo="Assinaturas ativas"
            valor={metricas.totalAssinaturasAtivas ?? '—'}
            icone={<Tag size={22} className="text-indigo-600" />}
            cor="bg-indigo-50"
          />
        </div>
      )}

      <h2 className="text-lg font-semibold text-gray-700 mb-4">Navegação rápida</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        {links.map((l) => (
          <Link
            key={l.to}
            to={l.to}
            className="flex items-center gap-3 bg-white border border-gray-100 rounded-2xl p-4 shadow-sm hover:shadow-md transition"
          >
            <div className={`p-2 rounded-lg ${l.cor}`}>{l.icone}</div>
            <span className="font-medium text-gray-700">{l.label}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
