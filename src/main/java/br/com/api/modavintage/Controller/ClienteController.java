package br.com.api.modavintage.Controller; // Seu pacote

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes") // Ou "/api/clientes"
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> salvarCliente(@RequestBody Cliente cliente) {
        Cliente clienteSalvo = clienteService.salvarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    // Endpoint GET /clientes atualizado para aceitar o parâmetro 'nome'
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(
            @RequestParam(required = false) String nome // Parâmetro de query opcional
    ) {
        List<Cliente> clientes = clienteService.listarClientes(nome);
        if (clientes.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 se a lista estiver vazia
        }
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteDetalhes) {
        try {
            Cliente clienteAtualizado = clienteService.atualizarCliente(id, clienteDetalhes);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (RuntimeException e) {
            // Se o serviço lançar exceção (ex: cliente não encontrado)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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