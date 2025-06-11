package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Service.VendaService;
import br.com.api.modavintage.dto.RelatorioLucratividadeMensalDTO;
import br.com.api.modavintage.dto.VendasPorMesDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Importações para Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    // ===== ADICIONADO: Logger para rastrear as requisições =====
    private static final Logger logger = LoggerFactory.getLogger(VendaController.class);

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<?> salvarVenda(@RequestBody Venda vendaRequest) {
        try {
            Venda vendaSalva = vendaService.salvarVenda(vendaRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(vendaSalva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && (e.getMessage().contains("não encontrado") || e.getMessage().contains("inativo"))) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("erro", e.getMessage()));
            }
            System.err.println("Erro inesperado ao salvar venda: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro interno ao processar a venda. Tente novamente mais tarde."));
        }
    }

    /**
     * ===== MÉTODO MODIFICADO =====
     * O método agora aceita parâmetros de requisição (@RequestParam) para filtrar as vendas.
     * Adicionamos um log para exibir os parâmetros recebidos em cada chamada.
     */
    @GetMapping
    public ResponseEntity<Page<Venda>> listarVendas(
            @RequestParam(required = false) String nomeCliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 10, sort = "dataVenda", direction = Sort.Direction.DESC) Pageable pageable) {

        // Log para diagnosticar os parâmetros recebidos
        logger.info(">>> REQUISIÇÃO RECEBIDA em /vendas com parâmetros: nomeCliente='{}', dataInicio='{}', dataFim='{}'",
                nomeCliente, dataInicio, dataFim);

        Page<Venda> vendas = vendaService.listarVendas(nomeCliente, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarVendaPorId(@PathVariable Long id) {
        return vendaService.buscarVendaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVenda(@PathVariable Long id) {
        try {
            vendaService.deletarVenda(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Venda não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
            }
            System.err.println("Erro inesperado ao deletar venda: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro interno ao tentar deletar a venda."));
        }
    }

    @GetMapping("/relatorio/total-mensal")
    public ResponseEntity<List<VendasPorMesDTO>> getRelatorioVendasMensal() {
        List<VendasPorMesDTO> relatorio = vendaService.getRelatorioVendasMensal();
        if (relatorio == null || relatorio.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/relatorio/lucratividade-mensal")
    public ResponseEntity<List<RelatorioLucratividadeMensalDTO>> getRelatorioLucratividadeMensal() {
        List<RelatorioLucratividadeMensalDTO> relatorio = vendaService.getRelatorioLucratividadeMensal();
        if (relatorio == null || relatorio.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(relatorio);
    }
}
