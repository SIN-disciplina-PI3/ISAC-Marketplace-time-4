package com.isac.marketplace.service;

import com.isac.marketplace.dto.CadastroLojaDTO;
import com.isac.marketplace.dto.LojaDTO;
import com.isac.marketplace.exception.RecursoNaoEncontradoException;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.repository.UsuarioRepository;
import com.isac.marketplace.util.CnpjValidator;
import com.isac.marketplace.util.CpfValidator;
import com.isac.marketplace.util.TelefoneUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LojaService {

    private final LojaRepository lojaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public LojaService(
            LojaRepository lojaRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            Clock clock
    ) {
        this.lojaRepository = lojaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    public Loja cadastrar(CadastroLojaDTO dto) {
        if (dto.nomeFantasia() == null || dto.nomeFantasia().isBlank()) {
            throw new RegraNegocioException("NOME_OBRIGATORIO", "O nome fantasia da loja é obrigatório.");
        }
        if (dto.bairro() == null || dto.bairro().isBlank()) {
            throw new RegraNegocioException("BAIRRO_OBRIGATORIO", "O bairro da loja é obrigatório.");
        }
        if (dto.telefoneWhatsapp() == null || dto.telefoneWhatsapp().isBlank()) {
            throw new RegraNegocioException("TELEFONE_OBRIGATORIO", "O telefone de WhatsApp é obrigatório.");
        }

        String telefoneLimpo = TelefoneUtil.normalizar(dto.telefoneWhatsapp());
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            throw new RegraNegocioException("TELEFONE_INVALIDO", "O telefone de WhatsApp deve conter DDD + número (10 ou 11 dígitos).");
        }

        if (lojaRepository.existsByTelefoneWhatsapp(telefoneLimpo)) {
            throw new RegraNegocioException("TELEFONE_DUPLICADO", "Já existe uma loja cadastrada com este telefone de WhatsApp.");
        }

        String docLimpo = null;
        TipoDocumento tipoDoc = dto.tipoDocumento();

        if (dto.documento() != null && !dto.documento().isBlank()) {
            docLimpo = dto.documento().replaceAll("\\D", "");

            if (docLimpo.length() == 11) {
                if (!CpfValidator.isValid(docLimpo)) {
                    throw new RegraNegocioException("CPF_INVALIDO", "O CPF informado é inválido.");
                }
                tipoDoc = TipoDocumento.CPF;
            } else if (docLimpo.length() == 14) {
                if (!CnpjValidator.isValid(docLimpo)) {
                    throw new RegraNegocioException("CNPJ_INVALIDO", "O CNPJ informado é inválido.");
                }
                tipoDoc = TipoDocumento.CNPJ;
            } else {
                throw new RegraNegocioException("DOCUMENTO_INVALIDO", "O documento deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos) válido.");
            }

            if (lojaRepository.existsByDocumento(docLimpo)) {
                throw new RegraNegocioException("DOCUMENTO_DUPLICADO", "Já existe uma loja cadastrada com este documento.");
            }
        }

        Loja loja = new Loja();
        loja.setNomeFantasia(dto.nomeFantasia().trim());
        loja.setDocumento(docLimpo);
        loja.setTipoDocumento(tipoDoc);
        loja.setCategoria(dto.categoria() != null ? dto.categoria().trim() : null);
        loja.setTelefoneWhatsapp(telefoneLimpo);
        loja.setEmail(dto.email() != null ? dto.email().trim().toLowerCase() : null);
        loja.setBairro(dto.bairro().trim());
        loja.setEndereco(dto.endereco() != null ? dto.endereco().trim() : null);
        loja.setPlano(PlanoLoja.GRATUITO);
        loja.setStatusAssinatura(StatusAssinatura.INATIVA);
        loja.setDataVencimentoAssinatura(null);
        loja.setDataCadastro(LocalDateTime.now(clock));

        Loja salva = lojaRepository.save(loja);

        // Se informou e-mail e senha, cria o usuário de acesso para a loja
        if (salva.getEmail() != null && !salva.getEmail().isBlank() && dto.senha() != null && !dto.senha().isBlank()) {
            if (usuarioRepository.existsByEmail(salva.getEmail())) {
                throw new RegraNegocioException("EMAIL_DUPLICADO", "Já existe um usuário cadastrado com este e-mail.");
            }
            Usuario usuario = new Usuario();
            usuario.setEmail(salva.getEmail());
            usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
            usuario.setPapel(Papel.LOJA);
            usuario.setLoja(salva);
            usuario.setAtivo(true);
            usuarioRepository.save(usuario);
        }

        return salva;
    }

    /**
     * Cadastro simplificado via WhatsApp (apenas nome, telefone e bairro obrigatórios).
     */
    public Loja cadastrarViaWhatsApp(String nome, String telefone, String bairro) {
        return cadastrar(new CadastroLojaDTO(
                nome,
                null,
                null,
                null,
                telefone,
                null,
                bairro,
                null,
                null
        ));
    }

    @Transactional(readOnly = true)
    public Loja buscarPorId(Long id) {
        return lojaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("LOJA_NAO_ENCONTRADA", "Loja com ID " + id + " não encontrada."));
    }

    @Transactional(readOnly = true)
    public Optional<Loja> buscarPorTelefone(String telefone) {
        String normalizado = TelefoneUtil.normalizar(telefone);
        return lojaRepository.findByTelefoneWhatsapp(normalizado);
    }

    @Transactional(readOnly = true)
    public Optional<Loja> buscarPorDocumento(String documento) {
        String digitos = documento.replaceAll("\\D", "");
        return lojaRepository.findByDocumento(digitos);
    }

    @Transactional(readOnly = true)
    public List<Loja> listarTodas(StatusAssinatura status) {
        if (status != null) {
            return lojaRepository.findByStatusAssinatura(status);
        }
        return lojaRepository.findAll();
    }

    public Loja atualizar(Long id, CadastroLojaDTO dto) {
        Loja loja = buscarPorId(id);

        if (dto.nomeFantasia() != null && !dto.nomeFantasia().isBlank()) {
            loja.setNomeFantasia(dto.nomeFantasia().trim());
        }
        if (dto.bairro() != null && !dto.bairro().isBlank()) {
            loja.setBairro(dto.bairro().trim());
        }
        if (dto.categoria() != null) {
            loja.setCategoria(dto.categoria().trim());
        }
        if (dto.endereco() != null) {
            loja.setEndereco(dto.endereco().trim());
        }

        // Se forneceu documento e era nulo, valida e atualiza
        if (dto.documento() != null && !dto.documento().isBlank()) {
            String docLimpo = dto.documento().replaceAll("\\D", "");
            if (!docLimpo.equals(loja.getDocumento())) {
                if (docLimpo.length() == 11) {
                    if (!CpfValidator.isValid(docLimpo)) {
                        throw new RegraNegocioException("CPF_INVALIDO", "CPF inválido.");
                    }
                    loja.setTipoDocumento(TipoDocumento.CPF);
                } else if (docLimpo.length() == 14) {
                    if (!CnpjValidator.isValid(docLimpo)) {
                        throw new RegraNegocioException("CNPJ_INVALIDO", "CNPJ inválido.");
                    }
                    loja.setTipoDocumento(TipoDocumento.CNPJ);
                } else {
                    throw new RegraNegocioException("DOCUMENTO_INVALIDO", "Documento deve ser CPF ou CNPJ válido.");
                }
                if (lojaRepository.existsByDocumento(docLimpo)) {
                    throw new RegraNegocioException("DOCUMENTO_DUPLICADO", "Documento já cadastrado em outra loja.");
                }
                loja.setDocumento(docLimpo);
            }
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            loja.setEmail(dto.email().trim().toLowerCase());
        }

        return lojaRepository.save(loja);
    }
}
