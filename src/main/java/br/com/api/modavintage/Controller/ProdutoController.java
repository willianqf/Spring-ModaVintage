package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Service.ProdutoService;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // ... (salvarProduto, listarProdutos - paginado, buscarProdutoPorId, etc. como antes) ...

    @PostMapping
    public ResponseEntity<Produto> salvarProduto(@RequestBody Produto produto) {
        Produto produtoSalvo = produtoService.salvarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    @GetMapping // Endpoint paginado
    public ResponseEntity<Page<Produto>> listarProdutos(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        String propertyToSortBy = StringUtils.hasText(sortBy) ? sortBy : "id";
        Sort sortOrder = Sort.by(direction, propertyToSortBy);
        
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Produto> paginaProdutos = produtoService.listarProdutos(nome, pageable);
        return ResponseEntity.ok(paginaProdutos);
    }

    // NOVO ENDPOINT PARA LISTAR TODOS OS PRODUTOS
    @GetMapping("/todos")
    public ResponseEntity<List<Produto>> listarTodosOsProdutos() {
        List<Produto> produtos = produtoService.listarTodosProdutos();
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
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


    @GetMapping("/relatorio/valor-entrada-estoque-mensal")
    public ResponseEntity<List<RelatorioMensalValorDTO>> getRelatorioValorEntradaEstoque() {
        // ... (como antes) ...
        try {
            List<RelatorioMensalValorDTO> relatorio = produtoService.getRelatorioValorEntradaEstoqueMensal();
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de valor de entrada de estoque mensal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}