package br.com.fiap.bairroemdia.oracle;

import br.com.fiap.bairroemdia.dao.MoradorDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de persistência no Oracle REAL da FIAP.
 * Só executa quando as variáveis DB_URL, DB_USER e DB_PASSWORD estiverem definidas
 * e os scripts da pasta /sql já tiverem sido executados no banco.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class OracleConexaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MoradorDao moradorDao;

    @Test
    void conectaNoOracle() {
        Integer um = jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
        assertThat(um).isEqualTo(1);
    }

    @Test
    void consultaTabelaDeMoradores() {
        assertThat(moradorDao.listarTodos()).isNotNull();
    }
}
