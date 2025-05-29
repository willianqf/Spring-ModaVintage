package br.com.api.modavintage.Repository; // Seu pacote

import br.com.api.modavintage.Model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Adicionar se não tiver

import java.util.List; // Importar List

@Repository // Adicionar @Repository se ainda não tiver
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    // Novo método para buscar fornecedores por nome contendo o termo, ignorando caso
    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
}