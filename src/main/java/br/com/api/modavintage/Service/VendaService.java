package br.com.api.modavintage.Service;


import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Model.ItemVenda;
import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Repository.ClienteRepository;
import br.com.api.modavintage.Repository.ProdutoRepository;
import br.com.api.modavintage.Repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.modavintage.dto.VendasPorMesDTO; // Importar DTO
import java.util.stream.Collectors; // Se precisar de mapeamento para nativeQuery
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired(required = false) // Opcional, se nem toda venda tiver cliente
    private ClienteRepository clienteRepository;

    @Transactional
    public Venda salvarVenda(Venda vendaRequest) {
        if (vendaRequest.getItens() == null || vendaRequest.getItens().isEmpty()) {
            throw new IllegalArgumentException("Uma venda deve ter pelo menos um item.");
        }

        Venda novaVenda = new Venda();
        novaVenda.setDataVenda(new Date());

        // Lidar com o cliente (se houver)
        if (vendaRequest.getCliente() != null && vendaRequest.getCliente().getId() != null) {
            Cliente cliente = clienteRepository.findById(vendaRequest.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente com ID " + vendaRequest.getCliente().getId() + " não encontrado."));
            novaVenda.setCliente(cliente);
        } else if (vendaRequest.getCliente() != null && vendaRequest.getCliente().getId() == null) {

             throw new IllegalArgumentException("Cliente informado sem ID. Cadastre o cliente primeiro ou forneça um ID válido.");
        }


        double totalCalculado = 0.0;

        for (ItemVenda itemRequest : vendaRequest.getItens()) {
            if (itemRequest.getProduto() == null || itemRequest.getProduto().getId() == null) {
                throw new IllegalArgumentException("Item da venda não especifica um produto válido.");
            }
            if (itemRequest.getQuantidadeVendida() == null || itemRequest.getQuantidadeVendida() <= 0) {
                throw new IllegalArgumentException("Quantidade vendida para o produto deve ser maior que zero.");
            }

            Produto produtoEmEstoque = produtoRepository.findById(itemRequest.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto com ID " + itemRequest.getProduto().getId() + " não encontrado."));

            if (produtoEmEstoque.getEstoque() < itemRequest.getQuantidadeVendida()) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + produtoEmEstoque.getNome() +
                                                ". Disponível: " + produtoEmEstoque.getEstoque() +
                                                ", Solicitado: " + itemRequest.getQuantidadeVendida());
            }

            // Atualiza o estoque do produto
            produtoEmEstoque.setEstoque(produtoEmEstoque.getEstoque() - itemRequest.getQuantidadeVendida());
            produtoRepository.save(produtoEmEstoque);

            // Cria o ItemVenda gerenciado
            ItemVenda itemVendaReal = new ItemVenda();
            itemVendaReal.setProduto(produtoEmEstoque);
            itemVendaReal.setQuantidadeVendida(itemRequest.getQuantidadeVendida());
            itemVendaReal.setPrecoUnitario(produtoEmEstoque.getPreco()); // Preço atual do produto
            itemVendaReal.setVenda(novaVenda); // Associa o item à nova venda

            novaVenda.getItens().add(itemVendaReal); // Adiciona o item à lista da venda
            totalCalculado += itemVendaReal.getPrecoUnitario() * itemVendaReal.getQuantidadeVendida();
        }

        novaVenda.setTotalVenda(totalCalculado);
        return vendaRepository.save(novaVenda);
    }

    public List<Venda> listarVendas() {

        return vendaRepository.findAll();
    }

    public Optional<Venda> buscarPorId(Long id) {
        // Similar a listarVendas, buscarPorId pode precisar de EntityGraph para carregar itens e cliente
        // de forma otimizada se eles forem LAZY.
        return vendaRepository.findById(id);
    }

    @Transactional
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda com ID " + id + " não encontrada."));

        // Estornar o estoque dos produtos ao deletar a venda
        // for (ItemVenda item : venda.getItens()) {
        //     Produto produto = item.getProduto();
        //     produto.setEstoque(produto.getEstoque() + item.getQuantidadeVendida());
        //     produtoRepository.save(produto);
        // }
        vendaRepository.delete(venda); // CascadeType.ALL em Venda.itens deve remover os ItemVenda associados
    }

    // Se precisar de busca de vendas por período ou cliente, adicione os métodos aqui
    // public List<Venda> buscarVendasPorCliente(Long clienteId) { ... }
    //  public List<Venda> buscarVendasPorData(Date inicio, Date fim) { ... }

 @Transactional(readOnly = true)
    public List<VendasPorMesDTO> getRelatorioVendasMensal() {
        List<Object[]> resultados = vendaRepository.findTotalVendasPorMesRaw();
        if (resultados == null) {
            return new ArrayList<>(); // Retorna lista vazia se não houver resultados
        }
        return resultados.stream()
                .map(record -> {
                    Integer ano = (Integer) record[0];
                    Integer mes = (Integer) record[1];
                    // O SUM pode retornar Long ou Double dependendo do dialeto e do tipo original
                    // Se totalVenda é Double, SUM(totalVenda) geralmente é Double.
                    // Se for Long, você precisaria converter para Double se o DTO espera Double.
                    Double total = ((Number) record[2]).doubleValue(); // Casting seguro para Number e depois para double

                    // Formata mesAno como "YYYY-MM"
                    String mesAno = String.format("%d-%02d", ano, mes);
                    return new VendasPorMesDTO(mesAno, total);
                })
                .collect(Collectors.toList());
    }
}