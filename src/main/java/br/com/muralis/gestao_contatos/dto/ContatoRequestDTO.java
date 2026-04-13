package br.com.muralis.gestao_contatos.dto;

import jakarta.validation.constraints.NotBlank;

public record ContatoRequestDTO(
        @NotBlank(message = "O tipo de contato é obrigatório (ex: Telefone, Email)")
        String tipo,

        @NotBlank(message = "O valor do contato é obrigatório")
        String valor,

        String observacao
) {
}