package br.com.fiap.bairroemdia.api;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class MoradorApiTest extends ApiTestBase {

    @Test
    void criaEBuscaPorId() throws Exception {
        String email = unico("ana") + "@example.com";
        MockHttpServletResponse criado = doPost("/moradores", "{\"nome\":\"Ana\",\"email\":\"" + email + "\"}");
        assertThat(criado.getStatus()).isEqualTo(201);
        assertThat(criado.getHeader("Location")).contains("/moradores/");
        long id = extrairId(corpo(criado));

        MockHttpServletResponse lido = doGet("/moradores/" + id);
        assertThat(lido.getStatus()).isEqualTo(200);
        assertThat(corpo(lido)).contains("\"nome\":\"Ana\"").contains(email);
    }

    @Test
    void listaMoradores() throws Exception {
        long id = criarMorador();
        MockHttpServletResponse lista = doGet("/moradores");
        assertThat(lista.getStatus()).isEqualTo(200);
        assertThat(corpo(lista)).contains("\"id\":" + id);
    }

    @Test
    void atualizaMorador() throws Exception {
        long id = criarMorador();
        String email = unico("novo") + "@example.com";
        MockHttpServletResponse r = doPut("/moradores/" + id, "{\"nome\":\"Nome Novo\",\"email\":\"" + email + "\"}");
        assertThat(r.getStatus()).isEqualTo(200);
        assertThat(corpo(r)).contains("Nome Novo").contains(email);
    }

    @Test
    void excluiMoradorEDepoisRetorna404() throws Exception {
        long id = criarMorador();
        assertThat(doDelete("/moradores/" + id).getStatus()).isEqualTo(204);
        assertThat(doGet("/moradores/" + id).getStatus()).isEqualTo(404);
    }

    @Test
    void nomeVazioRetorna400ComDetalhe() throws Exception {
        MockHttpServletResponse r = doPost("/moradores", "{\"nome\":\"\",\"email\":\"a@example.com\"}");
        assertThat(r.getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("nome:");
    }

    @Test
    void emailInvalidoRetorna400() throws Exception {
        MockHttpServletResponse r = doPost("/moradores", "{\"nome\":\"Ana\",\"email\":\"nao-e-email\"}");
        assertThat(r.getStatus()).isEqualTo(400);
        assertThat(corpo(r)).contains("email:");
    }

    @Test
    void emailDuplicadoRetorna409() throws Exception {
        String json = "{\"nome\":\"Ana\",\"email\":\"" + unico("dup") + "@example.com\"}";
        assertThat(doPost("/moradores", json).getStatus()).isEqualTo(201);
        assertThat(doPost("/moradores", json).getStatus()).isEqualTo(409);
    }

    @Test
    void idInexistenteRetorna404() throws Exception {
        MockHttpServletResponse r = doGet("/moradores/999999999");
        assertThat(r.getStatus()).isEqualTo(404);
        assertThat(corpo(r)).contains("\"status\":404");
    }

    @Test
    void idNaoNumericoRetorna400() throws Exception {
        assertThat(doGet("/moradores/abc").getStatus()).isEqualTo(400);
    }

    @Test
    void jsonMalformadoRetorna400() throws Exception {
        assertThat(doPost("/moradores", "{nome:").getStatus()).isEqualTo(400);
    }

    @Test
    void naoExcluiMoradorComOcorrencias() throws Exception {
        long moradorId = criarMorador();
        long categoriaId = criarCategoria();
        criarOcorrencia(moradorId, categoriaId);
        assertThat(doDelete("/moradores/" + moradorId).getStatus()).isEqualTo(409);
    }
}
