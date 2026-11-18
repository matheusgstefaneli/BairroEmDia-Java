package br.com.fiap.bairroemdia.dao.repository;

import br.com.fiap.bairroemdia.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    boolean existsByMoradorId(Long moradorId);

    boolean existsByCategoriaId(Long categoriaId);
}
