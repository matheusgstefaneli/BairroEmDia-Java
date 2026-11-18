package br.com.fiap.bairroemdia.bo;

import br.com.fiap.bairroemdia.dao.CategoriaDao;
import br.com.fiap.bairroemdia.dao.MoradorDao;
import br.com.fiap.bairroemdia.dao.OcorrenciaDao;
import br.com.fiap.bairroemdia.dto.OcorrenciaRequest;
import br.com.fiap.bairroemdia.exception.ConflitoException;
import br.com.fiap.bairroemdia.exception.RecursoNaoEncontradoException;
import br.com.fiap.bairroemdia.model.Categoria;
import br.com.fiap.bairroemdia.model.Morador;
import br.com.fiap.bairroemdia.model.Ocorrencia;
import br.com.fiap.bairroemdia.model.StatusOcorrencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OcorrenciaBoTest {

    @Mock
    private OcorrenciaDao ocorrenciaDao;
    @Mock
    private MoradorDao moradorDao;
    @Mock
    private CategoriaDao categoriaDao;

    private OcorrenciaBo bo;
    private Morador morador;
    private Categoria categoria;

    @BeforeEach
    void preparar() {
        bo = new OcorrenciaBo(ocorrenciaDao, moradorDao, categoriaDao);
        morador = new Morador("Ana", "ana@example.com");
        categoria = new Categoria("Buraco", "Buraco na rua");
        lenient().when(ocorrenciaDao.salvar(any(Ocorrencia.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Ocorrencia ocorrenciaComStatus(StatusOcorrencia destino) {
        Ocorrencia o = new Ocorrencia("Titulo", "Descricao", "Rua A", 3, morador, categoria);
        o.alterarStatus(destino);
        lenient().when(ocorrenciaDao.buscarPorId(1L)).thenReturn(Optional.of(o));
        return o;
    }

    @Test
    void criarOcorrenciaComMoradorInexistenteLancaNaoEncontrado() {
        lenient().when(moradorDao.buscarPorId(99L)).thenReturn(Optional.empty());
        OcorrenciaRequest req = new OcorrenciaRequest("t", "d", "l", 3, 99L, 1L);
        assertThatThrownBy(() -> bo.criar(req)).isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void criarOcorrenciaValidaNasceAberta() {
        lenient().when(moradorDao.buscarPorId(1L)).thenReturn(Optional.of(morador));
        lenient().when(categoriaDao.buscarPorId(2L)).thenReturn(Optional.of(categoria));
        Ocorrencia criada = bo.criar(new OcorrenciaRequest("t", "d", "l", 4, 1L, 2L));
        assertThat(criada.getStatus()).isEqualTo(StatusOcorrencia.ABERTA);
        assertThat(criada.getPrioridade()).isEqualTo(4);
        verify(ocorrenciaDao).salvar(any(Ocorrencia.class));
    }

    @Test
    void transicaoValidaAtualizaStatus() {
        ocorrenciaComStatus(StatusOcorrencia.ABERTA);
        Ocorrencia o = bo.alterarStatus(1L, StatusOcorrencia.EM_ANALISE);
        assertThat(o.getStatus()).isEqualTo(StatusOcorrencia.EM_ANALISE);
    }

    @Test
    void transicaoInvalidaLancaConflito() {
        ocorrenciaComStatus(StatusOcorrencia.ABERTA);
        assertThatThrownBy(() -> bo.alterarStatus(1L, StatusOcorrencia.RESOLVIDA))
                .isInstanceOf(ConflitoException.class);
    }

    @Test
    void ocorrenciaFinalizadaNaoPodeMudarPrioridade() {
        ocorrenciaComStatus(StatusOcorrencia.RESOLVIDA);
        assertThatThrownBy(() -> bo.alterarPrioridade(1L, 5)).isInstanceOf(ConflitoException.class);
    }

    @Test
    void ocorrenciaEmAndamentoNaoPodeSerExcluida() {
        ocorrenciaComStatus(StatusOcorrencia.EM_ANDAMENTO);
        assertThatThrownBy(() -> bo.excluir(1L)).isInstanceOf(ConflitoException.class);
    }

    @Test
    void buscarInexistenteLancaNaoEncontrado() {
        lenient().when(ocorrenciaDao.buscarPorId(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bo.buscar(7L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
