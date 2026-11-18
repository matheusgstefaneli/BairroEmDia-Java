package br.com.fiap.bairroemdia.api;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class OcorrenciaApiTest extends ApiTestBase {

    @Test
    void criaOcorrenciaComRelacionamentos() throws Exception {
        long moradorId = criarMorador();
        long categoriaId = criarCategoria();
        MockHttpServletResponse r = doPost("/ocorrencias", jsonOcorrencia(moradorId, categoriaId, 4));
        assertThat(r.getStatus()).isEqualTo(201);
        String corpo = corpo(r);
        assertThat(corpo).contains("\"status\":\"ABERTA\"")
                .contains("\"prioridade\":4")
                .contains("\"moradorId\":" + moradorId)
                .contains("\"categoriaId\":" + categoriaId)
                .contains("dataCriacao");
    }

    @Test
    void buscaEListaOcorrencias() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        assertThat(doGet("/ocorrencias/" + id).getStatus()).isEqualTo(200);
        assertThat(corpo(doGet("/ocorrencias"))).contains("\"id\":" + id);
    }

    @Test
    void prioridadeForaDaFaixaRetorna400() throws Exception {
        long m = criarMorador();
        long c = criarCategoria();
        assertThat(doPost("/ocorrencias", jsonOcorrencia(m, c, 6)).getStatus()).isEqualTo(400);
        assertThat(doPost("/ocorrencias", jsonOcorrencia(m, c, 0)).getStatus()).isEqualTo(400);
    }

    @Test
    void moradorInexistenteRetorna404() throws Exception {
        long c = criarCategoria();
        assertThat(doPost("/ocorrencias", jsonOcorrencia(999999999L, c, 3)).getStatus()).isEqualTo(404);
    }

    @Test
    void camposObrigatoriosAusentesRetorna400() throws Exception {
        assertThat(doPost("/ocorrencias", "{}").getStatus()).isEqualTo(400);
    }

    @Test
    void fluxoDeStatusValido() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        assertThat(doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"EM_ANALISE\"}").getStatus()).isEqualTo(200);
        assertThat(doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"EM_ANDAMENTO\"}").getStatus()).isEqualTo(200);
        MockHttpServletResponse r = doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"RESOLVIDA\"}");
        assertThat(r.getStatus()).isEqualTo(200);
        assertThat(corpo(r)).contains("\"status\":\"RESOLVIDA\"");
    }

    @Test
    void transicaoInvalidaRetorna409() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        assertThat(doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"RESOLVIDA\"}").getStatus()).isEqualTo(409);
    }

    @Test
    void statusInexistenteRetorna400() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        assertThat(doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"INVENTADO\"}").getStatus()).isEqualTo(400);
    }

    @Test
    void ocorrenciaFinalizadaNaoPodeSerAlterada() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        assertThat(doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"CANCELADA\"}").getStatus()).isEqualTo(200);
        assertThat(doPatch("/ocorrencias/" + id + "/prioridade", "{\"prioridade\":5}").getStatus()).isEqualTo(409);
    }

    @Test
    void alteraPrioridade() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        MockHttpServletResponse r = doPatch("/ocorrencias/" + id + "/prioridade", "{\"prioridade\":5}");
        assertThat(r.getStatus()).isEqualTo(200);
        assertThat(corpo(r)).contains("\"prioridade\":5");
        assertThat(doPatch("/ocorrencias/" + id + "/prioridade", "{\"prioridade\":9}").getStatus()).isEqualTo(400);
    }

    @Test
    void atualizaOcorrenciaComPut() throws Exception {
        long moradorId = criarMorador();
        long categoriaId = criarCategoria();
        long id = criarOcorrencia(moradorId, categoriaId);
        String json = "{\"titulo\":\"Titulo novo\",\"descricao\":\"Descricao nova\",\"localizacao\":\"Rua B, 20\","
                + "\"prioridade\":2,\"status\":\"EM_ANALISE\",\"categoriaId\":" + categoriaId + "}";
        MockHttpServletResponse r = doPut("/ocorrencias/" + id, json);
        assertThat(r.getStatus()).isEqualTo(200);
        assertThat(corpo(r)).contains("Titulo novo").contains("\"status\":\"EM_ANALISE\"").contains("\"prioridade\":2");
    }

    @Test
    void excluiOcorrenciaEDepoisRetorna404() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        assertThat(doDelete("/ocorrencias/" + id).getStatus()).isEqualTo(204);
        assertThat(doGet("/ocorrencias/" + id).getStatus()).isEqualTo(404);
    }

    @Test
    void naoExcluiOcorrenciaEmAndamento() throws Exception {
        long id = criarOcorrencia(criarMorador(), criarCategoria());
        doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"EM_ANALISE\"}");
        doPatch("/ocorrencias/" + id + "/status", "{\"status\":\"EM_ANDAMENTO\"}");
        assertThat(doDelete("/ocorrencias/" + id).getStatus()).isEqualTo(409);
    }

    @Test
    void rotaRaizRetornaStatusOnline() throws Exception {
        MockHttpServletResponse r = doGet("/");
        assertThat(r.getStatus()).isEqualTo(200);
        assertThat(corpo(r)).contains("online");
    }
}
