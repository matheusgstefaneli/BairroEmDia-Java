package br.com.fiap.bairroemdia.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/** Base dos testes de API: sobe o contexto Spring completo (perfil "test" = H2 em memória). */
@SpringBootTest
@ActiveProfiles("test")
abstract class ApiTestBase {

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mvc;

    @BeforeEach
    void configurarMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    protected MockHttpServletResponse enviar(MockHttpServletRequestBuilder req) throws Exception {
        return mvc.perform(req).andReturn().getResponse();
    }

    protected MockHttpServletResponse doGet(String url) throws Exception {
        return enviar(get(url));
    }

    protected MockHttpServletResponse doPost(String url, String json) throws Exception {
        return enviar(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected MockHttpServletResponse doPut(String url, String json) throws Exception {
        return enviar(put(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected MockHttpServletResponse doPatch(String url, String json) throws Exception {
        return enviar(patch(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected MockHttpServletResponse doDelete(String url) throws Exception {
        return enviar(delete(url));
    }

    protected String corpo(MockHttpServletResponse r) throws Exception {
        return r.getContentAsString(StandardCharsets.UTF_8);
    }

    /** Extrai o primeiro campo "id" do JSON retornado. */
    protected long extrairId(String json) {
        Matcher m = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(json);
        if (!m.find()) {
            throw new IllegalStateException("Campo id não encontrado em: " + json);
        }
        return Long.parseLong(m.group(1));
    }

    protected String unico(String prefixo) {
        return prefixo + System.nanoTime();
    }

    protected long criarMorador() throws Exception {
        String json = "{\"nome\":\"Morador Teste\",\"email\":\"" + unico("m") + "@example.com\"}";
        MockHttpServletResponse r = doPost("/moradores", json);
        return extrairId(corpo(r));
    }

    protected long criarCategoria() throws Exception {
        String json = "{\"nome\":\"" + unico("Cat") + "\",\"descricao\":\"Categoria de teste\"}";
        MockHttpServletResponse r = doPost("/categorias", json);
        return extrairId(corpo(r));
    }

    protected String jsonOcorrencia(long moradorId, long categoriaId, int prioridade) {
        return "{\"titulo\":\"Buraco na rua\",\"descricao\":\"Buraco grande\",\"localizacao\":\"Rua A, 10\","
                + "\"prioridade\":" + prioridade + ",\"moradorId\":" + moradorId + ",\"categoriaId\":" + categoriaId + "}";
    }

    protected long criarOcorrencia(long moradorId, long categoriaId) throws Exception {
        MockHttpServletResponse r = doPost("/ocorrencias", jsonOcorrencia(moradorId, categoriaId, 3));
        return extrairId(corpo(r));
    }
}
