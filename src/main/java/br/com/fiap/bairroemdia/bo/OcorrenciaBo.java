package br.com.fiap.bairroemdia.bo;

import br.com.fiap.bairroemdia.dao.CategoriaDao;
import br.com.fiap.bairroemdia.dao.MoradorDao;
import br.com.fiap.bairroemdia.dao.OcorrenciaDao;
import br.com.fiap.bairroemdia.dto.OcorrenciaRequest;
import br.com.fiap.bairroemdia.dto.OcorrenciaUpdateRequest;
import br.com.fiap.bairroemdia.exception.ConflitoException;
import br.com.fiap.bairroemdia.exception.RecursoNaoEncontradoException;
import br.com.fiap.bairroemdia.model.Categoria;
import br.com.fiap.bairroemdia.model.Morador;
import br.com.fiap.bairroemdia.model.Ocorrencia;
import br.com.fiap.bairroemdia.model.StatusOcorrencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio de Ocorrencia:
 * - nasce ABERTA;
 * - status segue o fluxo ABERTA -> EM_ANALISE -> EM_ANDAMENTO -> RESOLVIDA (CANCELADA a partir de qualquer estado não final);
 * - ocorrência RESOLVIDA/CANCELADA não pode mais ser alterada;
 * - prioridade entre 1 e 5;
 * - ocorrência EM_ANDAMENTO não pode ser excluída.
 */
@Service
@Transactional(readOnly = true)
public class OcorrenciaBo {

    private final OcorrenciaDao ocorrenciaDao;
    private final MoradorDao moradorDao;
    private final CategoriaDao categoriaDao;

    public OcorrenciaBo(OcorrenciaDao ocorrenciaDao, MoradorDao moradorDao, CategoriaDao categoriaDao) {
        this.ocorrenciaDao = ocorrenciaDao;
        this.moradorDao = moradorDao;
        this.categoriaDao = categoriaDao;
    }

    public List<Ocorrencia> listar() {
        return ocorrenciaDao.listarTodas();
    }

    public Ocorrencia buscar(Long id) {
        return ocorrenciaDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência", id));
    }

    @Transactional
    public Ocorrencia criar(OcorrenciaRequest req) {
        Morador morador = moradorDao.buscarPorId(req.moradorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Morador", req.moradorId()));
        Categoria categoria = categoriaDao.buscarPorId(req.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", req.categoriaId()));
        Ocorrencia nova = new Ocorrencia(req.titulo(), req.descricao(), req.localizacao(),
                req.prioridade(), morador, categoria);
        return ocorrenciaDao.salvar(nova);
    }

    @Transactional
    public Ocorrencia atualizar(Long id, OcorrenciaUpdateRequest req) {
        Ocorrencia ocorrencia = buscar(id);
        garantirNaoFinalizada(ocorrencia);
        Categoria categoria = categoriaDao.buscarPorId(req.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", req.categoriaId()));

        validarTransicao(ocorrencia.getStatus(), req.status());
        ocorrencia.atualizarDados(req.titulo(), req.descricao(), req.localizacao(), categoria);
        ocorrencia.alterarPrioridade(req.prioridade());
        ocorrencia.alterarStatus(req.status());
        return ocorrenciaDao.salvar(ocorrencia);
    }

    @Transactional
    public Ocorrencia alterarStatus(Long id, StatusOcorrencia novoStatus) {
        Ocorrencia ocorrencia = buscar(id);
        garantirNaoFinalizada(ocorrencia);
        validarTransicao(ocorrencia.getStatus(), novoStatus);
        ocorrencia.alterarStatus(novoStatus);
        return ocorrenciaDao.salvar(ocorrencia);
    }

    @Transactional
    public Ocorrencia alterarPrioridade(Long id, Integer novaPrioridade) {
        Ocorrencia ocorrencia = buscar(id);
        garantirNaoFinalizada(ocorrencia);
        ocorrencia.alterarPrioridade(novaPrioridade);
        return ocorrenciaDao.salvar(ocorrencia);
    }

    @Transactional
    public void excluir(Long id) {
        Ocorrencia ocorrencia = buscar(id);
        if (ocorrencia.getStatus() == StatusOcorrencia.EM_ANDAMENTO) {
            throw new ConflitoException("Uma ocorrência EM_ANDAMENTO não pode ser excluída. Resolva ou cancele antes.");
        }
        ocorrenciaDao.excluir(ocorrencia);
    }

    private void garantirNaoFinalizada(Ocorrencia ocorrencia) {
        if (ocorrencia.isFinalizada()) {
            throw new ConflitoException("Ocorrência " + ocorrencia.getStatus()
                    + " não pode mais ser alterada.");
        }
    }

    /** Mesmo status é aceito (sem mudança); mudanças precisam seguir o fluxo permitido. */
    private void validarTransicao(StatusOcorrencia atual, StatusOcorrencia novo) {
        if (atual == novo) {
            return;
        }
        if (!atual.podeTransitarPara(novo)) {
            throw new ConflitoException("Transição de status inválida: " + atual + " -> " + novo
                    + ". Permitidos a partir de " + atual + ": " + atual.proximosPermitidos() + ".");
        }
    }
}
