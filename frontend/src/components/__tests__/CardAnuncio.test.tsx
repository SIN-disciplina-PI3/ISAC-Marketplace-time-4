import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import CardAnuncio from '../CardAnuncio';
import { Anuncio } from '../../types';

const anuncioBase: Anuncio = {
  id: 1,
  titulo: 'Tapioca de queijo',
  descricao: 'Deliciosa tapioca artesanal',
  preco: 8.5,
  categoria: 'Alimentação',
  nomeLoja: 'Tapiocaria Enseada',
  bairro: 'Enseada dos Corais',
  telefoneWhatsapp: '81999990000',
  status: 'PUBLICADO',
  destaque: false,
  fotos: [],
};

function renderCard(anuncio: Partial<Anuncio> = {}) {
  return render(
    <MemoryRouter>
      <CardAnuncio anuncio={{ ...anuncioBase, ...anuncio }} />
    </MemoryRouter>
  );
}

describe('CardAnuncio', () => {
  it('exibe o título do anúncio', () => {
    renderCard();
    expect(screen.getByText('Tapioca de queijo')).toBeInTheDocument();
  });

  it('exibe o preço formatado em reais', () => {
    renderCard();
    expect(screen.getByText(/8,50/)).toBeInTheDocument();
  });

  it('exibe o nome da loja', () => {
    renderCard();
    expect(screen.getByText(/Tapiocaria Enseada/i)).toBeInTheDocument();
  });

  it('exibe o bairro', () => {
    renderCard();
    expect(screen.getByText(/Enseada dos Corais/i)).toBeInTheDocument();
  });

  it('contém link para o WhatsApp', () => {
    renderCard();
    const links = screen.getAllByRole('link');
    const whatsapp = links.find((l) => l.getAttribute('href')?.includes('wa.me'));
    expect(whatsapp).toBeDefined();
  });

  it('exibe o badge "Destaque" quando destaque=true', () => {
    renderCard({ destaque: true });
    expect(screen.getByText(/destaque/i)).toBeInTheDocument();
  });

  it('não exibe o badge "Destaque" quando destaque=false', () => {
    renderCard({ destaque: false });
    expect(screen.queryByText(/destaque/i)).toBeNull();
  });

  it('exibe "Preço a consultar" quando preco é null', () => {
    renderCard({ preco: null });
    expect(screen.getByText(/a consultar/i)).toBeInTheDocument();
  });

  it('contém link para a página de detalhes', () => {
    renderCard();
    const links = screen.getAllByRole('link');
    const detalhes = links.find((l) => l.getAttribute('href')?.includes('/anuncios/1'));
    expect(detalhes).toBeDefined();
  });
});
