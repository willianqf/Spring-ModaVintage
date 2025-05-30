package br.com.api.modavintage.Repository; // Seu pacote

import br.com.api.modavintage.Model.Fornecedor;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Removido import de java.util.List se não for mais usado aqui

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    // Método de pesquisa por nome agora retorna Page<Fornecedor> e aceita Pageable
    Page<Fornecedor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}