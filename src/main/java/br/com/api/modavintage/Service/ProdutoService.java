package br.com.api.modavintage.Service; // Seu pacote

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Repository.ProdutoRepository;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List; // Manter para getRelatorioValorEntradaEstoqueMensal
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    // Método listarProdutos atualizado para aceitar Pageable e retornar Page<Produto>
    public Page<Produto> listarProdutos(String nomePesquisa, Pageable pageable) {
        if (StringUtils.hasText(nomePesquisa)) {
            return produtoRepository.findByNomeContainingIgnoreCase(nomePesquisa, pageable);
        } else {
            return produtoRepository.findAll(pageable); // Usa o findAll que aceita Pageable
        }
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Transactional
    public Produto atualizarProduto(Long id, Produto produtoDetalhes) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        if (StringUtils.hasText(produtoDetalhes.getNome())) {
            produtoExistente.setNome(produtoDetalhes.getNome());
        }
        // ... (outras atualizações de campo como antes) ...
        if (produtoDetalhes.getPreco() != null) {
            produtoExistente.setPreco(produtoDetalhes.getPreco());
        }
        if (produtoDetalhes.getEstoque() != null) {
            produtoExistente.setEstoque(produtoDetalhes.getEstoque());
        }
        if (StringUtils.hasText(produtoDetalhes.getTamanho())) {
            produtoExistente.setTamanho(produtoDetalhes.getTamanho());
        }
        if (StringUtils.hasText(produtoDetalhes.getCategoria())) {
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

    // Método de relatório (mantém como está)
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