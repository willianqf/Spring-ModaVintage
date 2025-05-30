package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // Importar Sort
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
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Page<Cliente> listarClientes(String nomePesquisa, Pageable pageable) {
        if (StringUtils.hasText(nomePesquisa)) {
            return clienteRepository.findByNomeContainingIgnoreCase(nomePesquisa, pageable);
        } else {
            return clienteRepository.findAll(pageable);
        }
    }

    // NOVO MÉTODO (ou renomear o seu listarTodosClientesSemPaginacao)
    @Transactional(readOnly = true)
    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "nome")); // Ordena por nome
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente atualizarCliente(Long id, Cliente clienteDetalhes) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        if (StringUtils.hasText(clienteDetalhes.getNome())) {
            clienteExistente.setNome(clienteDetalhes.getNome());
        }
        if (StringUtils.hasText(clienteDetalhes.getTelefone())) {
            clienteExistente.setTelefone(clienteDetalhes.getTelefone());
        }
        if (StringUtils.hasText(clienteDetalhes.getEmail())) {
            clienteExistente.setEmail(clienteDetalhes.getEmail());
        }
        return clienteRepository.save(clienteExistente);
    }

    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}