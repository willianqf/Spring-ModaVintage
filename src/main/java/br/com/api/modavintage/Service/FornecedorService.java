package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Fornecedor;
import br.com.api.modavintage.Repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public Fornecedor salvarFornecedor(Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorRepository.findAll();
    }

    // Novo método
    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    // Novo método
    @Transactional
    public Fornecedor atualizarFornecedor(Long id, Fornecedor fornecedorDetalhes) {
        Fornecedor fornecedorExistente = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com id: " + id));

        if (fornecedorDetalhes.getNome() != null) {
            fornecedorExistente.setNome(fornecedorDetalhes.getNome());
        }
        if (fornecedorDetalhes.getCnpj() != null) {
            fornecedorExistente.setCnpj(fornecedorDetalhes.getCnpj());
        }
        if (fornecedorDetalhes.getContato() != null) { // Relembrando que adicionamos 'contato' de volta ao model
            fornecedorExistente.setContato(fornecedorDetalhes.getContato());
        }
        return fornecedorRepository.save(fornecedorExistente);
    }

    // Novo método
    @Transactional
    public void deletarFornecedor(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new RuntimeException("Fornecedor não encontrado com id: " + id);
        }
        fornecedorRepository.deleteById(id);
    }
}