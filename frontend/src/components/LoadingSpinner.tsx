import React from 'react';

interface LoadingSpinnerProps {
  texto?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ texto = 'Carregando informações...' }) => {
  return (
    <div className="flex flex-col items-center justify-center p-8 space-y-3" role="status" aria-live="polite">
      <div className="w-10 h-10 border-4 border-isac-green border-t-transparent rounded-full animate-spin"></div>
      <span className="text-sm text-slate-600 font-medium">{texto}</span>
    </div>
  );
};
