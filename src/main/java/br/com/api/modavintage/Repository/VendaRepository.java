package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query("SELECT YEAR(v.dataVenda) as ano, MONTH(v.dataVenda) as mes, SUM(v.totalVenda) as total " +
           "FROM Venda v " +
           "GROUP BY YEAR(v.dataVenda), MONTH(v.dataVenda) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findTotalVendasPorMes();


    // Query ajustada para usar o campo de snapshot do preço unitário do item
    @Query("SELECT " +
           "    YEAR(v.dataVenda) AS ano, " +
           "    MONTH(v.dataVenda) AS mes, " +
           "    SUM(iv.quantidade * iv.precoUnitarioSnapshot) AS receita, " + // Alterado para precoUnitarioSnapshot
           "    SUM(iv.quantidade * p.precoCusto) AS cmv " +
           "FROM Venda v " +
           "JOIN v.itens iv " +
           "JOIN iv.produto p " + // O join com Produto (p) ainda é necessário para buscar p.precoCusto
           "WHERE p.precoCusto IS NOT NULL " + // Mantém a condição para precoCusto
           "GROUP BY YEAR(v.dataVenda), MONTH(v.dataVenda) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findReceitaECmvPorMes();
}