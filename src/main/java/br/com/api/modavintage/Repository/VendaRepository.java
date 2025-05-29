package br.com.api.modavintage.Repository; // Seu pacote

import br.com.api.modavintage.Model.Venda;
// br.com.api.modavintage.dto.VendasPorMesDTO; // 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Alterado para retornar List<Object[]>
    // Seleciona o ano, o mês e a soma do total da venda.
    // As funções YEAR() e MONTH() são mais padrão em JPQL.
    @Query("SELECT YEAR(v.dataVenda) AS ano, MONTH(v.dataVenda) AS mes, SUM(v.totalVenda) AS totalVendido " +
           "FROM Venda v " +
           "GROUP BY YEAR(v.dataVenda), MONTH(v.dataVenda) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findTotalVendasPorMesRaw(); // Renomeado para indicar que são dados "crus"
}