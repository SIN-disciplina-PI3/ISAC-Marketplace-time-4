import React, { createContext, useContext, useState, useEffect } from 'react';
import { UsuarioAutenticado } from '../types';
import { api } from '../services/api';

interface AuthContextType {
  usuario: UsuarioAutenticado | null;
  loading: boolean;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => void;
  isLoja: boolean;
  isAdmin: boolean;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [usuario, setUsuario] = useState<UsuarioAutenticado | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    try {
      const token = localStorage.getItem('isac_token');
      const email = localStorage.getItem('isac_email');
      const papel = localStorage.getItem('isac_papel') as any;
      const lojaId = localStorage.getItem('isac_loja_id');
      const nome = localStorage.getItem('isac_nome') || undefined;

      if (token && email && papel) {
        setUsuario({
          token,
          email,
          papel,
          lojaId: lojaId ? Number(lojaId) : null,
          nome,
        });
      }
    } catch {
      // LocalStorage indisponível ou inválido
    } finally {
      setLoading(false);
    }
  }, []);

  const login = async (email: string, senha: string) => {
    const authData = await api.login(email, senha);
    setUsuario(authData);
    localStorage.setItem('isac_token', authData.token);
    localStorage.setItem('isac_email', authData.email);
    localStorage.setItem('isac_papel', authData.papel);
    if (authData.lojaId) {
      localStorage.setItem('isac_loja_id', String(authData.lojaId));
    } else {
      localStorage.removeItem('isac_loja_id');
    }
    if (authData.nome) {
      localStorage.setItem('isac_nome', authData.nome);
    }
  };

  const logout = () => {
    setUsuario(null);
    localStorage.removeItem('isac_token');
    localStorage.removeItem('isac_email');
    localStorage.removeItem('isac_papel');
    localStorage.removeItem('isac_loja_id');
    localStorage.removeItem('isac_nome');
  };

  const isLoja = usuario?.papel === 'LOJA';
  const isAdmin = usuario?.papel === 'ADMIN';
  const isAuthenticated = !!usuario;

  return (
    <AuthContext.Provider
      value={{
        usuario,
        loading,
        login,
        logout,
        isLoja,
        isAdmin,
        isAuthenticated,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};
