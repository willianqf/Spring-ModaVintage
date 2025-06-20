package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; 

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Métodos para buscar apenas produtos ATIVOS

    // Para busca paginada por nome considerando apenas produtos ativos
    Page<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    // Para listar todos os produtos ativos com paginação 
    Page<Produto> findAllByAtivoTrue(Pageable pageable);

    // Para listar todos os produtos ativos com ordenação 
    List<Produto> findAllByAtivoTrue(Sort sort);
    
    // Para buscar um produto ativo pelo ID
    Optional<Produto> findByIdAndAtivoTrue(Long id);

    // Para verificar se um produto ativo existe pelo ID
    boolean existsByIdAndAtivoTrue(Long id);

    @Query(value = "SELECT EXTRACT(YEAR FROM p.data_cadastro) as ano, EXTRACT(MONTH FROM p.data_cadastro) as mes, SUM(p.preco_custo * p.estoque) as valor " +
                   "FROM produtos p " +
                   "GROUP BY EXTRACT(YEAR FROM p.data_cadastro), EXTRACT(MONTH FROM p.data_cadastro) " +
                   "ORDER BY ano DESC, mes DESC", nativeQuery = true)
    List<Object[]> findValorEntradaEstoquePorMesRaw();
}