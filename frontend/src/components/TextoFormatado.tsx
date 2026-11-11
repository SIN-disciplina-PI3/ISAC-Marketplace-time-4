import React from 'react';

interface TextoFormatadoProps {
  texto?: string;
  className?: string;
}

/**
 * Converte texto com quebras de linha (\n\n ou \n) em elementos <p> e <ul>/<li> semânticos,
 * interpretando marcações de negrito (**texto**).
 */
export const TextoFormatado: React.FC<TextoFormatadoProps> = ({ texto = '', className = '' }) => {
  if (!texto) return null;

  // Divide o texto por blocos separados por quebras duplas de linha
  const blocos = texto.split(/\n\s*\n/).map((b) => b.trim()).filter(Boolean);

  const formatarLinhaComNegrito = (linha: string) => {
    // Processa **negrito**
    const partes = linha.split(/(\*\*.*?\*\*)/g);
    return partes.map((parte, i) => {
      if (parte.startsWith('**') && parte.endsWith('**')) {
        return <strong key={i} className="font-bold text-slate-900">{parte.slice(2, -2)}</strong>;
      }
      return parte;
    });
  };

  return (
    <div className={`space-y-4 ${className}`}>
      {blocos.map((bloco, idx) => {
        // Se todas as linhas do bloco começam com marcador de lista (- ou *)
        const linhas = bloco.split('\n').map((l) => l.trim()).filter(Boolean);
        const ehLista = linhas.length > 0 && linhas.every((l) => /^[-*•]\s+/.test(l));

        if (ehLista) {
          return (
            <ul key={idx} className="list-disc list-inside space-y-1.5 pl-2 text-slate-700">
              {linhas.map((l, lIdx) => {
                const conteudoItem = l.replace(/^[-*•]\s+/, '');
                return (
                  <li key={lIdx} className="leading-relaxed">
                    {formatarLinhaComNegrito(conteudoItem)}
                  </li>
                );
              })}
            </ul>
          );
        }

        // Se for parágrafo normal, pode ter quebra de linha simples interna
        return (
          <p key={idx} className="leading-relaxed text-slate-700">
            {linhas.map((linha, lIdx) => (
              <React.Fragment key={lIdx}>
                {lIdx > 0 && <br />}
                {formatarLinhaComNegrito(linha)}
              </React.Fragment>
            ))}
          </p>
        );
      })}
    </div>
  );
};

export default TextoFormatado;
