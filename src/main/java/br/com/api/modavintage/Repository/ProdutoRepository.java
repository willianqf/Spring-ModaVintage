package br.com.api.modavintage.Repository; // 

import br.com.api.modavintage.Model.Produto;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List; // Manter se usado por findValorEntradaEstoquePorMesRaw

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Método de pesquisa por nome agora retorna Page<Produto> e aceita Pageable
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // O JpaRepository já fornece findAll(Pageable pageable) que retorna Page<Produto>

    // Método para o relatório (mantém como está, não precisa de paginação aqui geralmente)
    @Query("SELECT YEAR(p.dataCadastro) AS ano, MONTH(p.dataCadastro) AS mes, SUM(p.preco * p.estoque) AS valorEntrada " +
           "FROM Produto p " +
           "WHERE p.dataCadastro IS NOT NULL " +
           "GROUP BY YEAR(p.dataCadastro), MONTH(p.dataCadastro) " +
           "ORDER BY ano ASC, mes ASC")
    List<Object[]> findValorEntradaEstoquePorMesRaw();
}