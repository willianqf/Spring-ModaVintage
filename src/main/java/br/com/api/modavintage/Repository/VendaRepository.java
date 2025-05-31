package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Venda;
// import br.com.api.modavintage.dto.VendasPorMesDTO; // Se não estiver usando, pode remover
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Método existente para total de vendas por mês - CORRIGIDO
    @Query("SELECT YEAR(v.dataVenda) as ano, MONTH(v.dataVenda) as mes, SUM(v.totalVenda) as total " + // ALTERADO de v.valorTotal para v.totalVenda
           "FROM Venda v " +
           "GROUP BY YEAR(v.dataVenda), MONTH(v.dataVenda) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findTotalVendasPorMes();


    // NOVO MÉTODO para Receita e CMV por Mês (este deve estar correto se ItemVenda e Produto estiverem ok)
    @Query("SELECT " +
           "    YEAR(v.dataVenda) AS ano, " +
           "    MONTH(v.dataVenda) AS mes, " +
           "    SUM(iv.quantidade * iv.precoUnitario) AS receita, " +
           "    SUM(iv.quantidade * p.precoCusto) AS cmv " +
           "FROM Venda v " +
           "JOIN v.itens iv " +
           "JOIN iv.produto p " +
           "WHERE p.precoCusto IS NOT NULL " +
           "GROUP BY YEAR(v.dataVenda), MONTH(v.dataVenda) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findReceitaECmvPorMes();
}