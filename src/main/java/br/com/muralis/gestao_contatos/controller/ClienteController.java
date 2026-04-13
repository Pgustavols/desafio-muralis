package br.com.muralis.gestao_contatos.controller;

import br.com.muralis.gestao_contatos.dto.ClienteRequestDTO;
import br.com.muralis.gestao_contatos.dto.ClienteResponseDTO;
import br.com.muralis.gestao_contatos.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody @Valid ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.cadastrarCliente(dto));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/nome")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNome(@RequestParam String valor) {
        return ResponseEntity.ok(clienteService.buscarPorNome(valor));
    }

    @GetMapping("/cpf/{valor}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(@PathVariable String valor) {
        return ResponseEntity.ok(clienteService.buscarPorCpf(valor));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.atualizarCliente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}