package br.com.fiap.bairroemdia.api;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaApiTest extends ApiTestBase {

    @Test
    void crudCompleto() throws Exception {
        String nome = unico("Iluminacao");
        MockHttpServletResponse criada = doPost("/categorias", "{\"nome\":\"" + nome + "\",\"descricao\":\"Postes\"}");
        assertThat(criada.getStatus()).isEqualTo(201);
        long id = extrairId(corpo(criada));

        assertThat(doGet("/categorias/" + id).getStatus()).isEqualTo(200);
        assertThat(corpo(doGet("/categorias"))).contains(nome);

        String novoNome = unico("Calcada");
        MockHttpServletResponse atualizada = doPut("/categorias/" + id,
                "{\"nome\":\"" + novoNome + "\",\"descricao\":\"Calcadas danificadas\"}");
        assertThat(atualizada.getStatus()).isEqualTo(200);
        assertThat(corpo(atualizada)).contains(novoNome);

        assertThat(doDelete("/categorias/" + id).getStatus()).isEqualTo(204);
        assertThat(doGet("/categorias/" + id).getStatus()).isEqualTo(404);
    }

    @Test
    void nomeVazioRetorna400() throws Exception {
        assertThat(doPost("/categorias", "{\"nome\":\"  \"}").getStatus()).isEqualTo(400);
    }

    @Test
    void nomeDuplicadoRetorna409() throws Exception {
        String json = "{\"nome\":\"" + unico("Lixo") + "\"}";
        assertThat(doPost("/categorias", json).getStatus()).isEqualTo(201);
        assertThat(doPost("/categorias", json).getStatus()).isEqualTo(409);
    }

    @Test
    void naoExcluiCategoriaComOcorrencias() throws Exception {
        long moradorId = criarMorador();
        long categoriaId = criarCategoria();
        criarOcorrencia(moradorId, categoriaId);
        assertThat(doDelete("/categorias/" + categoriaId).getStatus()).isEqualTo(409);
    }
}
