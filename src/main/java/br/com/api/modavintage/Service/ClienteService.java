package br.com.api.modavintage.Service; // Seu pacote

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Opcional para read-only
import org.springframework.util.StringUtils; // Para verificar se a string de pesquisa tem texto

import java.util.List; // Manter se tiver um método que retorne List<Cliente> sem paginação
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente salvarCliente(Cliente cliente) {
        // Adicionar qualquer lógica de validação ou pré-processamento aqui se necessário
        return clienteRepository.save(cliente);
    }

    // Método listarClientes atualizado para aceitar Pageable e retornar Page<Cliente>
    @Transactional(readOnly = true) // Boa prática para métodos de leitura
    public Page<Cliente> listarClientes(String nomePesquisa, Pageable pageable) {
        if (StringUtils.hasText(nomePesquisa)) {
            return clienteRepository.findByNomeContainingIgnoreCase(nomePesquisa, pageable);
        } else {
            return clienteRepository.findAll(pageable); // Usa o findAll que aceita Pageable
        }
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente atualizarCliente(Long id, Cliente clienteDetalhes) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        // Atualizar os campos do clienteExistente com os de clienteDetalhes
        if (StringUtils.hasText(clienteDetalhes.getNome())) {
            clienteExistente.setNome(clienteDetalhes.getNome());
        }
        if (StringUtils.hasText(clienteDetalhes.getTelefone())) {
            clienteExistente.setTelefone(clienteDetalhes.getTelefone());
        }
        if (StringUtils.hasText(clienteDetalhes.getEmail())) {
            clienteExistente.setEmail(clienteDetalhes.getEmail());
        }
        // Adicione outros campos se houver

        return clienteRepository.save(clienteExistente);
    }

    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    // Se você tiver um método como este para pegar todos os clientes sem paginação,
    // ele pode ser mantido, mas a listagem principal usará o paginado.
    @Transactional(readOnly = true)
    public List<Cliente> listarTodosClientesSemPaginacao() {
        return clienteRepository.findAll();
    }
}