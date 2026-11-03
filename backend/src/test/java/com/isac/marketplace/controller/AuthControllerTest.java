package com.isac.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.dto.LoginRequest;
import com.isac.marketplace.exception.GlobalExceptionHandler;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Papel;
import com.isac.marketplace.model.Usuario;
import com.isac.marketplace.repository.UsuarioRepository;
import com.isac.marketplace.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AuthController")
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(usuarioRepository, passwordEncoder, jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve autenticar com sucesso e retornar token")
    void deveAutenticarComSucesso() throws Exception {
        Loja loja = new Loja();
        loja.setId(10L);
        loja.setNomeFantasia("Tapiocaria Maravilha");

        Usuario usuario = new Usuario(1L, "loja@isac.org.br", "hashed_pwd", Papel.LOJA, loja, true);

        when(usuarioRepository.findByEmail("loja@isac.org.br")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "hashed_pwd")).thenReturn(true);
        when(jwtService.gerarToken(usuario)).thenReturn("mocked.jwt.token");

        LoginRequest request = new LoginRequest("loja@isac.org.br", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.email").value("loja@isac.org.br"))
                .andExpect(jsonPath("$.papel").value("LOJA"))
                .andExpect(jsonPath("$.lojaId").value(10))
                .andExpect(jsonPath("$.nome").value("Tapiocaria Maravilha"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve rejeitar senha incorreta")
    void deveRejeitarSenhaIncorreta() throws Exception {
        Usuario usuario = new Usuario(1L, "loja@isac.org.br", "hashed_pwd", Papel.LOJA, null, true);

        when(usuarioRepository.findByEmail("loja@isac.org.br")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha_errada", "hashed_pwd")).thenReturn(false);

        LoginRequest request = new LoginRequest("loja@isac.org.br", "senha_errada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CREDENCIAIS_INVALIDAS"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve rejeitar e-mail não cadastrado")
    void deveRejeitarEmailNaoCadastrado() throws Exception {
        when(usuarioRepository.findByEmail("inexistente@isac.org.br")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("inexistente@isac.org.br", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CREDENCIAIS_INVALIDAS"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve rejeitar conta inativa")
    void deveRejeitarContaInativa() throws Exception {
        Usuario usuario = new Usuario(1L, "inativo@isac.org.br", "hashed_pwd", Papel.LOJA, null, false);

        when(usuarioRepository.findByEmail("inativo@isac.org.br")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "hashed_pwd")).thenReturn(true);

        LoginRequest request = new LoginRequest("inativo@isac.org.br", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CONTA_INATIVA"));
    }
}
