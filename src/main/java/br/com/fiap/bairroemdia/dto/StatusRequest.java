package br.com.fiap.bairroemdia.dto;

import br.com.fiap.bairroemdia.model.StatusOcorrencia;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "O status é obrigatório")
        StatusOcorrencia status) {
}
