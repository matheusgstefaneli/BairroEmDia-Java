package br.com.fiap.bairroemdia.dao;

import br.com.fiap.bairroemdia.dao.repository.OcorrenciaRepository;
import br.com.fiap.bairroemdia.model.Ocorrencia;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de Ocorrencia. */
@Repository
public class OcorrenciaDao {

    private final OcorrenciaRepository repository;

    public OcorrenciaDao(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public List<Ocorrencia> listarTodas() {
        return repository.findAll(Sort.by("id"));
    }

    public Optional<Ocorrencia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public boolean existePorMorador(Long moradorId) {
        return repository.existsByMoradorId(moradorId);
    }

    public boolean existePorCategoria(Long categoriaId) {
        return repository.existsByCategoriaId(categoriaId);
    }

    public Ocorrencia salvar(Ocorrencia ocorrencia) {
        return repository.saveAndFlush(ocorrencia);
    }

    public void excluir(Ocorrencia ocorrencia) {
        repository.delete(ocorrencia);
        repository.flush();
    }
}
