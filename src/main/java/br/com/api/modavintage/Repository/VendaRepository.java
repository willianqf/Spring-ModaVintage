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
    @Query("SELECT EXTRACT(YEAR FROM v.dataVenda) as ano, EXTRACT(MONTH FROM v.dataVenda) as mes, SUM(v.totalVenda) as total " +
           "FROM Venda v " +
           "GROUP BY EXTRACT(YEAR FROM v.dataVenda), EXTRACT(MONTH FROM v.dataVenda) " +
           "ORDER BY ano DESC, mes DESC")
    List<Object[]> findTotalVendasPorMes();

    // CORREÇÃO: Trocado YEAR() e MONTH() por EXTRACT() para compatibilidade com PostgreSQL
    @Query("SELECT EXTRACT(YEAR FROM v.dataVenda) as ano, EXTRACT(MONTH FROM v.dataVenda) as mes, " +
           "SUM(iv.precoUnitarioSnapshot * iv.quantidade) as receita, " +
           "SUM(p.precoCusto * iv.quantidade) as cmv " +
           "FROM Venda v JOIN v.itens iv JOIN iv.produto p " +
           "GROUP BY EXTRACT(YEAR FROM v.dataVenda), EXTRACT(MONTH FROM v.dataVenda) " +
           "ORDER BY ano DESC, mes DESC")
    List<Object[]> findReceitaECmvPorMes();
}
