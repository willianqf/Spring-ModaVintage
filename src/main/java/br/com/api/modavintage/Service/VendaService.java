package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Cliente; // Import Cliente
import br.com.api.modavintage.Model.ItemVenda;
import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Repository.VendaRepository;
// Removido ClienteRepository e ProdutoRepository daqui, usaremos os services
import br.com.api.modavintage.dto.VendasPorMesDTO;
import br.com.api.modavintage.dto.RelatorioLucratividadeMensalDTO; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoService produtoService; // Usar ProdutoService para buscar produtos ativos

    @Autowired
    private ClienteService clienteService; // Usar ClienteService para buscar clientes ativos

    @Transactional
    public Venda salvarVenda(Venda vendaRequest) { // 'vendaRequest' é o DTO/payload da requisição
        Venda novaVenda = new Venda();
        novaVenda.setDataVenda(new Date());
        
        double valorTotalCalculado = 0.0;

        // Lidar com o Cliente e Snapshot de Cliente
        if (vendaRequest.getCliente() != null && vendaRequest.getCliente().getId() != null) {
            Long clienteId = vendaRequest.getCliente().getId();
            // Busca apenas cliente ativo. Se o frontend enviar ID de cliente inativo, a venda não deve ser com ele.
            Cliente clienteAtivo = clienteService.buscarPorIdAtivo(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente ativo não encontrado com id: " + clienteId + 
                                                       ". Verifique se o cliente está ativo ou selecione outro."));
            novaVenda.setCliente(clienteAtivo); // Link para o cliente original
            // Preencher snapshots do cliente
            novaVenda.setNomeClienteSnapshot(clienteAtivo.getNome());
            novaVenda.setEmailClienteSnapshot(clienteAtivo.getEmail());
            novaVenda.setTelefoneClienteSnapshot(clienteAtivo.getTelefone());
        } else {
            // Venda anônima, snapshots de cliente ficam nulos
            novaVenda.setCliente(null);
            novaVenda.setNomeClienteSnapshot("Cliente Não Informado"); // Ou null, ou um valor padrão
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
            // Busca apenas produto ativo. Se o produto estiver inativo, não pode ser vendido.
            Produto produtoAtivo = produtoService.buscarPorIdAtivo(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto ativo não encontrado com id: " + produtoId + 
                                                           ". O produto pode estar inativo ou fora de estoque."));

            if (produtoAtivo.getEstoque() < itemRequest.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produtoAtivo.getNome() +
                                           ". Disponível: " + produtoAtivo.getEstoque() + 
                                           ", Solicitado: " + itemRequest.getQuantidade());
            }

            // Atualiza o estoque do produto
            produtoAtivo.setEstoque(produtoAtivo.getEstoque() - itemRequest.getQuantidade());
            produtoService.salvarProduto(produtoAtivo); // Salva a alteração de estoque através do service

            // Cria o ItemVenda usando o construtor que popula os snapshots
            ItemVenda itemProcessado = new ItemVenda(produtoAtivo, itemRequest.getQuantidade());
            itemProcessado.setVenda(novaVenda); // Associa o item à nova venda
            
            itensProcessados.add(itemProcessado);
            valorTotalCalculado += itemProcessado.getSubtotal();
        }
        
        novaVenda.setItens(itensProcessados);
        novaVenda.setTotalVenda(valorTotalCalculado);

        return vendaRepository.save(novaVenda);
    }

    @Transactional(readOnly = true)
    public Page<Venda> listarVendas(Pageable pageable) {
        // Ao listar vendas, os snapshots já contêm os dados históricos.
        // As entidades Produto e Cliente associadas podem estar inativas.
        return vendaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Venda> buscarVendaPorId(Long id) { // Nome do método CORRIGIDO para "buscarVendaPorId"
        // Similar à listagem, os snapshots devem ser usados para exibir dados históricos.
        return vendaRepository.findById(id);
    }

    @Transactional
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com id: " + id));

        // Estornar o estoque dos produtos
        for (ItemVenda item : venda.getItens()) {
            // Precisamos do produto original para estornar o estoque.
            // Se o produto foi fisicamente deletado (o que não deve acontecer com soft delete),
            // ou se o link em ItemVenda.produto for nulo por algum motivo, isso falharia.
            // Com soft delete, o produtoService.buscarPorIdQualquerStatus pode buscar o produto
            // mesmo que esteja inativo para atualizar seu estoque.
            if (item.getProduto() != null && item.getProduto().getId() != null) {
                Produto produtoOriginal = produtoService.buscarPorIdQualquerStatus(item.getProduto().getId())
                    .orElse(null); // Decide como lidar se o produto original não for encontrado de forma alguma

                if (produtoOriginal != null) {
                    produtoOriginal.setEstoque(produtoOriginal.getEstoque() + item.getQuantidade());
                    produtoService.salvarProduto(produtoOriginal); // Salva via service
                } else {
                    // Logar um aviso/erro: Produto original do item de venda não encontrado para estorno.
                    System.err.println("AVISO: Produto original com ID " + item.getProduto().getId() + 
                                       " não encontrado para estornar estoque da venda " + id + ".");
                }
            }
        }
        vendaRepository.deleteById(id); // Deleta a venda e seus itens (devido ao CascadeType.ALL)
    }

    @Transactional(readOnly = true)
    public List<VendasPorMesDTO> getRelatorioVendasMensal() {
        List<Object[]> resultados = vendaRepository.findTotalVendasPorMes(); 
        if (resultados == null) {
            return new ArrayList<>();
        }
        return resultados.stream()
                .map(record -> {
                    Integer ano = (Integer) record[0];
                    Integer mes = (Integer) record[1];
                    Double total = (record[2] == null) ? 0.0 : ((Number) record[2]).doubleValue(); 
                    String mesAno = String.format("%d-%02d", ano, mes);
                    return new VendasPorMesDTO(mesAno, total);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RelatorioLucratividadeMensalDTO> getRelatorioLucratividadeMensal() {
        List<Object[]> resultados = vendaRepository.findReceitaECmvPorMes();
        if (resultados == null) {
            return new ArrayList<>();
        }
        return resultados.stream()
                .map(record -> {
                    Integer ano = (Integer) record[0];
                    Integer mes = (Integer) record[1];
                    Double receita = (record[2] == null) ? 0.0 : ((Number) record[2]).doubleValue();
                    Double cmv = (record[3] == null) ? 0.0 : ((Number) record[3]).doubleValue();
                    Double lucroBruto = receita - cmv;
                    String periodo = String.format("%d-%02d", ano, mes);
                    return new RelatorioLucratividadeMensalDTO(periodo, receita, cmv, lucroBruto);
                })
                .collect(Collectors.toList());
    }
}