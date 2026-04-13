package br.com.muralis.gestao_contatos.dto;

import java.time.LocalDate;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        LocalDate dataNasc,
        EnderecoDTO endereco
) {
}