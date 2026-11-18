package br.com.fiap.bairroemdia.dao;

import br.com.fiap.bairroemdia.dao.repository.CategoriaRepository;
import br.com.fiap.bairroemdia.model.Categoria;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de Categoria. */
@Repository
public class CategoriaDao {

    private final CategoriaRepository repository;

    public CategoriaDao(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listarTodas() {
        return repository.findAll(Sort.by("id"));
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Categoria> buscarPorNome(String nome) {
        return repository.findByNomeIgnoreCase(nome == null ? null : nome.trim());
    }

    public Categoria salvar(Categoria categoria) {
        return repository.saveAndFlush(categoria);
    }

    public void excluir(Categoria categoria) {
        repository.delete(categoria);
        repository.flush();
    }
}
