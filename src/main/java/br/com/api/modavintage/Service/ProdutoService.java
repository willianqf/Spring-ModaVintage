package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Produto;
import br.com.api.modavintage.Repository.ProdutoRepository;
import br.com.api.modavintage.dto.RelatorioMensalValorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // FileStorageService foi removido em passos anteriores, mantendo assim.

    public Produto salvarProduto(Produto produto) {
        if (produto.getId() == null) { // Novo produto
            produto.setDataCadastro(new Date());
            produto.setAtivo(true); // Garante que novos produtos são ativos
        }
        // O precoCusto será salvo se estiver presente no objeto produto
        return produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public Page<Produto> listarProdutos(String nomePesquisa, Pageable pageable) {
        if (StringUtils.hasText(nomePesquisa)) {
            // Usa o novo método que busca por nome e apenas produtos ativos
            return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nomePesquisa, pageable);
        } else {
            // Usa o novo método que lista todos os produtos ativos com paginação
            return produtoRepository.findAllByAtivoTrue(pageable);
        }
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodosProdutosAtivos() { 
        // Lista todos os produtos ativos, ordenados por nome 
        return produtoRepository.findAllByAtivoTrue(Sort.by(Sort.Direction.ASC, "nome"));
    }

    @Transactional(readOnly = true)
    public Optional<Produto> buscarPorIdAtivo(Long id) { // Renomeado para clareza
        // Busca um produto ativo pelo ID
        return produtoRepository.findByIdAndAtivoTrue(id);
    }
    
    // Método para buscar por ID independentemente do status 'ativo'
    @Transactional(readOnly = true)
    public Optional<Produto> buscarPorIdQualquerStatus(Long id) {
        return produtoRepository.findById(id);
    }


    @Transactional
    public Produto atualizarProduto(Long id, Produto produtoDetalhes) {
        // Busca um produto ativo para atualização
        Produto produtoExistente = produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Produto ativo não encontrado com id: " + id + " para atualização."));

        if (StringUtils.hasText(produtoDetalhes.getNome())) {
            produtoExistente.setNome(produtoDetalhes.getNome());
        }
        if (produtoDetalhes.getPrecoCusto() != null) {
            produtoExistente.setPrecoCusto(produtoDetalhes.getPrecoCusto());
        }
        if (produtoDetalhes.getPreco() != null) { // 'preco' é o preço de venda
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
        if(StringUtils.hasText(produtoDetalhes.getImagemUri()))
        {
            produtoExistente.setImagemUri(produtoDetalhes.getImagemUri());
        }

        // O campo 'ativo' não é modificado aqui. Deve ser feito por um método específico se necessário.
        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void deletarProduto(Long id) { // Implementa Soft Delete
        Produto produto = produtoRepository.findById(id) // Busca o produto independentemente do status ativo
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id + " para exclusão."));

        if (!produto.isAtivo()) {
            // throw new RuntimeException("Produto com id: " + id + " já está inativo.");
        }
        

        produto.setAtivo(false);
        // produto.setDataExclusao(new Date()); 
        produtoRepository.save(produto);
    }

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