package br.com.api.modavintage.Repository; // Seu pacote

import br.com.api.modavintage.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Novo método para buscar produtos por nome contendo o termo
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Método para buscar produtos por categoria (
    List<Produto> findByCategoriaContainingIgnoreCase(String categoria);

    // Método para o relatório de valor de entrada de estoque por mês
    @Query("SELECT YEAR(p.dataCadastro) AS ano, MONTH(p.dataCadastro) AS mes, SUM(p.preco * p.estoque) AS valorEntrada " +
           "FROM Produto p " +
           "WHERE p.dataCadastro IS NOT NULL " +
           "GROUP BY YEAR(p.dataCadastro), MONTH(p.dataCadastro) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findValorEntradaEstoquePorMesRaw();
}