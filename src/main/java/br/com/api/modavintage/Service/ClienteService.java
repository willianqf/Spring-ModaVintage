package br.com.api.modavintage.Service; // 

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Para verificar strings

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente salvarCliente(Cliente cliente) {
        // Poderia adicionar validações aqui antes de salvar
        return clienteRepository.save(cliente);
    }

    // Método listarClientes atualizado para aceitar um nome para pesquisa
    public List<Cliente> listarClientes(String nomePesquisa) {
        if (StringUtils.hasText(nomePesquisa)) {
            return clienteRepository.findByNomeContainingIgnoreCase(nomePesquisa);
        } else {
            return clienteRepository.findAll();
        }
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteDetalhes) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        if (StringUtils.hasText(clienteDetalhes.getNome())) { // Usar StringUtils para checar
            clienteExistente.setNome(clienteDetalhes.getNome());
        }
        if (StringUtils.hasText(clienteDetalhes.getEmail())) {
            clienteExistente.setEmail(clienteDetalhes.getEmail());
        }
        if (StringUtils.hasText(clienteDetalhes.getTelefone())) {
            clienteExistente.setTelefone(clienteDetalhes.getTelefone());
        }
        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}