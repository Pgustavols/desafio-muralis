package br.com.muralis.gestao_contatos.dto;

import jakarta.validation.constraints.NotBlank;

public record EnderecoDTO(
        @NotBlank(message = "O CEP é obrigatório") String cep,
        @NotBlank(message = "O logradouro é obrigatório") String logradouro,
        @NotBlank(message = "O número é obrigatório") String numero,
        String complemento,
        @NotBlank(message = "O bairro é obrigatório") String bairro,
        @NotBlank(message = "A cidade é obrigatória") String cidade,
        @NotBlank(message = "O estado é obrigatório") String estado
) {
}