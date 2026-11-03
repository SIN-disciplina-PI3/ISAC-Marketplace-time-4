package com.isac.marketplace.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.dto.LoginRequest;
import com.isac.marketplace.model.Papel;
import com.isac.marketplace.model.Usuario;
import com.isac.marketplace.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Segurança e Autorização (Spring Security + JWT)")
class SegurancaAutorizacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Rotas públicas devem ser acessíveis sem autenticação")
    void rotasPublicasDevemEstarAbertas() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/busca?termo=tapioca"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Rotas de admin devem retornar 403 quando não autenticado")
    void rotasAdminDevemRetornar403SemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/admin/lojas"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/metricas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Rotas de admin devem retornar 403 quando acessadas por usuário com papel LOJA")
    void rotasAdminDevemRetornar403ParaPapelLoja() throws Exception {
        Usuario usuarioLoja = new Usuario(null, "lojista@teste.local", passwordEncoder.encode("senha123"), Papel.LOJA, null, true);
        usuarioLoja = usuarioRepository.save(usuarioLoja);
        String tokenLoja = jwtService.gerarToken(usuarioLoja);

        mockMvc.perform(get("/api/admin/lojas")
                        .header("Authorization", "Bearer " + tokenLoja))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Rotas de admin devem permitir acesso com token JWT de ADMIN")
    void rotasAdminDevemPermitirAcessoComTokenAdmin() throws Exception {
        Usuario usuarioAdmin = new Usuario(null, "admin_sec@isac.org.br", passwordEncoder.encode("senha123"), Papel.ADMIN, null, true);
        usuarioAdmin = usuarioRepository.save(usuarioAdmin);
        String tokenAdmin = jwtService.gerarToken(usuarioAdmin);

        mockMvc.perform(get("/api/admin/lojas")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Rota de login deve permitir autenticação pública e emitir token funcional")
    void loginPublicoDeveEmitirToken() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario_login_teste@isac.org.br");
        usuario.setSenhaHash(passwordEncoder.encode("senhaCorreta123"));
        usuario.setPapel(Papel.ADMIN);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        LoginRequest loginRequest = new LoginRequest("usuario_login_teste@isac.org.br", "senhaCorreta123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
}
