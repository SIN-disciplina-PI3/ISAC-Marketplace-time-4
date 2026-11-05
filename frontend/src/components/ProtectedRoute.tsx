import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { PapelUsuario } from '../types';
import { LoadingSpinner } from './LoadingSpinner';

interface ProtectedRouteProps {
  children: React.ReactNode;
  /** Papel único (legado) ou lista de papéis permitidos */
  papelRequerido?: PapelUsuario;
  papeis?: PapelUsuario[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  papelRequerido,
  papeis,
}) => {
  const { usuario, loading: carregando } = useAuth();
  const location = useLocation();

  if (carregando) {
    return <LoadingSpinner />;
  }

  if (!usuario) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Constrói a lista de papéis permitidos combinando ambas as props
  const papeisPermitidos: PapelUsuario[] = [
    ...(papeis ?? []),
    ...(papelRequerido ? [papelRequerido] : []),
  ];

  if (papeisPermitidos.length > 0 && !papeisPermitidos.includes(usuario.papel)) {
    if (usuario.papel === 'ADMIN') {
      return <Navigate to="/admin" replace />;
    }
    return <Navigate to="/loja" replace />;
  }

  return <>{children}</>;
};
