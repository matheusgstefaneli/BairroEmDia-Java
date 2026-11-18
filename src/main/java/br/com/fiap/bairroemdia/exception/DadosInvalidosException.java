package br.com.fiap.bairroemdia.exception;

/** Dados que violam as regras do domínio -> HTTP 400. */
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
