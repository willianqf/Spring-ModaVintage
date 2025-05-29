package br.com.api.modavintage.Repository; // 

import br.com.api.modavintage.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // 

import java.util.List; // Importar List

@Repository // Adicionar @Repository se ainda não tiver
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Novo método para buscar clientes por nome contendo o termo
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}