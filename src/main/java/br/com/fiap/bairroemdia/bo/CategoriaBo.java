package br.com.fiap.bairroemdia.bo;

import br.com.fiap.bairroemdia.dao.CategoriaDao;
import br.com.fiap.bairroemdia.dao.OcorrenciaDao;
import br.com.fiap.bairroemdia.dto.CategoriaRequest;
import br.com.fiap.bairroemdia.exception.ConflitoException;
import br.com.fiap.bairroemdia.exception.RecursoNaoEncontradoException;
import br.com.fiap.bairroemdia.model.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio de Categoria. */
@Service
@Transactional(readOnly = true)
public class CategoriaBo {

    private final CategoriaDao categoriaDao;
    private final OcorrenciaDao ocorrenciaDao;

    public CategoriaBo(CategoriaDao categoriaDao, OcorrenciaDao ocorrenciaDao) {
        this.categoriaDao = categoriaDao;
        this.ocorrenciaDao = ocorrenciaDao;
    }

    public List<Categoria> listar() {
        return categoriaDao.listarTodas();
    }

    public Categoria buscar(Long id) {
        return categoriaDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", id));
    }

    @Transactional
    public Categoria criar(CategoriaRequest req) {
        Categoria nova = new Categoria(req.nome(), req.descricao());
        if (categoriaDao.buscarPorNome(nova.getNome()).isPresent()) {
            throw new ConflitoException("Já existe uma categoria com este nome.");
        }
        return categoriaDao.salvar(nova);
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaRequest req) {
        Categoria categoria = buscar(id);
        categoriaDao.buscarPorNome(req.nome()).ifPresent(outra -> {
            if (!outra.getId().equals(id)) {
                throw new ConflitoException("Já existe uma categoria com este nome.");
            }
        });
        categoria.atualizar(req.nome(), req.descricao());
        return categoriaDao.salvar(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscar(id);
        if (ocorrenciaDao.existePorCategoria(id)) {
            throw new ConflitoException("Não é possível excluir uma categoria que possui ocorrências registradas.");
        }
        categoriaDao.excluir(categoria);
    }
}
