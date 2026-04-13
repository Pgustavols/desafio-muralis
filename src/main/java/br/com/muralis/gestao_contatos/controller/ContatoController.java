package br.com.muralis.gestao_contatos.controller;

import br.com.muralis.gestao_contatos.dto.ContatoRequestDTO;
import br.com.muralis.gestao_contatos.dto.ContatoResponseDTO;
import br.com.muralis.gestao_contatos.service.ContatoService;
import jakarta.validation.Valid; // Import obrigatório para a anotação funcionar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ContatoController {

    @Autowired
    private ContatoService contatoService;

    @PostMapping("/clientes/{clienteId}/contatos")
    public ResponseEntity<ContatoResponseDTO> adicionarContato(
            @PathVariable Long clienteId,
            @RequestBody @Valid ContatoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contatoService.adicionarContato(clienteId, dto));
    }

    @GetMapping("/clientes/{clienteId}/contatos")
    public ResponseEntity<List<ContatoResponseDTO>> listarContatosDoCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(contatoService.listarContatosDoCliente(clienteId));
    }

    @PutMapping("/contatos/{id}")
    public ResponseEntity<ContatoResponseDTO> atualizarContato(
            @PathVariable Long id,
            @RequestBody @Valid ContatoRequestDTO dto) {
        return ResponseEntity.ok(contatoService.atualizarContato(id, dto));
    }

    @DeleteMapping("/contatos/{id}")
    public ResponseEntity<Void> deletarContato(@PathVariable Long id) {
        contatoService.deletarContato(id);
        return ResponseEntity.noContent().build();
    }
}