package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Service.ProdutoService;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO;
import jakarta.validation.Valid; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError; // Importar
import org.springframework.web.bind.MethodArgumentNotValidException; // Importar
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> salvarProduto(@Valid @RequestBody Produto produto) { // Adicionar @Valid
        Produto produtoSalvo = produtoService.salvarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Valid @RequestBody Produto produtoDetalhes) { // Adicionar @Valid
        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoDetalhes);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }
    
    // Handler para erros de validação neste controller
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (primeiro, segundo) -> primeiro
                ));
    }

    @GetMapping
    public ResponseEntity<Page<Produto>> listarProdutos(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Produto> paginaProdutos = produtoService.listarProdutos(nome, pageable); 
        return ResponseEntity.ok(paginaProdutos);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Produto>> listarTodosOsProdutos() {
    
        List<Produto> produtos = produtoService.listarTodosProdutosAtivos();
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        return produtoService.buscarPorIdAtivo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        try {
            List<RelatorioMensalValorDTO> relatorio = produtoService.getRelatorioValorEntradaEstoqueMensal();
            if (relatorio == null || relatorio.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de valor de entrada de estoque mensal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}