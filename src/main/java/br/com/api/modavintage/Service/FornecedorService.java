package br.com.api.modavintage.Service; 

import br.com.api.modavintage.Model.Fornecedor;
import br.com.api.modavintage.Repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Para verificar strings

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public Fornecedor salvarFornecedor(Fornecedor fornecedor) {
        // Adicione validações aqui se necessário (ex: CNPJ único)
        return fornecedorRepository.save(fornecedor);
    }

    // Método listarFornecedores atualizado para aceitar um nome para pesquisa
    public List<Fornecedor> listarFornecedores(String nomePesquisa) {
        if (StringUtils.hasText(nomePesquisa)) {
            return fornecedorRepository.findByNomeContainingIgnoreCase(nomePesquisa);
        } else {
            return fornecedorRepository.findAll();
        }
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    @Transactional
    public Fornecedor atualizarFornecedor(Long id, Fornecedor fornecedorDetalhes) {
        Fornecedor fornecedorExistente = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com id: " + id));

        if (StringUtils.hasText(fornecedorDetalhes.getNome())) {
            fornecedorExistente.setNome(fornecedorDetalhes.getNome());
        }
        if (StringUtils.hasText(fornecedorDetalhes.getCnpj())) {
            fornecedorExistente.setCnpj(fornecedorDetalhes.getCnpj());
        }
        if (StringUtils.hasText(fornecedorDetalhes.getContato())) {
            fornecedorExistente.setContato(fornecedorDetalhes.getContato());
        }
        return fornecedorRepository.save(fornecedorExistente);
    }

    @Transactional
    public void deletarFornecedor(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new RuntimeException("Fornecedor não encontrado com id: " + id);
        }
        fornecedorRepository.deleteById(id);
    }
}