package com.isac.marketplace.config;

import com.isac.marketplace.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final DataSource dataSource;
    private final CategoriaRepository categoriaRepository;

    public DataLoader(DataSource dataSource, CategoriaRepository categoriaRepository) {
        this.dataSource = dataSource;
        this.categoriaRepository = categoriaRepository;
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
    }
}
