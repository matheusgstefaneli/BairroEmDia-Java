package br.com.fiap.bairroemdia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MoradorRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail em formato inválido")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
        String email) {
}
