package br.com.muralis.gestao_contatos.repository;

import br.com.muralis.gestao_contatos.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Retorna um Optional porque o cliente tem no máximo 1 endereço.
    // Isso evita o temido NullPointerException caso o cliente ainda não tenha endereço cadastrado.
    Optional<Endereco> findByClienteId(Long clienteId);
}