package br.com.api.modavintage.Controller; // Seu pacote

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.PageRequest; // Importar PageRequest
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.domain.Sort; // Importar Sort
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Removido import de java.util.List se não for mais usado para GET /clientes
// import java.util.List; 

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

    // Endpoint GET /clientes atualizado para aceitar parâmetros de paginação e pesquisa
    @GetMapping
    public ResponseEntity<Page<Cliente>> listarClientes(
            @RequestParam(required = false) String nome, // Parâmetro de pesquisa opcional
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort // Ex: sort=nome,asc ou sort=id,desc
    ) {
        // Lógica para criar Sort object a partir do array de strings 'sort'
        // Exemplo simples: sort[0] é o campo, sort[1] é a direção (asc/desc)
        // Para múltiplos critérios de ordenação, o Spring lida com isso se você passar múltiplos sort=campo,direcao
        Sort sortOrder = Sort.by(sort[0]); // Campo base
        if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sortOrder = sortOrder.descending();
        } else {
            sortOrder = sortOrder.ascending();
        }
        // Para múltiplos sorts, seria algo como:
        // List<Sort.Order> orders = new ArrayList<>();
        // for (String sortParam : sort) {
        // String[] parts = sortParam.split(",");
        // orders.add(new Sort.Order(parts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, parts[0]));
        // }
        // Sort sortOrder = Sort.by(orders);

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Cliente> paginaClientes = clienteService.listarClientes(nome, pageable);

        // A resposta da Page já inclui se está vazia. O Spring lida com 204 No Content se a lista 'content' for vazia.
        // if (paginaClientes.isEmpty()) {
        // return ResponseEntity.noContent().build();
        // }
        return ResponseEntity.ok(paginaClientes);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Você pode querer logar o erro aqui também
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}