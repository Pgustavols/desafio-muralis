package br.com.muralis.gestao_contatos.exception;

// DTO para devolver o erro de forma legível para o frontend
public record ErroDeValidacaoDTO(String campo, String mensagem) {
}