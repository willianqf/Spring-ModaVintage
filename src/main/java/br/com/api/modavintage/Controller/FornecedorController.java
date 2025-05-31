package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Fornecedor;
import br.com.api.modavintage.Service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
// Removido PageRequest e List<Sort.Order>, ArrayList
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.domain.Sort;      // Importar Sort
import org.springframework.data.web.PageableDefault; // Importar PageableDefault
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Removido StringUtils se não for mais usado em outros lugares após esta mudança

// import java.util.ArrayList; // Não mais necessário para ordenação manual
// import java.util.List;    // Não mais necessário para List<String> sortParams

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
            // Deixe o Spring injetar e popular o Pageable diretamente
            // A URL do frontend como ?page=0&size=10&sort=nome,ASC será interpretada corretamente
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        // O objeto 'pageable' já virá configurado com os parâmetros da requisição (page, size, sort).
        // Se 'sort' não for passado na URL, o @PageableDefault (sort = "id", direction = Sort.Direction.ASC) será usado.
        // Se 'sort=nome,ASC' for passado, ele sobrescreverá o default.

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
            // Se o serviço lança uma exceção específica para não encontrado, você pode capturá-la.
            // Por enquanto, assumindo que lança RuntimeException se não encontrar.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}