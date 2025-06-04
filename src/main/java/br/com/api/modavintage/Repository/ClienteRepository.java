package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; 
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Métodos para buscar apenas clientes ATIVOS

    // Para busca paginada por nome, considerando apenas clientes ativos
    Page<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    // Para listar todos os clientes ativos com paginação 
    Page<Cliente> findAllByAtivoTrue(Pageable pageable);

    // Para listar todos os clientes ativos com ordenação
    List<Cliente> findAllByAtivoTrue(Sort sort);
    
    // Para buscar um cliente ativo pelo ID
    Optional<Cliente> findByIdAndAtivoTrue(Long id);

    // Para verificar se um cliente ativo existe pelo ID
    boolean existsByIdAndAtivoTrue(Long id);

}