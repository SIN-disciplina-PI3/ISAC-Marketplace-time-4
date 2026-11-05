import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Vitrine from '../Vitrine';
import { Anuncio } from '../../types';

// Mock da API
vi.mock('../../services/api', () => ({
  api: {
    buscar: vi.fn(),
    listarCategorias: vi.fn(),
  },
}));

import { api } from '../../services/api';

const anuncioMock: Anuncio = {
  id: 42,
  titulo: 'Cocada artesanal',
  descricao: 'Cocada fresca da vizinha',
  preco: 5.0,
  categoria: 'Doces',
  nomeLoja: 'Doceria Enseada',
  bairro: 'Centro',
  telefoneWhatsapp: '81988880000',
  status: 'PUBLICADO',
  destaque: false,
  fotos: [],
};

function renderVitrine() {
  return render(
    <MemoryRouter initialEntries={['/vitrine']}>
      <Vitrine />
    </MemoryRouter>
  );
}

beforeEach(() => {
  vi.mocked(api.listarCategorias).mockResolvedValue([]);
  vi.mocked(api.buscar).mockResolvedValue({
    content: [anuncioMock],
    totalPages: 1,
    totalElements: 1,
    number: 0,
  });
});

describe('Vitrine', () => {
  it('renderiza o campo de busca', async () => {
    renderVitrine();
    expect(screen.getByPlaceholderText(/buscar/i)).toBeInTheDocument();
  });

  it('exibe anúncios retornados pela API', async () => {
    renderVitrine();
    await waitFor(() => {
      expect(screen.getByText('Cocada artesanal')).toBeInTheDocument();
    });
  });

  it('chama api.buscar ao digitar no campo de busca e submeter', async () => {
    renderVitrine();
    const input = screen.getByPlaceholderText(/buscar/i);
    fireEvent.change(input, { target: { value: 'cocada' } });
    const form = input.closest('form');
    if (form) fireEvent.submit(form);
    await waitFor(() => {
      expect(api.buscar).toHaveBeenCalled();
    });
  });

  it('exibe mensagem quando não há resultados', async () => {
    vi.mocked(api.buscar).mockResolvedValue({
      content: [],
      totalPages: 0,
      totalElements: 0,
      number: 0,
    });
    renderVitrine();
    await waitFor(() => {
      expect(screen.getByText(/nenhum anúncio/i)).toBeInTheDocument();
    });
  });
});
