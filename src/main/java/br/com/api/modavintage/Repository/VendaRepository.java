package br.com.api.modavintage.Repository;

import br.com.api.modavintage.Model.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Busca por nome de cliente (para filtro de vendas)
    Page<Venda> findByNomeClienteSnapshotContainingIgnoreCase(String nomeCliente, Pageable pageable);

    // Busca por intervalo de datas (para filtro de vendas)
    Page<Venda> findByDataVendaBetween(LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);

    // Relatório de total de vendas por mês (já estava correto)
    @Query("SELECT FUNCTION('YEAR', v.dataVenda), FUNCTION('MONTH', v.dataVenda), SUM(v.totalVenda) " +
           "FROM Venda v " +
           "GROUP BY FUNCTION('YEAR', v.dataVenda), FUNCTION('MONTH', v.dataVenda) " +
           "ORDER BY FUNCTION('YEAR', v.dataVenda) DESC, FUNCTION('MONTH', v.dataVenda) DESC")
    List<Object[]> findTotalVendasPorMes();

    /**
     * ===== QUERY CORRIGIDA =====
     * A cláusula GROUP BY foi alterada para usar as expressões de função completas
     * em vez de aliases. Isso garante a compatibilidade com o banco de dados H2.
     */
    @Query("SELECT FUNCTION('YEAR', v.dataVenda) as ano, " +
           "       FUNCTION('MONTH', v.dataVenda) as mes, " +
           "       SUM(iv.precoUnitarioSnapshot * iv.quantidade) as receita, " +
           "       SUM(p.precoCusto * iv.quantidade) as cmv " +
           "FROM Venda v " +
           "JOIN v.itens iv " +
           "JOIN iv.produto p " +
           "GROUP BY FUNCTION('YEAR', v.dataVenda), FUNCTION('MONTH', v.dataVenda) " +
           "ORDER BY ano DESC, mes DESC")
    List<Object[]> findReceitaECmvPorMes();
}
