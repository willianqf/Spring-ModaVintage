package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente salvarCliente(Cliente cliente) {
        if (cliente.getId() == null) { // Novo cliente
            cliente.setAtivo(true); // Garante que novos clientes são ativos
        }
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Page<Cliente> listarClientes(String nomePesquisa, Pageable pageable) {
        if (StringUtils.hasText(nomePesquisa)) {
            return clienteRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nomePesquisa, pageable);
        } else {
            return clienteRepository.findAllByAtivoTrue(pageable);
        }
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodosClientesAtivos() { 
        return clienteRepository.findAllByAtivoTrue(Sort.by(Sort.Direction.ASC, "nome"));
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorIdAtivo(Long id) { 
        return clienteRepository.findByIdAndAtivoTrue(id);
    }

    // Método para buscar por ID independentemente do status 'ativo'
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorIdQualquerStatus(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteDetalhes) {
        Cliente clienteExistente = clienteRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Cliente ativo não encontrado com id: " + id + " para atualização."));

        if (StringUtils.hasText(clienteDetalhes.getNome())) {
            clienteExistente.setNome(clienteDetalhes.getNome());
        }
        // Atualiza telefone e email, permitindo que sejam definidos como null ou string vazia
        clienteExistente.setTelefone(clienteDetalhes.getTelefone());
        clienteExistente.setEmail(clienteDetalhes.getEmail());
        
        // O campo 'ativo' não é modificado aqui.
        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void deletarCliente(Long id) { // Implementa Soft Delete
        Cliente cliente = clienteRepository.findById(id) // Busca o cliente independentemente do status
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id + " para exclusão."));

        if (!cliente.isAtivo()) {
            // throw new RuntimeException("Cliente com id: " + id + " já está inativo.");
        }


        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }
}