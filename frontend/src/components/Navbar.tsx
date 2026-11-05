import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Menu, X, ShoppingBag, User, LogOut, Shield, Store } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Navbar: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { usuario, logout, isLoja, isAdmin, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsOpen(false);
  };

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-50 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo & Marca */}
          <Link to="/" className="flex items-center gap-2.5 focus:outline-none focus:ring-2 focus:ring-isac-green rounded-lg p-1">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-600 to-sky-600 flex items-center justify-center text-white shadow">
              <ShoppingBag className="w-5 h-5" />
            </div>
            <div>
              <span className="font-extrabold text-xl tracking-tight text-slate-900 block leading-none">
                ISAC<span className="text-emerald-600">.</span>
              </span>
              <span className="text-[10px] uppercase font-bold tracking-wider text-slate-500 block leading-none mt-0.5">
                Marketplace Comunitário
              </span>
            </div>
          </Link>

          {/* Links Desktop */}
          <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-slate-600">
            <Link to="/" className="hover:text-emerald-600 transition">Início</Link>
            <Link to="/vitrine" className="hover:text-emerald-600 transition">Vitrine</Link>
            <Link to="/eventos" className="hover:text-emerald-600 transition">Eventos</Link>
            <Link to="/institucional/sobre" className="hover:text-emerald-600 transition">Sobre o ISAC</Link>
            <Link to="/institucional/agricultura" className="hover:text-emerald-600 transition">Agricultura Familiar</Link>
            <Link to="/como-anunciar" className="hover:text-emerald-600 transition">Como Anunciar</Link>
          </nav>

          {/* Ações Desktop */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <div className="flex items-center gap-3">
                {isLoja && (
                  <Link
                    to="/loja"
                    className="flex items-center gap-1.5 text-sm font-semibold bg-emerald-50 text-emerald-700 hover:bg-emerald-100 px-3.5 py-2 rounded-lg transition"
                  >
                    <Store className="w-4 h-4" /> Minha Loja
                  </Link>
                )}
                {isAdmin && (
                  <Link
                    to="/admin"
                    className="flex items-center gap-1.5 text-sm font-semibold bg-sky-50 text-sky-800 hover:bg-sky-100 px-3.5 py-2 rounded-lg transition"
                  >
                    <Shield className="w-4 h-4" /> Painel Admin
                  </Link>
                )}
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-1 text-sm text-slate-500 hover:text-rose-600 p-2 rounded-lg transition"
                  title="Sair"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="text-sm font-medium text-slate-700 hover:text-emerald-600 px-3 py-2 rounded-lg transition"
                >
                  Entrar
                </Link>
                <Link
                  to="/cadastro-loja"
                  className="text-sm font-semibold bg-emerald-600 hover:bg-emerald-700 text-white px-4 py-2 rounded-lg shadow-sm transition active:scale-95"
                >
                  Quero Anunciar
                </Link>
              </div>
            )}
          </div>

          {/* Botão Hambúrguer Mobile */}
          <div className="md:hidden flex items-center">
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="p-2 rounded-md text-slate-600 hover:text-slate-900 hover:bg-slate-100 focus:outline-none"
              aria-label="Abrir menu"
            >
              {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Menu Mobile */}
      {isOpen && (
        <div className="md:hidden border-t border-slate-200 bg-white px-4 pt-3 pb-6 space-y-3">
          <Link
            to="/"
            onClick={() => setIsOpen(false)}
            className="block py-2 text-base font-medium text-slate-700 hover:text-emerald-600"
          >
            Início
          </Link>
          <Link
            to="/vitrine"
            onClick={() => setIsOpen(false)}
            className="block py-2 text-base font-medium text-slate-700 hover:text-emerald-600"
          >
            Vitrine
          </Link>
          <Link
            to="/eventos"
            onClick={() => setIsOpen(false)}
            className="block py-2 text-base font-medium text-slate-700 hover:text-emerald-600"
          >
            Eventos
          </Link>
          <Link
            to="/institucional/sobre"
            onClick={() => setIsOpen(false)}
            className="block py-2 text-base font-medium text-slate-700 hover:text-emerald-600"
          >
            Sobre o ISAC
          </Link>
          <Link
            to="/institucional/agricultura"
            onClick={() => setIsOpen(false)}
            className="block py-2 text-base font-medium text-slate-700 hover:text-emerald-600"
          >
            Agricultura Familiar
          </Link>
          <Link
            to="/como-anunciar"
            onClick={() => setIsOpen(false)}
            className="block py-2 text-base font-medium text-slate-700 hover:text-emerald-600"
          >
            Como Anunciar
          </Link>

          <div className="border-t border-slate-100 pt-3 space-y-2">
            {isAuthenticated ? (
              <>
                {isLoja && (
                  <Link
                    to="/loja"
                    onClick={() => setIsOpen(false)}
                    className="flex items-center gap-2 py-2 text-base font-medium text-emerald-700"
                  >
                    <Store className="w-5 h-5" /> Minha Loja
                  </Link>
                )}
                {isAdmin && (
                  <Link
                    to="/admin"
                    onClick={() => setIsOpen(false)}
                    className="flex items-center gap-2 py-2 text-base font-medium text-sky-700"
                  >
                    <Shield className="w-5 h-5" /> Painel Admin
                  </Link>
                )}
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-2 py-2 text-base font-medium text-rose-600 w-full text-left"
                >
                  <LogOut className="w-5 h-5" /> Sair
                </button>
              </>
            ) : (
              <div className="flex flex-col gap-2 pt-2">
                <Link
                  to="/login"
                  onClick={() => setIsOpen(false)}
                  className="w-full text-center py-2.5 rounded-lg border border-slate-300 font-semibold text-slate-700"
                >
                  Entrar
                </Link>
                <Link
                  to="/cadastro-loja"
                  onClick={() => setIsOpen(false)}
                  className="w-full text-center py-2.5 rounded-lg bg-emerald-600 text-white font-semibold shadow"
                >
                  Cadastrar Loja
                </Link>
              </div>
            )}
          </div>
        </div>
      )}
    </header>
  );
};
