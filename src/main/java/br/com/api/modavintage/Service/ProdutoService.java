package br.com.api.modavintage.Service; // Seu pacote

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Repository.ProdutoRepository;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO; // Mantido se você está usando em outro lugar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Para verificar strings vazias/nulas

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList; // Mantido se você está usando em outro lugar
import java.util.stream.Collectors; // Mantido se você está usando em outro lugar


@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto salvarProduto(Produto produto) {
        if (produto.getId() == null) {
            produto.setDataCadastro(new Date());
        }
        return produtoRepository.save(produto);
    }

    // Método listarProdutos atualizado para aceitar um nome para pesquisa
    public List<Produto> listarProdutos(String nomePesquisa) {
        if (StringUtils.hasText(nomePesquisa)) { // Verifica se nomePesquisa não é nulo, vazio ou apenas espaços em branco
            return produtoRepository.findByNomeContainingIgnoreCase(nomePesquisa);
        } else {
            return produtoRepository.findAll();
        }
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Transactional
    public Produto atualizarProduto(Long id, Produto produtoDetalhes) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        if (produtoDetalhes.getNome() != null) {
            produtoExistente.setNome(produtoDetalhes.getNome());
        }
        if (produtoDetalhes.getPreco() != null) {
            produtoExistente.setPreco(produtoDetalhes.getPreco());
        }
        if (produtoDetalhes.getEstoque() != null) {
            produtoExistente.setEstoque(produtoDetalhes.getEstoque());
        }
        if (produtoDetalhes.getTamanho() != null) {
            produtoExistente.setTamanho(produtoDetalhes.getTamanho());
        }
        if (produtoDetalhes.getCategoria() != null) {
            produtoExistente.setCategoria(produtoDetalhes.getCategoria());
        }
        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado com id: " + id);
        }
        produtoRepository.deleteById(id);
    }

    // Método para o relatório de valor de entrada de estoque
    @Transactional(readOnly = true)
    public List<RelatorioMensalValorDTO> getRelatorioValorEntradaEstoqueMensal() {
        List<Object[]> resultados = produtoRepository.findValorEntradaEstoquePorMesRaw();
        if (resultados == null) {
            return new ArrayList<>();
        }
        return resultados.stream()
                .map(record -> {
                    Integer ano = (Integer) record[0];
                    Integer mes = (Integer) record[1];
                    Double valor = (record[2] == null) ? 0.0 : ((Number) record[2]).doubleValue();
                    String mesAno = String.format("%d-%02d", ano, mes);
                    return new RelatorioMensalValorDTO(mesAno, valor);
                })
                .collect(Collectors.toList());
    }
}