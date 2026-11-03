package com.isac.marketplace.controller;

import com.isac.marketplace.dto.LoginRequest;
import com.isac.marketplace.dto.LoginResponse;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.Usuario;
import com.isac.marketplace.repository.UsuarioRepository;
import com.isac.marketplace.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String email = request.email() != null ? request.email().trim().toLowerCase() : "";
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraNegocioException("CREDENCIAIS_INVALIDAS", "E-mail ou senha incorretos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new RegraNegocioException("CREDENCIAIS_INVALIDAS", "E-mail ou senha incorretos.");
        }

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new RegraNegocioException("CONTA_INATIVA", "Esta conta foi inativada. Entre em contato com o suporte do ISAC.");
        }

        String token = jwtService.gerarToken(usuario);
        Long lojaId = usuario.getLoja() != null ? usuario.getLoja().getId() : null;
        String nome = usuario.getLoja() != null ? usuario.getLoja().getNomeFantasia() : "Administrador ISAC";

        return ResponseEntity.ok(new LoginResponse(
                token,
                usuario.getEmail(),
                usuario.getPapel(),
                lojaId,
                nome
        ));
    }
}
