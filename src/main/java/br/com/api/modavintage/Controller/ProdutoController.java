package br.com.api.modavintage.Controller; // Seu pacote

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Service.ProdutoService;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.PageRequest; // Importar PageRequest
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.domain.Sort; // Opcional: para ordenação
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // Endpoint GET /produtos atualizado para aceitar parâmetros de paginação e pesquisa
    @GetMapping
    public ResponseEntity<Page<Produto>> listarProdutos(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page,     // Número da página, padrão 0
            @RequestParam(defaultValue = "10") int size,    // Tamanho da página, padrão 10
            @RequestParam(defaultValue = "id,asc") String[] sort // Opcional: Parâmetros de ordenação, ex: "nome,asc" ou "id,desc"
    ) {
        // Criação do objeto Pageable com ordenação
        // Exemplo: sort=nome,asc ou sort=id,desc (pode receber múltiplos sort=campo,direcao)
        // Por simplicidade, vamos usar a ordenação padrão ou uma fixa se 'sort' não for robustamente tratado.
        // Aqui, vamos usar a ordenação que vem do request ou uma padrão se não vier.
        // String[] sortParams = sort.split(",");
        // Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        // Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        // Simplificando para ordenação por ID por enquanto:
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));


        Page<Produto> paginaProdutos = produtoService.listarProdutos(nome, pageable);

        if (paginaProdutos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 se a página estiver vazia
        }
        return ResponseEntity.ok(paginaProdutos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        // ... (como antes) ...
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoDetalhes) {
        // ... (como antes) ...
        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoDetalhes);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        // ... (como antes) ...
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint de relatório (mantém como está)
    @GetMapping("/relatorio/valor-entrada-estoque-mensal")
    public ResponseEntity<List<RelatorioMensalValorDTO>> getRelatorioValorEntradaEstoque() {
        // ... (como antes) ...
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