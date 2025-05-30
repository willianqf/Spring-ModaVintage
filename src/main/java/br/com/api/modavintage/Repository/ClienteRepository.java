package br.com.api.modavintage.Repository; // Seu pacote

import br.com.api.modavintage.Model.Cliente;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Método de pesquisa por nome agora retorna Page<Cliente> e aceita Pageable
    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // O JpaRepository já fornece findAll(Pageable pageable) que retorna Page<Cliente>
}