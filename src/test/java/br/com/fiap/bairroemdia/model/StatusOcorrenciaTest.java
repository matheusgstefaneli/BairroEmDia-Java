package br.com.fiap.bairroemdia.model;

import br.com.fiap.bairroemdia.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusOcorrenciaTest {

    @Test
    void fluxoNormalEPermitido() {
        assertThat(StatusOcorrencia.ABERTA.podeTransitarPara(StatusOcorrencia.EM_ANALISE)).isTrue();
        assertThat(StatusOcorrencia.EM_ANALISE.podeTransitarPara(StatusOcorrencia.EM_ANDAMENTO)).isTrue();
        assertThat(StatusOcorrencia.EM_ANDAMENTO.podeTransitarPara(StatusOcorrencia.RESOLVIDA)).isTrue();
    }

    @Test
    void cancelarEPermitidoEmEstadosNaoFinais() {
        assertThat(StatusOcorrencia.ABERTA.podeTransitarPara(StatusOcorrencia.CANCELADA)).isTrue();
        assertThat(StatusOcorrencia.EM_ANALISE.podeTransitarPara(StatusOcorrencia.CANCELADA)).isTrue();
        assertThat(StatusOcorrencia.EM_ANDAMENTO.podeTransitarPara(StatusOcorrencia.CANCELADA)).isTrue();
    }

    @Test
    void saltosEEstadosFinaisNaoTemSaida() {
        assertThat(StatusOcorrencia.ABERTA.podeTransitarPara(StatusOcorrencia.RESOLVIDA)).isFalse();
        assertThat(StatusOcorrencia.RESOLVIDA.proximosPermitidos()).isEmpty();
        assertThat(StatusOcorrencia.CANCELADA.proximosPermitidos()).isEmpty();
        assertThat(StatusOcorrencia.RESOLVIDA.isFinal()).isTrue();
        assertThat(StatusOcorrencia.ABERTA.isFinal()).isFalse();
    }

    @Test
    void entidadesNaoAceitamEstadoInvalido() {
        assertThatThrownBy(() -> new Morador(" ", "a@b.com")).isInstanceOf(DadosInvalidosException.class);
        assertThatThrownBy(() -> new Morador("Ana", "email-invalido")).isInstanceOf(DadosInvalidosException.class);
        assertThatThrownBy(() -> new Categoria(null, "x")).isInstanceOf(DadosInvalidosException.class);

        Morador m = new Morador("Ana", "ANA@Example.com");
        Categoria c = new Categoria("Lixo", null);
        assertThat(m.getEmail()).isEqualTo("ana@example.com");
        assertThatThrownBy(() -> new Ocorrencia("t", "d", "l", 6, m, c)).isInstanceOf(DadosInvalidosException.class);
        assertThatThrownBy(() -> new Ocorrencia("t", "d", "l", 0, m, c)).isInstanceOf(DadosInvalidosException.class);

        Ocorrencia o = new Ocorrencia("t", "d", "l", 3, m, c);
        assertThat(o.getStatus()).isEqualTo(StatusOcorrencia.ABERTA);
        assertThat(o.getDataCriacao()).isNotNull();
    }
}
