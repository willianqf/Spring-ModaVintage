package br.com.api.modavintage.Controller;


import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.api.modavintage.dto.VendasPorMesDTO; // Importar DTO
import org.springframework.http.ResponseEntity; // 


import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<?> salvarVenda(@RequestBody Venda vendaRequest) {
        try {
            Venda vendaSalva = vendaService.salvarVenda(vendaRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(vendaSalva);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Para exceções como "Produto não encontrado" ou "Cliente não encontrado"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Venda>> listarVendas() {
        List<Venda> vendas = vendaService.listarVendas();
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarVendaPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        try {
            vendaService.deletarVenda(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Ou e.getMessage()
        }
    }

     @GetMapping("/relatorio/total-mensal")
    public ResponseEntity<List<VendasPorMesDTO>> getRelatorioTotalVendasMensal() {
        try {
            List<VendasPorMesDTO> relatorio = vendaService.getRelatorioVendasMensal();
            if (relatorio.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 se não houver dados
            }
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            // Logar o erro e retornar um erro interno do servidor
            System.err.println("Erro ao gerar relatório de vendas mensal: " + e.getMessage());
            return ResponseEntity.internalServerError().build(); // 500
        }
    }
}