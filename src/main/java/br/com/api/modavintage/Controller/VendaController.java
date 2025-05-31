package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Service.VendaService;
import br.com.api.modavintage.dto.VendasPorMesDTO;
// Importe o novo DTO
import br.com.api.modavintage.dto.RelatorioLucratividadeMensalDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<Venda> salvarVenda(@RequestBody Venda venda) {
        Venda novaVenda = vendaService.salvarVenda(venda);
        return ResponseEntity.ok(novaVenda);
    }

    @GetMapping
    public ResponseEntity<Page<Venda>> listarVendas(@PageableDefault(size = 10, sort = "dataVenda") Pageable pageable) {
        Page<Venda> vendas = vendaService.listarVendas(pageable);
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarVendaPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id) // Corrigido na etapa anterior
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        vendaService.deletarVenda(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorio/total-mensal")
    public ResponseEntity<List<VendasPorMesDTO>> getRelatorioVendasMensal() {
        List<VendasPorMesDTO> relatorio = vendaService.getRelatorioVendasMensal();
        return ResponseEntity.ok(relatorio);
    }

    // NOVO ENDPOINT PARA RELATÓRIO DE LUCRATIVIDADE MENSAL
    @GetMapping("/relatorio/lucratividade-mensal")
    public ResponseEntity<List<RelatorioLucratividadeMensalDTO>> getRelatorioLucratividadeMensal() {
        List<RelatorioLucratividadeMensalDTO> relatorio = vendaService.getRelatorioLucratividadeMensal();
        return ResponseEntity.ok(relatorio);
    }
}