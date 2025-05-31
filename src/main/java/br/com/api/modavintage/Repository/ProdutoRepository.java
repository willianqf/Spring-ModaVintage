package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @Query("SELECT YEAR(p.dataCadastro) AS ano, MONTH(p.dataCadastro) AS mes, SUM(p.precoCusto * p.estoque) AS valorEntrada " + // ALTERADO para p.precoCusto
           "FROM Produto p " +
           "WHERE p.dataCadastro IS NOT NULL AND p.precoCusto IS NOT NULL " + // Garante que precoCusto não seja nulo no cálculo
           "GROUP BY YEAR(p.dataCadastro), MONTH(p.dataCadastro) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findValorEntradaEstoquePorMesRaw();
}