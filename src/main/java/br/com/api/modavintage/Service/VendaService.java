package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.ItemVenda;
import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Repository.ClienteRepository;
import br.com.api.modavintage.Repository.ProdutoRepository;
import br.com.api.modavintage.Repository.VendaRepository;
import br.com.api.modavintage.dto.VendasPorMesDTO;
// Importe o novo DTO que criamos
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
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public Venda salvarVenda(Venda venda) {
        venda.setDataVenda(new Date());
        double valorTotalCalculado = 0.0;

        if (venda.getCliente() != null && venda.getCliente().getId() != null) {
            clienteRepository.findById(venda.getCliente().getId())
                .ifPresentOrElse(venda::setCliente,
                                 () -> { throw new RuntimeException("Cliente não encontrado com id: " + venda.getCliente().getId());});
        } else {
            venda.setCliente(null);
        }

        List<ItemVenda> itensProcessados = new ArrayList<>();
        for (ItemVenda item : venda.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + item.getProduto().getId()));

            if (produto.getEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
            produtoRepository.save(produto);

            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco()); // Preço de venda do produto
            // Não precisamos mais do precoCustoUnitarioNaVenda aqui, conforme nossa última discussão
            
            item.setVenda(venda);
            itensProcessados.add(item);
            valorTotalCalculado += item.getSubtotal();
        }
        venda.setItens(itensProcessados);
        // Use o nome correto do campo da sua entidade Venda para o valor total
        venda.setTotalVenda(valorTotalCalculado); // Assumindo que o campo em Venda.java é totalVenda

        return vendaRepository.save(venda);
    }

    @Transactional(readOnly = true)
    public Page<Venda> listarVendas(Pageable pageable) {
        return vendaRepository.findAll(pageable);
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
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }
        vendaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<VendasPorMesDTO> getRelatorioVendasMensal() {
        // CORRIGIDO: Chamando o método correto do VendaRepository
        List<Object[]> resultados = vendaRepository.findTotalVendasPorMes(); 
        if (resultados == null) {
            return new ArrayList<>();
        }
        return resultados.stream()
                .map(record -> {
                    Integer ano = (Integer) record[0];
                    Integer mes = (Integer) record[1];
                    // O SUM pode retornar null se não houver vendas, então tratamos isso.
                    Double total = (record[2] == null) ? 0.0 : ((Number) record[2]).doubleValue(); 
                    String mesAno = String.format("%d-%02d", ano, mes);
                    return new VendasPorMesDTO(mesAno, total);
                })
                .collect(Collectors.toList());
    }

    // NOVO MÉTODO para o relatório de lucratividade
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

        @Transactional(readOnly = true)
    public Optional<Venda> buscarPorId(Long id) { // Nome do método CORRIGIDO para "buscarPorId"
        return vendaRepository.findById(id);
    }
}