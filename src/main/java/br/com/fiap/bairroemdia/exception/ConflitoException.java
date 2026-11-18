package br.com.fiap.bairroemdia.exception;

/** Conflito de integridade ou de regra de negócio -> HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
