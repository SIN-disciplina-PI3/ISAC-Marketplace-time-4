import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Login from '../Login';

// Mock do contexto de autenticação
const mockLogin = vi.fn();
const mockNavigate = vi.fn();

vi.mock('../../context/AuthContext', () => ({
  useAuth: () => ({
    login: mockLogin,
    usuario: null,
  }),
}));

vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

function renderLogin() {
  return render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );
}

beforeEach(() => {
  mockLogin.mockReset();
  mockNavigate.mockReset();
});

describe('Login', () => {
  it('renderiza os campos de e-mail e senha', () => {
    renderLogin();
    expect(screen.getByLabelText(/e-mail/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/senha/i)).toBeInTheDocument();
  });

  it('renderiza o botão de entrar', () => {
    renderLogin();
    expect(screen.getByRole('button', { name: /entrar/i })).toBeInTheDocument();
  });

  it('chama login() com as credenciais preenchidas', async () => {
    mockLogin.mockResolvedValueOnce(undefined);
    renderLogin();

    fireEvent.change(screen.getByLabelText(/e-mail/i), {
      target: { value: 'loja@teste.com' },
    });
    fireEvent.change(screen.getByLabelText(/senha/i), {
      target: { value: 'senha123' },
    });
    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('loja@teste.com', 'senha123');
    });
  });

  it('exibe mensagem de erro quando login falha', async () => {
    mockLogin.mockRejectedValueOnce(new Error('Credenciais inválidas'));
    renderLogin();

    fireEvent.change(screen.getByLabelText(/e-mail/i), {
      target: { value: 'errado@teste.com' },
    });
    fireEvent.change(screen.getByLabelText(/senha/i), {
      target: { value: 'senhaerrada' },
    });
    fireEvent.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() => {
      expect(screen.getByText(/inválid|incorrect|incorrect|verifique|tente novamente/i)).toBeInTheDocument();
    });
  });

  it('contém link para cadastro de loja', () => {
    renderLogin();
    const link = screen.getByRole('link', { name: /cadastr/i });
    expect(link).toBeInTheDocument();
  });
});
