package com.isac.marketplace.config;

import com.isac.marketplace.model.Papel;
import com.isac.marketplace.model.Usuario;
import com.isac.marketplace.repository.CategoriaRepository;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final DataSource dataSource;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LojaRepository lojaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            DataSource dataSource,
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository,
            LojaRepository lojaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.dataSource = dataSource;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.lojaRepository = lojaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            log.info("Banco de dados vazio no perfil 'dev'. Carregando dados de demonstração (dados-demo.sql)...");
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("dados-demo.sql"));
                populator.execute(dataSource);
                log.info("Dados de demonstração carregados com sucesso!");
            } catch (Exception e) {
                log.error("Erro ao carregar dados de demonstração: {}", e.getMessage(), e);
            }
        } else {
            log.info("Banco de dados já contém registros. Pulo da carga de demonstração.");
        }

        // Garante a existência do usuário Administrador padrão
        if (usuarioRepository.findByEmail("admin@isac.org.br").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@isac.org.br");
            admin.setSenhaHash(passwordEncoder.encode("admin123"));
            admin.setPapel(Papel.ADMIN);
            admin.setAtivo(true);
            usuarioRepository.save(admin);
            log.info("Usuário ADMIN criado com sucesso: admin@isac.org.br / admin123");
        }

        // Garante a existência do usuário da Tapiocaria da Enseada (Loja 1)
        lojaRepository.findById(1L).ifPresent(loja -> {
            if (usuarioRepository.findByEmail(loja.getEmail()).isEmpty()) {
                Usuario lojaUser = new Usuario();
                lojaUser.setEmail(loja.getEmail());
                lojaUser.setSenhaHash(passwordEncoder.encode("loja123"));
                lojaUser.setPapel(Papel.LOJA);
                lojaUser.setLoja(loja);
                lojaUser.setAtivo(true);
                usuarioRepository.save(lojaUser);
                log.info("Usuário LOJA criado com sucesso: {} / loja123", loja.getEmail());
            }
        });
    }
}
