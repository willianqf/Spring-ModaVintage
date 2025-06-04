package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Service.ProdutoService;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.domain.Sort;      // Importar Sort
import org.springframework.data.web.PageableDefault; // Importar PageableDefault
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> salvarProduto(@RequestBody Produto produto) {
        Produto produtoSalvo = produtoService.salvarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    @GetMapping // Endpoint paginado para listar produtos ativos
    public ResponseEntity<Page<Produto>> listarProdutos(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Produto> paginaProdutos = produtoService.listarProdutos(nome, pageable); 
        return ResponseEntity.ok(paginaProdutos);
    }

    @GetMapping("/todos") // Endpoint para listar TODOS os produtos ATIVOS (sem paginação)
    public ResponseEntity<List<Produto>> listarTodosOsProdutos() {
    
        List<Produto> produtos = produtoService.listarTodosProdutosAtivos();
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        // Chama o método renomeado no serviço para buscar produto ativo
        return produtoService.buscarPorIdAtivo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoDetalhes) {
        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoDetalhes);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        try {
            // O método deletarProduto no serviço agora faz soft delete
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/relatorio/valor-entrada-estoque-mensal")
    public ResponseEntity<List<RelatorioMensalValorDTO>> getRelatorioValorEntradaEstoque() {
        try {
            List<RelatorioMensalValorDTO> relatorio = produtoService.getRelatorioValorEntradaEstoqueMensal();
            // Adicionado tratamento para lista vazia conforme boas práticas
            if (relatorio == null || relatorio.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de valor de entrada de estoque mensal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erro 500 genérico
        }
    }
}