package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils; 
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> salvarCliente(@RequestBody Cliente cliente) {
        Cliente clienteSalvo = clienteService.salvarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @GetMapping // Endpoint paginado para listar clientes ativos
    public ResponseEntity<Page<Cliente>> listarClientes(
            @RequestParam(required = false) String nome,
            // Usando @PageableDefault para simplificar a obtenção do Pageable
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<Cliente> paginaClientes = clienteService.listarClientes(nome, pageable); //  busca ativos
        return ResponseEntity.ok(paginaClientes);
    }

    @GetMapping("/todos") // Endpoint para listar TODOS os clientes ATIVOS (sem paginação)
    public ResponseEntity<List<Cliente>> listarTodosOsClientes() {
        // Chama o método renomeado no serviço
        List<Cliente> clientes = clienteService.listarTodosClientesAtivos();
        if (clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        // Chama o método renomeado no serviço para buscar cliente ativo
        return clienteService.buscarPorIdAtivo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteDetalhes) {
        try {
            Cliente clienteAtualizado = clienteService.atualizarCliente(id, clienteDetalhes);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (RuntimeException e) {
            // Retornar um corpo de erro estruturado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

                // Metodo modificado para soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Se o serviço lançar exceção por cliente não encontrado para deletar
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}