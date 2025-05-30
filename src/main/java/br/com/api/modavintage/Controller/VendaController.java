package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Service.VendaService;
import br.com.api.modavintage.dto.VendasPorMesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.PageRequest; // Importar PageRequest
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.domain.Sort; // Importar Sort
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils; // Importar StringUtils
import org.springframework.web.bind.annotation.*;

import java.util.List; // Para o DTO de relatório
import java.util.Map; // Para respostas de erro

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
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<Venda>> listarVendas(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(name = "sortBy", defaultValue = "dataVenda") String sortBy,
        @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
        // Adicionar @RequestParam para filtros (ex: por clienteId, por período) se necessário
    ) {
        Sort.Direction direction = Sort.Direction.DESC;
        if (sortDir.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }
        String propertyToSortBy = StringUtils.hasText(sortBy) ? sortBy : "dataVenda";
        Sort sortOrder = Sort.by(direction, propertyToSortBy);

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Venda> paginaVendas = vendaService.listarVendas(pageable /*, outros filtros */);
        return ResponseEntity.ok(paginaVendas);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

     @GetMapping("/relatorio/total-mensal")
    public ResponseEntity<List<VendasPorMesDTO>> getRelatorioTotalVendasMensal() {
        try {
            List<VendasPorMesDTO> relatorio = vendaService.getRelatorioVendasMensal();
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de vendas mensal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}