package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // Importar Sort
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Importar Optional

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Métodos para buscar apenas produtos ATIVOS

    // Para busca paginada por nome, considerando apenas produtos ativos
    Page<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    // Para listar todos os produtos ativos com paginação (caso não haja pesquisa por nome)
    Page<Produto> findAllByAtivoTrue(Pageable pageable);

    // Para listar todos os produtos ativos com ordenação (usado no VendaService para popular modal)
    List<Produto> findAllByAtivoTrue(Sort sort);
    
    // Para buscar um produto ativo pelo ID
    Optional<Produto> findByIdAndAtivoTrue(Long id);

    // Para verificar se um produto ativo existe pelo ID
    boolean existsByIdAndAtivoTrue(Long id);


    // Método original de pesquisa por nome (pode ser mantido se houver uso específico para buscar todos, inclusive inativos, ou removido se não)
    // Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable); 
    // Por ora, vamos manter comentado para focar nos ativos. Se precisar reativar, lembre-se que não filtra por 'ativo'.


    // Método para o relatório (mantém como está, não precisa de paginação aqui geralmente)
    // A lógica de 'ativo' para este relatório depende: se for valor de entrada de *novos* produtos,
    // o status 'ativo' atual pode não ser relevante. Se for valor de estoque *atual* de produtos
    // que entraram naquele mês, então precisaria filtrar por 'ativo = true'.
    // Vamos manter como está por enquanto, assumindo que é o valor bruto de entrada no mês.
    @Query("SELECT YEAR(p.dataCadastro) AS ano, MONTH(p.dataCadastro) AS mes, SUM(p.precoCusto * p.estoque) AS valorEntrada " +
           "FROM Produto p " +
           "WHERE p.dataCadastro IS NOT NULL AND p.precoCusto IS NOT NULL " + // Garante que precoCusto não seja nulo no cálculo
           "GROUP BY YEAR(p.dataCadastro), MONTH(p.dataCadastro) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findValorEntradaEstoquePorMesRaw();
}