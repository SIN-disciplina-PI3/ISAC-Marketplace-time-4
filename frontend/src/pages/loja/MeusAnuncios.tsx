import React, { useEffect, useState } from 'react';
import { Plus, RefreshCw, Pause, Play, Edit3, Image, AlertCircle, CheckCircle2, X } from 'lucide-react';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { Anuncio, Categoria, AssinaturaInfo } from '../../types';
import { api } from '../../services/api';

export const MeusAnuncios: React.FC = () => {
  const [anuncios, setAnuncios] = useState<Anuncio[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [assinatura, setAssinatura] = useState<AssinaturaInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [mensagemSucesso, setMensagemSucesso] = useState<string | null>(null);
  const [mensagemErro, setMensagemErro] = useState<string | null>(null);

  // Modal de Criação / Edição
  const [modalAberto, setModalAberto] = useState(false);
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [preco, setPreco] = useState('');
  const [categoriaId, setCategoriaId] = useState<number | undefined>(undefined);
  const [bairro, setBairro] = useState('');
  const [tipo, setTipo] = useState<'PRODUTO' | 'SERVICO'>('PRODUTO');

  // Modal de Upload de Foto
  const [modalFotoAberto, setModalFotoAberto] = useState(false);
  const [anuncioFotoId, setAnuncioFotoId] = useState<number | null>(null);
  const [arquivoFoto, setArquivoFoto] = useState<File | null>(null);
  const [enviandoFoto, setEnviandoFoto] = useState(false);

  const carregarTudo = async () => {
    setLoading(true);
    try {
      const [anunRes, catRes, assRes] = await Promise.all([
        api.listarMeusAnuncios(),
        api.listarCategorias(),
        api.obterMinhaAssinatura(),
      ]);
      setAnuncios(anunRes);
      setCategorias(catRes);
      setAssinatura(assRes);
    } catch (err: any) {
      setMensagemErro(err.message || 'Erro ao carregar anúncios.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarTudo();
  }, []);

  const abrirModalNovo = () => {
    setEditandoId(null);
    setTitulo('');
    setDescricao('');
    setPreco('');
    setCategoriaId(categorias[0]?.id);
    setBairro('');
    setTipo('PRODUTO');
    setModalAberto(true);
  };

  const abrirModalEditar = (anuncio: Anuncio) => {
    setEditandoId(anuncio.id);
    setTitulo(anuncio.titulo);
    setDescricao(anuncio.descricao || '');
    setPreco(anuncio.preco != null ? String(anuncio.preco) : '');
    setCategoriaId(anuncio.categoriaId);
    setBairro(anuncio.bairro);
    setTipo(anuncio.tipo ?? 'PRODUTO');
    setModalAberto(true);
  };

  const salvarAnuncio = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensagemErro(null);

    const valorPreco = parseFloat(preco.replace(',', '.'));
    if (isNaN(valorPreco) || valorPreco <= 0) {
      setMensagemErro('Informe um preço válido maior que zero.');
      return;
    }

    try {
      if (editandoId) {
        await api.atualizarAnuncio(editandoId, {
          titulo,
          descricao,
          preco: valorPreco,
          categoriaId,
          bairro,
          tipo,
        });
        setMensagemSucesso('Anúncio atualizado com sucesso!');
      } else {
        await api.criarAnuncio({
          titulo,
          descricao,
          preco: valorPreco,
          categoriaId,
          bairro,
          tipo,
        });
        setMensagemSucesso('Anúncio criado com sucesso!');
      }
      setModalAberto(false);
      carregarTudo();
    } catch (err: any) {
      setMensagemErro(err.message || 'Erro ao salvar anúncio.');
    }
  };

  const handleRenovar = async (id: number) => {
    try {
      await api.renovarAnuncio(id);
      setMensagemSucesso('Anúncio renovado com sucesso por mais 15 dias!');
      carregarTudo();
    } catch (err: any) {
      setMensagemErro(err.message || 'Erro ao renovar anúncio.');
    }
  };

  const handlePausar = async (id: number) => {
    try {
      await api.pausarAnuncio(id);
      setMensagemSucesso('Anúncio pausado.');
      carregarTudo();
    } catch (err: any) {
      setMensagemErro(err.message || 'Erro ao pausar anúncio.');
    }
  };

  const handleReativar = async (id: number) => {
    try {
      await api.reativarAnuncio(id);
      setMensagemSucesso('Anúncio reativado com sucesso!');
      carregarTudo();
    } catch (err: any) {
      setMensagemErro(err.message || 'Erro ao reativar anúncio.');
    }
  };

  const handleEnviarFoto = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!anuncioFotoId || !arquivoFoto) return;
    setEnviandoFoto(true);
    setMensagemErro(null);

    try {
      await api.uploadFotoAnuncio(anuncioFotoId, arquivoFoto);
      setMensagemSucesso('Foto atualizada com sucesso!');
      setModalFotoAberto(false);
      setArquivoFoto(null);
      carregarTudo();
    } catch (err: any) {
      setMensagemErro(err.message || 'Erro no envio da foto.');
    } finally {
      setEnviandoFoto(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Cabeçalho */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900">Meus Anúncios</h1>
          <p className="text-sm text-slate-500">
            Cota ativa: <strong>{assinatura?.cotaUsada}</strong> de{' '}
            <strong>{assinatura?.ilimitado ? 'Ilimitado' : assinatura?.cotaLimite}</strong> permitidos
          </p>
        </div>

        <button
          onClick={abrirModalNovo}
          className="inline-flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 px-4 rounded-xl shadow text-sm transition active:scale-95"
        >
          <Plus className="w-4 h-4" /> Novo Anúncio
        </button>
      </div>

      {/* Banners de Sucesso e Erro */}
      {mensagemSucesso && (
        <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 p-4 rounded-xl text-sm flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-5 h-5 flex-shrink-0 text-emerald-600" />
            <span>{mensagemSucesso}</span>
          </div>
          <button onClick={() => setMensagemSucesso(null)} className="text-slate-400 hover:text-slate-600">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {mensagemErro && (
        <div className="bg-rose-50 border border-rose-200 text-rose-800 p-4 rounded-xl text-sm flex items-center justify-between">
          <div className="flex items-center gap-2">
            <AlertCircle className="w-5 h-5 flex-shrink-0 text-rose-600" />
            <span>{mensagemErro}</span>
          </div>
          <button onClick={() => setMensagemErro(null)} className="text-slate-400 hover:text-slate-600">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Tabela de Anúncios */}
      {loading ? (
        <LoadingSpinner />
      ) : anuncios.length > 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200 text-slate-600 text-xs uppercase tracking-wider font-semibold">
                  <th className="py-3.5 px-4">Anúncio</th>
                  <th className="py-3.5 px-4">Preço</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4">Validade</th>
                  <th className="py-3.5 px-4 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {anuncios.map((anuncio) => (
                  <tr key={anuncio.id} className="hover:bg-slate-50/70 transition">
                    <td className="py-4 px-4 flex items-center gap-3">
                      <img
                        src={anuncio.fotoUrl || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=150'}
                        alt={anuncio.titulo}
                        className="w-12 h-12 rounded-lg object-cover bg-slate-100 border border-slate-200 flex-shrink-0"
                      />
                      <div>
                        <h2 className="font-bold text-slate-900 leading-snug">{anuncio.titulo}</h2>
                        <span className="text-xs text-slate-500">{anuncio.nomeCategoria || 'Sem categoria'} • {anuncio.bairro}</span>
                      </div>
                    </td>

                    <td className="py-4 px-4 font-bold text-slate-800">
                      {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(anuncio.preco ?? 0)}
                    </td>

                    <td className="py-4 px-4">
                      <span
                        className={`inline-block text-xs font-bold px-2.5 py-1 rounded-full ${
                          anuncio.status === 'ATIVO'
                            ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                            : anuncio.status === 'PAUSADO'
                            ? 'bg-amber-50 text-amber-700 border border-amber-200'
                            : 'bg-rose-50 text-rose-700 border border-rose-200'
                        }`}
                      >
                        {anuncio.status}
                      </span>
                    </td>

                    <td className="py-4 px-4 text-xs text-slate-500">
                      {anuncio.dataExpiracao
                        ? new Date(anuncio.dataExpiracao).toLocaleDateString('pt-BR')
                        : '—'}
                    </td>

                    <td className="py-4 px-4 text-right space-x-1.5 whitespace-nowrap">
                      {anuncio.status === 'ATIVO' ? (
                        <button
                          onClick={() => handlePausar(anuncio.id)}
                          title="Pausar anúncio"
                          className="p-1.5 text-slate-500 hover:text-amber-600 rounded-lg hover:bg-slate-100 transition"
                        >
                          <Pause className="w-4 h-4" />
                        </button>
                      ) : (
                        <button
                          onClick={() => handleReativar(anuncio.id)}
                          title="Reativar anúncio"
                          className="p-1.5 text-slate-500 hover:text-emerald-600 rounded-lg hover:bg-slate-100 transition"
                        >
                          <Play className="w-4 h-4" />
                        </button>
                      )}

                      <button
                        onClick={() => handleRenovar(anuncio.id)}
                        title="Renovar por +15 dias"
                        className="p-1.5 text-slate-500 hover:text-sky-600 rounded-lg hover:bg-slate-100 transition"
                      >
                        <RefreshCw className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() => {
                          setAnuncioFotoId(anuncio.id);
                          setModalFotoAberto(true);
                        }}
                        title="Alterar foto"
                        className="p-1.5 text-slate-500 hover:text-emerald-600 rounded-lg hover:bg-slate-100 transition"
                      >
                        <Image className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() => abrirModalEditar(anuncio)}
                        title="Editar anúncio"
                        className="p-1.5 text-slate-500 hover:text-slate-800 rounded-lg hover:bg-slate-100 transition"
                      >
                        <Edit3 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="text-center py-16 bg-white rounded-2xl border border-slate-200 p-8 space-y-3">
          <p className="text-slate-500 text-sm">Você ainda não tem nenhum anúncio cadastrado.</p>
          <button
            onClick={abrirModalNovo}
            className="inline-flex items-center gap-2 bg-emerald-600 text-white font-semibold px-4 py-2 rounded-xl text-sm shadow"
          >
            <Plus className="w-4 h-4" /> Criar meu Primeiro Anúncio
          </button>
        </div>
      )}

      {/* Modal Criar / Editar Anúncio */}
      {modalAberto && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-xl space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center border-b border-slate-100 pb-3">
              <h2 className="text-lg font-bold text-slate-900">
                {editandoId ? 'Editar Anúncio' : 'Novo Anúncio'}
              </h2>
              <button onClick={() => setModalAberto(false)} className="text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={salvarAnuncio} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Título *</label>
                <input
                  type="text"
                  required
                  value={titulo}
                  onChange={(e) => setTitulo(e.target.value)}
                  placeholder="Ex: Tapioca de Queijo Coalho com Coco"
                  className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Preço (R$) *</label>
                  <input
                    type="text"
                    required
                    value={preco}
                    onChange={(e) => setPreco(e.target.value)}
                    placeholder="12,50"
                    className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Tipo</label>
                  <select
                    value={tipo}
                    onChange={(e) => setTipo(e.target.value as any)}
                    className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  >
                    <option value="PRODUTO">Produto</option>
                    <option value="SERVICO">Serviço</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Categoria</label>
                  <select
                    value={categoriaId}
                    onChange={(e) => setCategoriaId(Number(e.target.value))}
                    className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  >
                    {categorias.map((c) => (
                      <option key={c.id} value={c.id}>{c.nome}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Bairro</label>
                  <input
                    type="text"
                    value={bairro}
                    onChange={(e) => setBairro(e.target.value)}
                    placeholder="Deixe em branco p/ padrão da loja"
                    className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Descrição Detalhada</label>
                <textarea
                  rows={3}
                  value={descricao}
                  onChange={(e) => setDescricao(e.target.value)}
                  placeholder="Conte os detalhes do produto, porção, horário de entrega..."
                  className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                ></textarea>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setModalAberto(false)}
                  className="px-4 py-2 rounded-lg border border-slate-200 text-sm text-slate-600 hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm shadow"
                >
                  {editandoId ? 'Salvar Alterações' : 'Publicar Anúncio'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Upload de Foto */}
      {modalFotoAberto && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl space-y-4">
            <div className="flex justify-between items-center border-b border-slate-100 pb-3">
              <h2 className="text-lg font-bold text-slate-900">Atualizar Foto do Anúncio</h2>
              <button onClick={() => setModalFotoAberto(false)} className="text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleEnviarFoto} className="space-y-4">
              <p className="text-xs text-slate-500">
                Selecione uma imagem no formato JPG, PNG ou WebP de até 5 MB.
              </p>

              <input
                type="file"
                accept="image/jpeg,image/png,image/webp"
                required
                onChange={(e) => setArquivoFoto(e.target.files?.[0] || null)}
                className="w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-xs file:font-semibold file:bg-emerald-50 file:text-emerald-700 hover:file:bg-emerald-100"
              />

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setModalFotoAberto(false)}
                  className="px-4 py-2 rounded-lg border border-slate-200 text-sm text-slate-600 hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={enviandoFoto || !arquivoFoto}
                  className="px-5 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white font-semibold text-sm shadow"
                >
                  {enviandoFoto ? 'Enviando...' : 'Fazer Upload'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default MeusAnuncios;
