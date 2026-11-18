package br.com.fiap.bairroemdia.dto;

import br.com.fiap.bairroemdia.model.Categoria;

public record CategoriaResponse(Long id, String nome, String descricao) {

    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao());
    }
}
