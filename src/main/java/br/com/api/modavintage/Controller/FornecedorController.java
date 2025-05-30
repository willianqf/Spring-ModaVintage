package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Fornecedor;
import br.com.api.modavintage.Service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils; // Para StringUtils.hasText

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @PostMapping
    public ResponseEntity<Fornecedor> salvarFornecedor(@RequestBody Fornecedor fornecedor) {
        Fornecedor fornecedorSalvo = fornecedorService.salvarFornecedor(fornecedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorSalvo);
    }

    @GetMapping
    public ResponseEntity<Page<Fornecedor>> listarFornecedores(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // Espera múltiplos parâmetros sort, ex: ?sort=nome,asc&sort=id,desc
            // Ou um único ?sort=nome,asc
            @RequestParam(name = "sort", required = false) List<String> sortParams // Renomeado para clareza
    ) {
        List<Sort.Order> orders = new ArrayList<>();
        System.out.println("Teste"); // Colocado o SystemOut 
        if (sortParams != null && !sortParams.isEmpty()) {
            for (String sortParam : sortParams) { // Cada sortParam deve ser "propriedade,direcao" ou "propriedade"
                String[] parts = sortParam.split(",");
                String property = parts[0].trim();
                
                if (StringUtils.hasText(property)) { // Garante que a propriedade não seja vazia
                    Sort.Direction direction = Sort.Direction.ASC; // Padrão ASC
                    if (parts.length > 1 && StringUtils.hasText(parts[1]) && parts[1].trim().equalsIgnoreCase("desc")) {
                        direction = Sort.Direction.DESC;
                    }
                    orders.add(new Sort.Order(direction, property));
                }
            }
        }
        
        // Se nenhuma ordenação válida foi criada a partir dos parâmetros (ou nenhum parâmetro sort foi enviado),
        // aplica uma ordenação padrão.
        if (orders.isEmpty()) {
             orders.add(new Sort.Order(Sort.Direction.ASC, "id")); 
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        
        Page<Fornecedor> paginaFornecedores = fornecedorService.listarFornecedores(nome, pageable);

        return ResponseEntity.ok(paginaFornecedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarFornecedorPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFornecedor(@PathVariable Long id, @RequestBody Fornecedor fornecedorDetalhes) {
        try {
            Fornecedor fornecedorAtualizado = fornecedorService.atualizarFornecedor(id, fornecedorDetalhes);
            return ResponseEntity.ok(fornecedorAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFornecedor(@PathVariable Long id) {
        try {
            fornecedorService.deletarFornecedor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}