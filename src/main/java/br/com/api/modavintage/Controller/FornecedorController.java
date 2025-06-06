package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Fornecedor;
import br.com.api.modavintage.Service.FornecedorService;
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

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @PostMapping
    public ResponseEntity<Fornecedor> salvarFornecedor(@Valid @RequestBody Fornecedor fornecedor) { // Adicionar @Valid
        Fornecedor fornecedorSalvo = fornecedorService.salvarFornecedor(fornecedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFornecedor(@PathVariable Long id, @Valid @RequestBody Fornecedor fornecedorDetalhes) { // Adicionar @Valid
        try {
            Fornecedor fornecedorAtualizado = fornecedorService.atualizarFornecedor(id, fornecedorDetalhes);
            return ResponseEntity.ok(fornecedorAtualizado);
        } catch (RuntimeException e) {
            // Aqui mantemos o retorno da mensagem, pois é um erro de "Não Encontrado", não de validação.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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

    // ... O resto do controller (GET, DELETE, etc.) permanece igual

    @GetMapping
    public ResponseEntity<Page<Fornecedor>> listarFornecedores(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        System.out.println("Controller: Pageable resolvido pelo Spring: " + pageable);
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                System.out.println("Controller: Ordenando por: " + order.getProperty() + " -> " + order.getDirection());
            });
        } else {
             System.out.println("Controller: Nenhuma ordenação específica solicitada, usando default ou nenhuma.");
        }

        Page<Fornecedor> paginaFornecedores = fornecedorService.listarFornecedores(nome, pageable);
        return ResponseEntity.ok(paginaFornecedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarFornecedorPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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