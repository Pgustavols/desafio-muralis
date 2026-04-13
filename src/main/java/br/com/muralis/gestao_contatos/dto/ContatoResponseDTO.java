package br.com.muralis.gestao_contatos.dto;

public record ContatoResponseDTO(
        Long id,
        String tipo,
        String valor,
        String observacao
) {
}