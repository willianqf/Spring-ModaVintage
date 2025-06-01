package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // Importar Sort
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importar List
import java.util.Optional; // Importar Optional

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Métodos para buscar apenas clientes ATIVOS

    // Para busca paginada por nome, considerando apenas clientes ativos
    Page<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    // Para listar todos os clientes ativos com paginação (caso não haja pesquisa por nome)
    Page<Cliente> findAllByAtivoTrue(Pageable pageable);

    // Para listar todos os clientes ativos com ordenação (para seleção em vendas, por exemplo)
    List<Cliente> findAllByAtivoTrue(Sort sort);
    
    // Para buscar um cliente ativo pelo ID
    Optional<Cliente> findByIdAndAtivoTrue(Long id);

    // Para verificar se um cliente ativo existe pelo ID
    boolean existsByIdAndAtivoTrue(Long id);

    // O JpaRepository já fornece findAll(Pageable pageable) que retorna Page<Cliente>
    // O método original findByNomeContainingIgnoreCase foi substituído pela versão com 'AndAtivoTrue'
    // para garantir que as pesquisas principais retornem apenas clientes ativos.
}