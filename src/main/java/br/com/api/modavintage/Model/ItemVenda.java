package br.com.api.modavintage.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// Removido @AllArgsConstructor para usar um construtor customizado que inclua o snapshot
@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    @JsonBackReference
    private Venda venda;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "produto_id", nullable = false) 
    private Produto produto;

    private Integer quantidade;
    
    // Snapshots dos dados do produto no momento da venda
    @Column(nullable = false)
    private Double precoUnitarioSnapshot; // Preço de venda unitário do produto no momento da venda

    @Column(nullable = false)
    private String nomeProdutoSnapshot; // Nome do produto no momento da venda
    
    private String tamanhoSnapshot; // Tamanho do produto no momento da venda 
    
    private String categoriaSnapshot; // Categoria do produto no momento da venda 


    // Construtor para facilitar a criação com snapshots
    public ItemVenda(Produto produto, Integer quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo ao criar ItemVenda.");
        }
        this.produto = produto; // Link ao produto original
        this.quantidade = quantidade;
        
        // Preenchendo os campos de snapshot
        this.precoUnitarioSnapshot = produto.getPreco(); // Pega o preço de venda atual do produto
        this.nomeProdutoSnapshot = produto.getNome();
        this.tamanhoSnapshot = produto.getTamanho();
        this.categoriaSnapshot = produto.getCategoria();
    }
    
    // Método para calcular o subtotal do item, usando o preço do snapshot
    public Double getSubtotal() {
        if (this.precoUnitarioSnapshot == null || this.quantidade == null) {
            return 0.0;
        }
        return this.precoUnitarioSnapshot * this.quantidade;
    }

}