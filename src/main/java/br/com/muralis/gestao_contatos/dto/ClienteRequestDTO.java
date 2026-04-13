package br.com.muralis.gestao_contatos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record ClienteRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "O CPF informado não tem um formato válido")
        String cpf,

        @NotNull(message = "A data de nascimento é obrigatória")
        LocalDate dataNasc,

        @Valid // Manda o Spring validar o que está dentro do EnderecoDTO também
        EnderecoDTO endereco
) {
}