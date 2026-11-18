package br.com.fiap.bairroemdia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PrioridadeRequest(
        @NotNull(message = "A prioridade é obrigatória")
        @Min(value = 1, message = "A prioridade mínima é 1")
        @Max(value = 5, message = "A prioridade máxima é 5")
        Integer prioridade) {
}
