import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';

import Home from './pages/Home';
import Vitrine from './pages/Vitrine';
import AnuncioDetalhes from './pages/AnuncioDetalhes';
import Eventos from './pages/Eventos';
import Institucional from './pages/Institucional';
import ComoAnunciar from './pages/ComoAnunciar';
import Login from './pages/Login';
import CadastroLoja from './pages/CadastroLoja';

import DashboardLoja from './pages/loja/DashboardLoja';
import MeusAnuncios from './pages/loja/MeusAnuncios';
import DadosLoja from './pages/loja/DadosLoja';

import DashboardAdmin from './pages/admin/DashboardAdmin';
import LojasAdmin from './pages/admin/LojasAdmin';
import AnunciosAdmin from './pages/admin/AnunciosAdmin';
import CategoriasAdmin from './pages/admin/CategoriasAdmin';
import EventosAdmin from './pages/admin/EventosAdmin';
import ConteudosAdmin from './pages/admin/ConteudosAdmin';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="min-h-screen flex flex-col bg-gray-50">
          <Navbar />
          <main className="flex-1">
            <Routes>
              {/* Rotas públicas */}
              <Route path="/" element={<Home />} />
              <Route path="/vitrine" element={<Vitrine />} />
              <Route path="/anuncios/:id" element={<AnuncioDetalhes />} />
              <Route path="/eventos" element={<Eventos />} />
              <Route path="/institucional/:chave" element={<Institucional />} />
              <Route path="/como-anunciar" element={<ComoAnunciar />} />
              <Route path="/login" element={<Login />} />
              <Route path="/cadastro-loja" element={<CadastroLoja />} />

              {/* Rotas da loja (LOJA ou ADMIN) */}
              <Route
                path="/loja"
                element={
                  <ProtectedRoute papeis={['LOJA', 'ADMIN']}>
                    <DashboardLoja />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/loja/anuncios"
                element={
                  <ProtectedRoute papeis={['LOJA', 'ADMIN']}>
                    <MeusAnuncios />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/loja/dados"
                element={
                  <ProtectedRoute papeis={['LOJA', 'ADMIN']}>
                    <DadosLoja />
                  </ProtectedRoute>
                }
              />

              {/* Rotas admin */}
              <Route
                path="/admin"
                element={
                  <ProtectedRoute papeis={['ADMIN']}>
                    <DashboardAdmin />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/lojas"
                element={
                  <ProtectedRoute papeis={['ADMIN']}>
                    <LojasAdmin />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/anuncios"
                element={
                  <ProtectedRoute papeis={['ADMIN']}>
                    <AnunciosAdmin />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/categorias"
                element={
                  <ProtectedRoute papeis={['ADMIN']}>
                    <CategoriasAdmin />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/eventos"
                element={
                  <ProtectedRoute papeis={['ADMIN']}>
                    <EventosAdmin />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/conteudos"
                element={
                  <ProtectedRoute papeis={['ADMIN']}>
                    <ConteudosAdmin />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </main>
          <Footer />
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}
