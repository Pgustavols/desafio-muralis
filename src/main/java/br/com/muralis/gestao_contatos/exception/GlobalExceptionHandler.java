package br.com.muralis.gestao_contatos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarErrosDeNegocio(IllegalArgumentException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 2. PERFEITO: Captura os erros do @Valid e devolve uma lista [{}, {}]
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErroDeValidacaoDTO>> tratarErrosDeValidacao(MethodArgumentNotValidException ex) {
        List<ErroDeValidacaoDTO> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErroDeValidacaoDTO(erro.getField(), erro.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleViolacaoDeBanco(Exception ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("message", "Atenção: Este CPF já se encontra registado no banco de dados.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}