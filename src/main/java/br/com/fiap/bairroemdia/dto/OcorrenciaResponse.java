package br.com.fiap.bairroemdia.dto;

import br.com.fiap.bairroemdia.model.Ocorrencia;
import br.com.fiap.bairroemdia.model.StatusOcorrencia;

import java.time.LocalDateTime;

public record OcorrenciaResponse(
        Long id,
        String titulo,
        String descricao,
        String localizacao,
        Integer prioridade,
        StatusOcorrencia status,
        LocalDateTime dataCriacao,
        Long moradorId,
        String moradorNome,
        Long categoriaId,
        String categoriaNome) {

    public static OcorrenciaResponse from(Ocorrencia o) {
        return new OcorrenciaResponse(
                o.getId(), o.getTitulo(), o.getDescricao(), o.getLocalizacao(),
                o.getPrioridade(), o.getStatus(), o.getDataCriacao(),
                o.getMorador().getId(), o.getMorador().getNome(),
                o.getCategoria().getId(), o.getCategoria().getNome());
    }
}
