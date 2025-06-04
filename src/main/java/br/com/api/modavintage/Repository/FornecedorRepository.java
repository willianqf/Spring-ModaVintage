package br.com.api.modavintage.Repository; 

import br.com.api.modavintage.Model.Fornecedor;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    // Método de pesquisa por nome agora retorna Page<Fornecedor> e aceita Pageable
    Page<Fornecedor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}