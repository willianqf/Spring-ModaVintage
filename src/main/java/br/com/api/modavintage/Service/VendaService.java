package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Cliente; 
import br.com.api.modavintage.Model.ItemVenda;
import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Repository.VendaRepository;
import br.com.api.modavintage.dto.VendasPorMesDTO;
import br.com.api.modavintage.dto.RelatorioLucratividadeMensalDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Importações para Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VendaService {

    // ===== ADICIONADO: Logger para rastrear a lógica de negócio =====
    private static final Logger logger = LoggerFactory.getLogger(VendaService.class);

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Transactional
    public Venda salvarVenda(Venda vendaRequest) {
        Venda novaVenda = new Venda();
        novaVenda.setDataVenda(new Date());
        
        double valorTotalCalculado = 0.0;

        if (vendaRequest.getCliente() != null && vendaRequest.getCliente().getId() != null) {
            Long clienteId = vendaRequest.getCliente().getId();
            Cliente clienteAtivo = clienteService.buscarPorIdAtivo(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente ativo não encontrado com id: " + clienteId + 
                                                           ". Verifique se o cliente está ativo ou selecione outro."));
            novaVenda.setCliente(clienteAtivo);
            novaVenda.setNomeClienteSnapshot(clienteAtivo.getNome());
            novaVenda.setEmailClienteSnapshot(clienteAtivo.getEmail());
            novaVenda.setTelefoneClienteSnapshot(clienteAtivo.getTelefone());
        } else {
            novaVenda.setCliente(null);
            novaVenda.setNomeClienteSnapshot("Cliente Não Informado");
        }

        if (vendaRequest.getItens() == null || vendaRequest.getItens().isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter pelo menos um item.");
        }

        List<ItemVenda> itensProcessados = new ArrayList<>();
        for (ItemVenda itemRequest : vendaRequest.getItens()) {
            if (itemRequest.getProduto() == null || itemRequest.getProduto().getId() == null) {
                throw new IllegalArgumentException("Produto não especificado para um item da venda.");
            }
            if (itemRequest.getQuantidade() == null || itemRequest.getQuantidade() <= 0) {
                 throw new IllegalArgumentException("Quantidade inválida para o produto: " + (itemRequest.getProduto().getNome() != null ? itemRequest.getProduto().getNome() : itemRequest.getProduto().getId()));
            }

            Long produtoId = itemRequest.getProduto().getId();
            Produto produtoAtivo = produtoService.buscarPorIdAtivo(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto ativo não encontrado com id: " + produtoId + 
                                                           ". O produto pode estar inativo ou fora de estoque."));

            if (produtoAtivo.getEstoque() < itemRequest.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produtoAtivo.getNome() +
                                           ". Disponível: " + produtoAtivo.getEstoque() + 
                                           ", Solicitado: " + itemRequest.getQuantidade());
            }

            produtoAtivo.setEstoque(produtoAtivo.getEstoque() - itemRequest.getQuantidade());
            produtoService.salvarProduto(produtoAtivo);

            ItemVenda itemProcessado = new ItemVenda(produtoAtivo, itemRequest.getQuantidade());
            itemProcessado.setVenda(novaVenda);
            
            itensProcessados.add(itemProcessado);
            valorTotalCalculado += itemProcessado.getSubtotal();
        }
        
        novaVenda.setItens(itensProcessados);
        novaVenda.setTotalVenda(valorTotalCalculado);

        return vendaRepository.save(novaVenda);
    }

    /**
     * ===== MÉTODO MODIFICADO =====
     * Este método agora implementa a lógica para decidir qual tipo de busca fazer no banco de dados
     * com base nos filtros recebidos do controller. Adicionamos logs para cada caminho possível.
     */
    @Transactional(readOnly = true)
    public Page<Venda> listarVendas(String nomeCliente, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        // Lógica de filtro reestruturada para clareza
        if (nomeCliente != null && !nomeCliente.isBlank()) {
            logger.info("--> Lógica de Serviço: Filtrando por nomeCliente: '{}'", nomeCliente);
            // Assume que o VendaRepository tem este método. O Spring Data JPA o implementará.
            return vendaRepository.findByNomeClienteSnapshotContainingIgnoreCase(nomeCliente, pageable);
        } else if (dataInicio != null && dataFim != null) {
            logger.info("--> Lógica de Serviço: Filtrando por data: '{}' a '{}'", dataInicio, dataFim);
            LocalDateTime inicioDoDia = dataInicio.atStartOfDay();
            LocalDateTime fimDoDia = dataFim.atTime(23, 59, 59);
             // Assume que o VendaRepository tem este método.
            return vendaRepository.findByDataVendaBetween(inicioDoDia, fimDoDia, pageable);
        } else {
            logger.info("--> Lógica de Serviço: Nenhum filtro aplicado, buscando todas as vendas.");
            return vendaRepository.findAll(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Venda> buscarVendaPorId(Long id) { 
        return vendaRepository.findById(id);
    }

    @Transactional
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com id: " + id));

        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() != null && item.getProduto().getId() != null) {
                Produto produtoOriginal = produtoService.buscarPorIdQualquerStatus(item.getProduto().getId())
                        .orElse(null);

                if (produtoOriginal != null) {
                    produtoOriginal.setEstoque(produtoOriginal.getEstoque() + item.getQuantidade());
                    produtoService.salvarProduto(produtoOriginal);
                } else {
                    System.err.println("AVISO: Produto original com ID " + item.getProduto().getId() + 
                                       " não encontrado para estornar estoque da venda " + id + ".");
                }
            }
        }
        vendaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<VendasPorMesDTO> getRelatorioVendasMensal(){
        List<Object[]> resultados = vendaRepository.findTotalVendasPorMes();
        if(resultados == null){
            return new ArrayList<>();
        }
        return resultados.stream().map(record -> {
            // CORREÇÃO: Convertendo de forma segura de Number para Integer
            Integer ano = (record[0] != null) ? ((Number) record[0]).intValue() : 0;
            Integer mes = (record[1] != null) ? ((Number) record[1]).intValue() : 0;
            Double total = (record[2] != null) ? ((Number) record[2]).doubleValue() : 0.0;
            String mesAno = String.format("%d-%02d", ano, mes);
            return new VendasPorMesDTO(mesAno, total);
        }).collect(Collectors.toList());
    }

        @Transactional(readOnly = true)
        public List<RelatorioLucratividadeMensalDTO> getRelatorioLucratividadeMensal(){
            List<Object[]> resultados = vendaRepository.findReceitaECmvPorMes();
            if(resultados == null){
                return new ArrayList<>();
            }
            return resultados.stream().map(record -> {
                // CORREÇÃO: Convertendo de forma segura de Number para Integer
                Integer ano = (record[0] != null) ? ((Number) record[0]).intValue() : 0;
                Integer mes = (record[1] != null) ? ((Number) record[1]).intValue() : 0;
                Double receita = (record[2] != null) ? ((Number) record[2]).doubleValue() : 0.0;
                Double cmv = (record[3] != null) ? ((Number) record[3]).doubleValue() : 0.0;
                Double lucro = receita - cmv;
                String mesAno = String.format("%d-%02d", ano, mes);
                return new RelatorioLucratividadeMensalDTO(mesAno, receita, cmv, lucro);
            }).collect(Collectors.toList());
        }
}
