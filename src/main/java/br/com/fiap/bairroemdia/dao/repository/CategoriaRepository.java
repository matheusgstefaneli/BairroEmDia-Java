package br.com.fiap.bairroemdia.dao.repository;

import br.com.fiap.bairroemdia.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNomeIgnoreCase(String nome);
}
