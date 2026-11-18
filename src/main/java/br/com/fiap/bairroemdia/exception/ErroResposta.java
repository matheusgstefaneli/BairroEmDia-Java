package br.com.fiap.bairroemdia.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Corpo padronizado das respostas de erro (sem stack trace nem dados internos). */
public record ErroResposta(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes) {
}
