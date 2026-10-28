package com.isac.marketplace.service;

import com.isac.marketplace.dto.CadastroLojaDTO;
import com.isac.marketplace.exception.RecursoNaoEncontradoException;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do LojaService")
class LojaServiceTest {

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Clock fixedClock;
    private LojaService lojaService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-28T10:00:00Z"), ZoneId.of("America/Recife"));
        lojaService = new LojaService(lojaRepository, usuarioRepository, passwordEncoder, fixedClock);
    }

    @Test
    @DisplayName("Deve cadastrar loja com sucesso com CNPJ válido")
    void deveCadastrarLojaComSucesso() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Tapiocaria da Enseada",
                "11.222.333/0001-81",
                TipoDocumento.CNPJ,
                "Alimentação",
                "81999990001",
                "contato@tapiocaria.local",
                "Enseada dos Corais",
                "Av Beira Mar, 10",
                null
        );

        when(lojaRepository.existsByTelefoneWhatsapp("81999990001")).thenReturn(false);
        when(lojaRepository.existsByDocumento("11222333000181")).thenReturn(false);
        when(lojaRepository.save(any(Loja.class))).thenAnswer(inv -> inv.getArgument(0));

        Loja salva = lojaService.cadastrar(dto);

        assertNotNull(salva);
        assertEquals("Tapiocaria da Enseada", salva.getNomeFantasia());
        assertEquals("11222333000181", salva.getDocumento());
        assertEquals(PlanoLoja.GRATUITO, salva.getPlano());
        assertEquals(StatusAssinatura.INATIVA, salva.getStatusAssinatura());
        assertNull(salva.getDataVencimentoAssinatura());
        verify(lojaRepository).save(any(Loja.class));
    }

    @Test
    @DisplayName("Deve rejeitar cadastro sem nome fantasia")
    void deveRejeitarCadastroSemNomeFantasia() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "",
                "11.222.333/0001-81",
                TipoDocumento.CNPJ,
                "Alimentação",
                "81999990001",
                null,
                "Enseada dos Corais",
                null,
                null
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> lojaService.cadastrar(dto));
        assertEquals("NOME_OBRIGATORIO", ex.getCodigo());
        verifyNoInteractions(lojaRepository);
    }

    @Test
    @DisplayName("Deve rejeitar cadastro sem bairro")
    void deveRejeitarCadastroSemBairro() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Loja Teste",
                null,
                null,
                null,
                "81999990001",
                null,
                "   ",
                null,
                null
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> lojaService.cadastrar(dto));
        assertEquals("BAIRRO_OBRIGATORIO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com telefone inválido (menos de 10 dígitos)")
    void deveRejeitarCadastroComTelefoneInvalido() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Loja Teste",
                null,
                null,
                null,
                "12345",
                null,
                "Gaibu",
                null,
                null
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> lojaService.cadastrar(dto));
        assertEquals("TELEFONE_INVALIDO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com telefone já existente")
    void deveRejeitarCadastroComTelefoneDuplicado() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Loja Nova",
                null,
                null,
                null,
                "81999990001",
                null,
                "Enseada dos Corais",
                null,
                null
        );

        when(lojaRepository.existsByTelefoneWhatsapp("81999990001")).thenReturn(true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> lojaService.cadastrar(dto));
        assertEquals("TELEFONE_DUPLICADO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com CNPJ inválido")
    void deveRejeitarCadastroComCnpjInvalido() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Loja Inválida",
                "11.222.333/0001-82", // DV incorreto
                TipoDocumento.CNPJ,
                null,
                "81999990001",
                null,
                "Enseada dos Corais",
                null,
                null
        );

        when(lojaRepository.existsByTelefoneWhatsapp("81999990001")).thenReturn(false);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> lojaService.cadastrar(dto));
        assertEquals("CNPJ_INVALIDO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com CPF inválido")
    void deveRejeitarCadastroComCpfInvalido() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Loja Artesã",
                "529.982.247-00", // CPF incorreto
                TipoDocumento.CPF,
                null,
                "81999990001",
                null,
                "Enseada dos Corais",
                null,
                null
        );

        when(lojaRepository.existsByTelefoneWhatsapp("81999990001")).thenReturn(false);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> lojaService.cadastrar(dto));
        assertEquals("CPF_INVALIDO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com documento já existente em outra loja")
    void deveRejeitarCadastroComDocumentoDuplicado() {
        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Outra Loja",
                "529.982.247-25", // CPF válido
                TipoDocumento.CPF,
                null,
                "81999990001",
                null,
                "Gaibu",
                null,
                null
        );

        when(lojaRepository.existsByTelefoneWhatsapp("81999990001")).thenReturn(false);
        when(lojaRepository.existsByDocumento("52998224725")).thenReturn(true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> lojaService.cadastrar(dto));
        assertEquals("DOCUMENTO_DUPLICADO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve permitir cadastro simplificado via WhatsApp sem documento")
    void devePermitirCadastroSimplificadoViaWhatsApp() {
        when(lojaRepository.existsByTelefoneWhatsapp("81999990005")).thenReturn(false);
        when(lojaRepository.save(any(Loja.class))).thenAnswer(inv -> inv.getArgument(0));

        Loja salva = lojaService.cadastrarViaWhatsApp("Peixaria do Zé", "81999990005", "Enseada dos Corais");

        assertNotNull(salva);
        assertEquals("Peixaria do Zé", salva.getNomeFantasia());
        assertNull(salva.getDocumento());
        assertEquals("81999990005", salva.getTelefoneWhatsapp());
        assertEquals("Enseada dos Corais", salva.getBairro());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar por ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(lojaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> lojaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve atualizar dados da loja com sucesso")
    void deveAtualizarDadosDaLojaComSucesso() {
        Loja lojaExistente = new Loja();
        lojaExistente.setId(1L);
        lojaExistente.setNomeFantasia("Nome Antigo");
        lojaExistente.setBairro("Enseada dos Corais");
        lojaExistente.setTelefoneWhatsapp("81999990001");

        when(lojaRepository.findById(1L)).thenReturn(Optional.of(lojaExistente));
        when(lojaRepository.save(any(Loja.class))).thenAnswer(inv -> inv.getArgument(0));

        CadastroLojaDTO dto = new CadastroLojaDTO(
                "Nome Atualizado",
                null,
                null,
                "Nova Categoria",
                "81999990001",
                "novo@email.com",
                "Gaibu",
                "Rua Nova, 123",
                null
        );

        Loja atualizada = lojaService.atualizar(1L, dto);

        assertEquals("Nome Atualizado", atualizada.getNomeFantasia());
        assertEquals("Gaibu", atualizada.getBairro());
        assertEquals("Nova Categoria", atualizada.getCategoria());
    }
}
