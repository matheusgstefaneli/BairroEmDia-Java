package br.com.fiap.bairroemdia.model;

import br.com.fiap.bairroemdia.exception.DadosInvalidosException;

/** Validações de domínio reutilizadas pelos construtores/métodos das entidades. */
final class Validacao {

    private Validacao() {
    }

    static String texto(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo '" + campo + "' é obrigatório.");
        }
        String limpo = valor.trim();
        if (limpo.length() > max) {
            throw new DadosInvalidosException("O campo '" + campo + "' deve ter no máximo " + max + " caracteres.");
        }
        return limpo;
    }

    static String textoOpcional(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.length() > max) {
            throw new DadosInvalidosException("O campo '" + campo + "' deve ter no máximo " + max + " caracteres.");
        }
        return limpo;
    }
}
