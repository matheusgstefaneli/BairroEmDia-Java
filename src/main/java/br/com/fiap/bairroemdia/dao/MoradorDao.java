package br.com.fiap.bairroemdia.dao;

import br.com.fiap.bairroemdia.dao.repository.MoradorRepository;
import br.com.fiap.bairroemdia.model.Morador;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO de Morador: única porta de acesso à persistência (a BO não conhece o JPA). */
@Repository
public class MoradorDao {

    private final MoradorRepository repository;

    public MoradorDao(MoradorRepository repository) {
        this.repository = repository;
    }

    public List<Morador> listarTodos() {
        return repository.findAll(Sort.by("id"));
    }

    public Optional<Morador> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Morador> buscarPorEmail(String email) {
        return repository.findByEmail(email == null ? null : email.trim().toLowerCase());
    }

    public Morador salvar(Morador morador) {
        return repository.saveAndFlush(morador);
    }

    public void excluir(Morador morador) {
        repository.delete(morador);
        repository.flush();
    }
}
