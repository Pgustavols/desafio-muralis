package br.com.muralis.gestao_contatos.service;

import br.com.muralis.gestao_contatos.dto.ContatoRequestDTO;
import br.com.muralis.gestao_contatos.dto.ContatoResponseDTO;
import br.com.muralis.gestao_contatos.entity.Cliente;
import br.com.muralis.gestao_contatos.entity.Contato;
import br.com.muralis.gestao_contatos.repository.ContatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContatoService {

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private ClienteService clienteService; // Usamos o serviço para reaproveitar a busca com tratamento de erro

    // RF06: Cadastrar Contato
    @Transactional
    public ContatoResponseDTO adicionarContato(Long clienteId, ContatoRequestDTO dto) {
        Cliente cliente = clienteService.buscarPorId(clienteId);

        Contato contato = new Contato();
        contato.setTipo(dto.tipo());
        contato.setValor(dto.valor());
        contato.setObservacao(dto.observacao());
        contato.setCliente(cliente); // Amarra a chave estrangeira

        return mapearParaResponseDTO(contatoRepository.save(contato));
    }

    // RF09: Listar Contatos de um Cliente
    public List<ContatoResponseDTO> listarContatosDoCliente(Long clienteId) {
        return contatoRepository.findByClienteId(clienteId).stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    // RF07: Editar Contato
    @Transactional
    public ContatoResponseDTO atualizarContato(Long idContato, ContatoRequestDTO dto) {
        Contato contato = contatoRepository.findById(idContato)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado."));

        contato.setTipo(dto.tipo());
        contato.setValor(dto.valor());
        contato.setObservacao(dto.observacao());

        return mapearParaResponseDTO(contatoRepository.save(contato));
    }

    // RF08: Excluir Contato
    @Transactional
    public void deletarContato(Long idContato) {
        if (!contatoRepository.existsById(idContato)) {
            throw new RuntimeException("Contato não encontrado.");
        }
        contatoRepository.deleteById(idContato);
    }

    private ContatoResponseDTO mapearParaResponseDTO(Contato contato) {
        return new ContatoResponseDTO(
                contato.getId(),
                contato.getTipo(),
                contato.getValor(),
                contato.getObservacao()
        );
    }
}