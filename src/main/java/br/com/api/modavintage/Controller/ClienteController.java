package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Service.ClienteService;
import jakarta.validation.Valid; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException; // Importar
import org.springframework.validation.FieldError; // Importar

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> salvarCliente(@Valid @RequestBody Cliente cliente) { // Adicionar @Valid
        Cliente clienteSalvo = clienteService.salvarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente clienteDetalhes) { // Adicionar @Valid
        try {
            Cliente clienteAtualizado = clienteService.atualizarCliente(id, clienteDetalhes);
            return ResponseEntity.ok(clienteAtualizado);
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
                        (primeiro, segundo) -> primeiro // Em caso de múltiplos erros no mesmo campo, pega o primeiro
                ));
    }
    
    @GetMapping
    public ResponseEntity<Page<Cliente>> listarClientes(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<Cliente> paginaClientes = clienteService.listarClientes(nome, pageable);
        return ResponseEntity.ok(paginaClientes);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Cliente>> listarTodosOsClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientesAtivos();
        if (clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        return clienteService.buscarPorIdAtivo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}