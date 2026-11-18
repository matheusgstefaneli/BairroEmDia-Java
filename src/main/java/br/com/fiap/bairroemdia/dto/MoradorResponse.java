package br.com.fiap.bairroemdia.dto;

import br.com.fiap.bairroemdia.model.Morador;

public record MoradorResponse(Long id, String nome, String email) {

    public static MoradorResponse from(Morador m) {
        return new MoradorResponse(m.getId(), m.getNome(), m.getEmail());
    }
}
