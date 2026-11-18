package br.com.fiap.bairroemdia.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

/** Tratamento centralizado de exceções (Aula 31). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroResposta> dadosInvalidos(DadosInvalidosException ex, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> conflito(ConflitoException ex, HttpServletRequest req) {
        return montar(HttpStatus.CONFLICT, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .sorted()
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos na requisição.", req, detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> jsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST,
                "Corpo da requisição ausente, malformado ou com valor inválido (verifique o JSON e o status informado).",
                req, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro '" + ex.getName() + "'.", req, List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> metodoNaoSuportado(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não suportado para este endpoint.", req, List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> rotaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, "Endpoint não encontrado.", req, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violação de integridade no banco: {}", ex.getClass().getSimpleName());
        return montar(HttpStatus.CONFLICT,
                "Operação viola uma restrição de integridade dos dados (registro duplicado ou em uso).", req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> erroInterno(Exception ex, HttpServletRequest req) {
        log.error("Erro interno não esperado", ex);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor. Tente novamente mais tarde.", req, List.of());
    }

    private ResponseEntity<ErroResposta> montar(HttpStatus status, String mensagem,
                                                HttpServletRequest req, List<String> detalhes) {
        ErroResposta corpo = new ErroResposta(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensagem, req.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
