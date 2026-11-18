package br.com.fiap.bairroemdia.bo;

import br.com.fiap.bairroemdia.dao.MoradorDao;
import br.com.fiap.bairroemdia.dao.OcorrenciaDao;
import br.com.fiap.bairroemdia.dto.MoradorRequest;
import br.com.fiap.bairroemdia.exception.ConflitoException;
import br.com.fiap.bairroemdia.exception.RecursoNaoEncontradoException;
import br.com.fiap.bairroemdia.model.Morador;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio de Morador. */
@Service
@Transactional(readOnly = true)
public class MoradorBo {

    private final MoradorDao moradorDao;
    private final OcorrenciaDao ocorrenciaDao;

    public MoradorBo(MoradorDao moradorDao, OcorrenciaDao ocorrenciaDao) {
        this.moradorDao = moradorDao;
        this.ocorrenciaDao = ocorrenciaDao;
    }

    public List<Morador> listar() {
        return moradorDao.listarTodos();
    }

    public Morador buscar(Long id) {
        return moradorDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Morador", id));
    }

    @Transactional
    public Morador criar(MoradorRequest req) {
        Morador novo = new Morador(req.nome(), req.email());
        if (moradorDao.buscarPorEmail(novo.getEmail()).isPresent()) {
            throw new ConflitoException("Já existe um morador cadastrado com este e-mail.");
        }
        return moradorDao.salvar(novo);
    }

    @Transactional
    public Morador atualizar(Long id, MoradorRequest req) {
        Morador morador = buscar(id);
        moradorDao.buscarPorEmail(req.email()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new ConflitoException("Já existe um morador cadastrado com este e-mail.");
            }
        });
        morador.atualizar(req.nome(), req.email());
        return moradorDao.salvar(morador);
    }

    @Transactional
    public void excluir(Long id) {
        Morador morador = buscar(id);
        if (ocorrenciaDao.existePorMorador(id)) {
            throw new ConflitoException("Não é possível excluir um morador que possui ocorrências registradas.");
        }
        moradorDao.excluir(morador);
    }
}
