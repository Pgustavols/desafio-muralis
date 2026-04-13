package br.com.muralis.gestao_contatos.repository;

import br.com.muralis.gestao_contatos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Retorna um Optional porque o CPF é único, então pode encontrar 1 ou nenhum cliente
    Optional<Cliente> findByCpf(String cpf);

    // Retorna uma Lista porque podem existir várias pessoas com o mesmo nome (ou parte dele)
    // O "ContainingIgnoreCase" faz a busca como um "LIKE %nome%" no SQL, sem se importar com letras maiúsculas
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}