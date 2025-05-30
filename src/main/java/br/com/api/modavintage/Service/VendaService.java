package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Cliente;
import br.com.api.modavintage.Model.ItemVenda;
import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Model.Venda;
import br.com.api.modavintage.Repository.ClienteRepository;
import br.com.api.modavintage.Repository.ProdutoRepository;
import br.com.api.modavintage.Repository.VendaRepository;
import br.com.api.modavintage.dto.VendasPorMesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Autowired(required = false)
    private ClienteRepository clienteRepository;

    @Transactional
    public Venda salvarVenda(Venda vendaRequest) {
        if (vendaRequest.getItens() == null || vendaRequest.getItens().isEmpty()) {
            throw new IllegalArgumentException("Uma venda deve ter pelo menos um item.");
        }

        Venda novaVenda = new Venda();
        novaVenda.setDataVenda(new Date());

        if (vendaRequest.getCliente() != null && vendaRequest.getCliente().getId() != null) {
            Cliente cliente = clienteRepository.findById(vendaRequest.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente com ID " + vendaRequest.getCliente().getId() + " não encontrado."));
            novaVenda.setCliente(cliente);
        } else if (vendaRequest.getCliente() != null && vendaRequest.getCliente().getId() == null && StringUtils.hasText(vendaRequest.getCliente().getNome())) {
             // Lógica para tentar encontrar ou criar cliente se apenas o nome for enviado (mais complexo)
             // Por agora, vamos assumir que se um cliente é enviado, ele tem ID ou é nulo.
             // Ou, se o ID é nulo mas outros dados do cliente são enviados, você poderia tentar salvar um novo cliente:
             // Cliente novoCliente = clienteRepository.save(vendaRequest.getCliente());
             // novaVenda.setCliente(novoCliente);
             // Para simplificar, manteremos a lógica de exigir ID se o objeto cliente for enviado.
        }


        double totalCalculado = 0.0;
        List<ItemVenda> itensProcessados = new ArrayList<>();

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

            produtoEmEstoque.setEstoque(produtoEmEstoque.getEstoque() - itemRequest.getQuantidadeVendida());
            produtoRepository.save(produtoEmEstoque);

            ItemVenda itemVendaReal = new ItemVenda();
            itemVendaReal.setProduto(produtoEmEstoque);
            itemVendaReal.setQuantidadeVendida(itemRequest.getQuantidadeVendida());
            itemVendaReal.setPrecoUnitario(produtoEmEstoque.getPreco());
            // A associação bidirecional Venda <-> ItemVenda será gerenciada pelo JPA ao salvar Venda
            // devido ao cascade e mappedBy. O importante é adicionar o item à lista da venda.
            // itemVendaReal.setVenda(novaVenda); // Isso será feito pelo JPA ou pode ser feito manualmente se necessário antes do save.
                                               // Para cascade, adicionar à coleção do lado "pai" é o principal.
            itensProcessados.add(itemVendaReal);
            totalCalculado += itemVendaReal.getPrecoUnitario() * itemVendaReal.getQuantidadeVendida();
        }
        
        novaVenda.setItens(itensProcessados); // Define a lista de itens processados
        // É importante que a relação Venda -> ItemVenda seja o lado "dono" (com JoinColumn em ItemVenda.venda)
        // e que ItemVenda.venda seja preenchido para que o mappedBy funcione.
        // Ao usar CascadeType.ALL, o save da Venda persistirá os ItemVenda.
        // Precisamos garantir que cada ItemVenda tenha a referência para novaVenda.
        for(ItemVenda item : itensProcessados) {
            item.setVenda(novaVenda);
        }

        novaVenda.setTotalVenda(totalCalculado);
        return vendaRepository.save(novaVenda);
    }

    @Transactional(readOnly = true)
    public Page<Venda> listarVendas(Pageable pageable) { // Modificado para paginação
        return vendaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Venda> buscarPorId(Long id) {
        // Para evitar problemas com LazyInitializationException ao serializar,
        // pode ser necessário inicializar as coleções aqui se não estiver usando DTOs.
        Optional<Venda> vendaOpt = vendaRepository.findById(id);
        vendaOpt.ifPresent(venda -> {
            if (venda.getCliente() != null) venda.getCliente().getNome(); // Força inicialização
            venda.getItens().size(); // Força inicialização da lista de itens
            for (ItemVenda item : venda.getItens()) {
                if (item.getProduto() != null) item.getProduto().getNome(); // Força inicialização do produto no item
            }
        });
        return vendaOpt;
    }

    @Transactional
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda com ID " + id + " não encontrada."));
        // Opcional: Estornar estoque (ainda não implementado)
        vendaRepository.delete(venda);
    }

    @Transactional(readOnly = true)
    public List<VendasPorMesDTO> getRelatorioVendasMensal() {
        List<Object[]> resultados = vendaRepository.findTotalVendasPorMesRaw();
        if (resultados == null) {
            return new ArrayList<>();
        }
        return resultados.stream()
                .map(record -> {
                    Integer ano = (Integer) record[0];
                    Integer mes = (Integer) record[1];
                    Double total = ((Number) record[2]).doubleValue();
                    String mesAno = String.format("%d-%02d", ano, mes);
                    return new VendasPorMesDTO(mesAno, total);
                })
                .collect(Collectors.toList());
    }
}