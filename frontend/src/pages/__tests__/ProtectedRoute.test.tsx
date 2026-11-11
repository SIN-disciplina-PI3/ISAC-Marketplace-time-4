import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import { ProtectedRoute } from '../../components/ProtectedRoute';

// Mock do contexto de autenticação
vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from '../../context/AuthContext';

function PaginaProtegida() {
  return <div>Conteúdo protegido</div>;
}

function PaginaLogin() {
  return <div>Página de login</div>;
}

function renderComRota(papel: string | null) {
  vi.mocked(useAuth).mockReturnValue({
    usuario: papel ? { email: 'user@test.com', papel } as any : null,
    login: vi.fn(),
    logout: vi.fn(),
    loading: false,
    carregando: false,
    isLoja: papel === 'LOJA',
    isAdmin: papel === 'ADMIN',
    isAuthenticated: !!papel,
  } as any);

  return render(
    <MemoryRouter initialEntries={['/admin']}>
      <Routes>
        <Route path="/login" element={<PaginaLogin />} />
        <Route
          path="/admin"
          element={
            <ProtectedRoute papeis={['ADMIN']}>
              <PaginaProtegida />
            </ProtectedRoute>
          }
        />
      </Routes>
    </MemoryRouter>
  );
}

describe('ProtectedRoute', () => {
  it('redireciona para /login quando usuário não autenticado', () => {
    renderComRota(null);
    expect(screen.getByText('Página de login')).toBeInTheDocument();
    expect(screen.queryByText('Conteúdo protegido')).toBeNull();
  });

  it('exibe conteúdo quando usuário tem papel permitido', () => {
    renderComRota('ADMIN');
    expect(screen.getByText('Conteúdo protegido')).toBeInTheDocument();
  });

  it('redireciona quando usuário tem papel insuficiente', () => {
    renderComRota('LOJA');
    expect(screen.queryByText('Conteúdo protegido')).toBeNull();
  });
});
