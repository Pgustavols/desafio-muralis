package br.com.muralis.gestao_contatos.service;

import br.com.muralis.gestao_contatos.dto.ClienteRequestDTO;
import br.com.muralis.gestao_contatos.dto.ClienteResponseDTO;
import br.com.muralis.gestao_contatos.dto.EnderecoDTO;
import br.com.muralis.gestao_contatos.entity.Cliente;
import br.com.muralis.gestao_contatos.entity.Endereco;
import br.com.muralis.gestao_contatos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // RF01: Cadastrar
    @Transactional
    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO dto) {
        String cpfLimpo = limparCpf(dto.cpf());

        if (clienteRepository.findByCpf(cpfLimpo).isPresent()) {
            throw new IllegalArgumentException("Este CPF já está cadastrado no sistema.");
        }

        Cliente cliente = new Cliente();
        atualizarDadosCliente(cliente, dto, cpfLimpo);

        return mapearParaResponseDTO(clienteRepository.save(cliente));
    }

    // RF04: Listar Todos
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    // RF05: Buscar por ID (Auxiliar para edições e contatos)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
    }

    // RF05: Buscar por Nome
    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    // RF05: Buscar por CPF
    public ClienteResponseDTO buscarPorCpf(String cpf) {
        String cpfLimpo = limparCpf(cpf);
        Cliente cliente = clienteRepository.findByCpf(cpfLimpo)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com este CPF."));
        return mapearParaResponseDTO(cliente);
    }

    // RF02: Atualizar
    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarPorId(id);
        String cpfLimpo = limparCpf(dto.cpf());

        // Valida se o novo CPF já pertence a OUTRO cliente
        clienteRepository.findByCpf(cpfLimpo).ifPresent(clienteExistente -> {
            if (!clienteExistente.getId().equals(id)) {
                throw new IllegalArgumentException("Este CPF já pertence a outro cliente.");
            }
        });

        atualizarDadosCliente(cliente, dto, cpfLimpo);
        return mapearParaResponseDTO(clienteRepository.save(cliente));
    }

    // RF03: Deletar
    @Transactional
    public void deletarCliente(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente); // O CascadeType.ALL deleta o endereço e contatos junto!
    }


    private String limparCpf(String cpf) {
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }

    private void atualizarDadosCliente(Cliente cliente, ClienteRequestDTO dto, String cpfLimpo) {

        // --- BLINDAGEM RN08: MÍNIMO DE 18 ANOS ---
        java.time.LocalDate hoje = java.time.LocalDate.now();
        int idade = hoje.getYear() - dto.dataNasc().getYear();
        if (dto.dataNasc().plusYears(idade).isAfter(hoje)) {
            idade--;
        }
        if (idade < 18) {
            throw new IllegalArgumentException("Regra de Negócio: O cliente deve ter no mínimo 18 anos.");
        }
        // -----------------------------------------

        cliente.setNome(dto.nome());
        cliente.setCpf(cpfLimpo);
        cliente.setDataNasc(dto.dataNasc());

        // Só tenta montar e salvar o endereço se o frontend tiver enviado um
        if (dto.endereco() != null) {
            if (cliente.getEndereco() == null) {
                cliente.setEndereco(new Endereco());
                cliente.getEndereco().setCliente(cliente);
            }

            Endereco endereco = cliente.getEndereco();
            endereco.setCep(dto.endereco().cep());
            endereco.setLogradouro(dto.endereco().logradouro());
            endereco.setNumero(dto.endereco().numero());
            endereco.setComplemento(dto.endereco().complemento());
            endereco.setBairro(dto.endereco().bairro());
            endereco.setCidade(dto.endereco().cidade());
            endereco.setEstado(dto.endereco().estado());
        }
    }

    private ClienteResponseDTO mapearParaResponseDTO(Cliente cliente) {
        EnderecoDTO enderecoDTO = null;

        // Só monta o DTO de resposta do endereço se existir um salvo no banco
        if (cliente.getEndereco() != null) {
            enderecoDTO = new EnderecoDTO(
                    cliente.getEndereco().getCep(),
                    cliente.getEndereco().getLogradouro(),
                    cliente.getEndereco().getNumero(),
                    cliente.getEndereco().getComplemento(),
                    cliente.getEndereco().getBairro(),
                    cliente.getEndereco().getCidade(),
                    cliente.getEndereco().getEstado()
            );
        }

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getDataNasc(),
                enderecoDTO
        );
    }
}