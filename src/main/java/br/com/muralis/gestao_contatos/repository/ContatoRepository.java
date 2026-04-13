package br.com.muralis.gestao_contatos.repository;

import br.com.muralis.gestao_contatos.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    // O Spring gera o SQL: SELECT * FROM contato WHERE cliente_id = ?
    // Retorna uma Lista, pois um cliente pode ter vários contatos (telefone, email, etc.)
    List<Contato> findByClienteId(Long clienteId);
}