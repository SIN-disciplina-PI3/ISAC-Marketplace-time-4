import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Store, CheckCircle2, AlertCircle } from 'lucide-react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';

export const CadastroLoja: React.FC = () => {
  const [nomeFantasia, setNomeFantasia] = useState('');
  const [documento, setDocumento] = useState('');
  const [categoria, setCategoria] = useState('');
  const [telefoneWhatsapp, setTelefoneWhatsapp] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [bairro, setBairro] = useState('Enseada dos Corais');
  const [endereco, setEndereco] = useState('');

  const [erro, setErro] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();

  const bairros = [
    'Enseada dos Corais',
    'Gaibu',
    'Paiva',
    'Suape',
    'Cabo Centro',
    'Nazaré',
    'Outro',
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);
    setSubmitting(true);

    try {
      await api.cadastrarLoja({
        nomeFantasia,
        documento: documento.trim() ? documento.trim() : null,
        categoria: categoria.trim() ? categoria.trim() : null,
        telefoneWhatsapp,
        email: email.trim() ? email.trim() : null,
        senha: senha.trim() ? senha.trim() : null,
        bairro,
        endereco: endereco.trim() ? endereco.trim() : null,
      });

      // Se informou e-mail e senha, já loga automaticamente
      if (email && senha) {
        await login(email, senha);
        navigate('/loja', { replace: true });
      } else {
        navigate('/login', { replace: true });
      }
    } catch (err: any) {
      setErro(err.message || 'Erro ao realizar cadastro da loja.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto px-4 py-12">
      <div className="bg-white rounded-2xl border border-slate-200 p-8 shadow-sm space-y-6">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 bg-emerald-100 text-emerald-700 rounded-xl flex items-center justify-center mx-auto">
            <Store className="w-6 h-6" />
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900">Cadastre seu Negócio</h1>
          <p className="text-sm text-slate-500">
            Junte-se ao marketplace comunitário do ISAC. Grátis para começar!
          </p>
        </div>

        {erro && (
          <div className="bg-rose-50 border border-rose-200 text-rose-700 p-3.5 rounded-xl text-sm flex items-center gap-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0" />
            <span>{erro}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
              Nome Fantasia / Nome do Negócio *
            </label>
            <input
              type="text"
              required
              value={nomeFantasia}
              onChange={(e) => setNomeFantasia(e.target.value)}
              placeholder="Ex: Tapiocaria da Enseada ou Artesanato da Ilha"
              className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
                WhatsApp com DDD (apenas números) *
              </label>
              <input
                type="tel"
                required
                value={telefoneWhatsapp}
                onChange={(e) => setTelefoneWhatsapp(e.target.value)}
                placeholder="Ex: 81999990001"
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
                Bairro *
              </label>
              <select
                value={bairro}
                onChange={(e) => setBairro(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
              >
                {bairros.map((b) => (
                  <option key={b} value={b}>
                    {b}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
                Categoria Principal
              </label>
              <input
                type="text"
                value={categoria}
                onChange={(e) => setCategoria(e.target.value)}
                placeholder="Ex: Alimentação, Artesanato, Serviços"
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
                CPF ou CNPJ (opcional)
              </label>
              <input
                type="text"
                value={documento}
                onChange={(e) => setDocumento(e.target.value)}
                placeholder="Apenas dígitos"
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
              Endereço / Ponto de Referência (opcional)
            </label>
            <input
              type="text"
              value={endereco}
              onChange={(e) => setEndereco(e.target.value)}
              placeholder="Ex: Av. Beira Mar, 120"
              className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <div className="border-t border-slate-100 pt-4">
            <h2 className="text-sm font-bold text-slate-800 mb-2">Dados de Acesso ao Site</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
                  E-mail de Login *
                </label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="seu-negocio@exemplo.com"
                  className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
                  Crie uma Senha *
                </label>
                <input
                  type="password"
                  required
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  placeholder="Mínimo 6 caracteres"
                  className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white font-bold py-3 px-4 rounded-xl shadow transition text-sm active:scale-[0.98]"
          >
            {submitting ? 'Cadastrando Loja...' : 'Concluir Cadastro Gratuito'}
          </button>
        </form>

        <div className="border-t border-slate-100 pt-4 text-center text-xs text-slate-500">
          Já tem conta? <Link to="/login" className="font-bold text-emerald-600 hover:text-emerald-700">Faça login</Link>
        </div>
      </div>
    </div>
  );
};

export default CadastroLoja;
