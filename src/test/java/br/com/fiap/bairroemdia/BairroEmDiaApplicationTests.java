package br.com.fiap.bairroemdia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BairroEmDiaApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextoInicializa() {
        assertThat(context).isNotNull();
        assertThat(context.containsBean("moradorController")).isTrue();
        assertThat(context.containsBean("ocorrenciaBo")).isTrue();
        assertThat(context.containsBean("categoriaDao")).isTrue();
    }
}
