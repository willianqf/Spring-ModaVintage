package br.com.api.modavintage.Controller; // Seu pacote

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Service.ProdutoService;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO; // Mantido
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos") // Ou "/api/produtos"
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> salvarProduto(@RequestBody Produto produto) {
        Produto produtoSalvo = produtoService.salvarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    // Endpoint GET /produtos atualizado para aceitar o parâmetro 'nome'
    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos(
            @RequestParam(required = false) String nome // Parâmetro de query opcional
    ) {
        List<Produto> produtos = produtoService.listarProdutos(nome);
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 se a lista (filtrada ou não) estiver vazia
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoDetalhes) {
        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoDetalhes);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint para o relatório de valor de entrada de estoque mensal
    @GetMapping("/relatorio/valor-entrada-estoque-mensal")
    public ResponseEntity<List<RelatorioMensalValorDTO>> getRelatorioValorEntradaEstoque() {
        try {
            List<RelatorioMensalValorDTO> relatorio = produtoService.getRelatorioValorEntradaEstoqueMensal();
            if (relatorio.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de valor de entrada de estoque mensal: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}