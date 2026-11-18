package br.com.fiap.bairroemdia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corpo do POST /ocorrencias (a ocorrência nasce com status ABERTA). */
public record OcorrenciaRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String descricao,

        @NotBlank(message = "A localização é obrigatória")
        @Size(max = 255, message = "A localização deve ter no máximo 255 caracteres")
        String localizacao,

        @NotNull(message = "A prioridade é obrigatória")
        @Min(value = 1, message = "A prioridade mínima é 1")
        @Max(value = 5, message = "A prioridade máxima é 5")
        Integer prioridade,

        @NotNull(message = "O morador é obrigatório")
        Long moradorId,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId) {
}
