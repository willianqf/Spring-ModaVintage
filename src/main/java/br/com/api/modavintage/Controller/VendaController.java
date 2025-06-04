package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Service.VendaService;
import br.com.api.modavintage.dto.VendasPorMesDTO;
import br.com.api.modavintage.dto.RelatorioLucratividadeMensalDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus; // Importar HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Map; // Importar Map para corpo de erro JSON

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
        } catch (IllegalArgumentException e) {
            // Erros: "A venda deve conter pelo menos um item", "Produto não especificado", "Quantidade inválida"
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            // Erros: "Estoque insuficiente para o produto..."
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {

            if (e.getMessage() != null && (e.getMessage().contains("não encontrado") || e.getMessage().contains("inativo"))) {
                 return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("erro", e.getMessage()));
            }
            // Para outras RuntimeExceptions não esperadas durante o processo de salvar venda
            System.err.println("Erro inesperado ao salvar venda: " + e.getMessage()); // Log do erro no servidor
            e.printStackTrace(); // Para mais detalhes no log do servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro interno ao processar a venda. Tente novamente mais tarde."));
        }
    }

    @GetMapping
    public ResponseEntity<Page<Venda>> listarVendas(
        // Usando sort para dataVenda, com os mais recentes primeiro
        @PageableDefault(size = 10, sort = "dataVenda", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Venda> vendas = vendaService.listarVendas(pageable);
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
            // Se a venda não for encontrada para deleção
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