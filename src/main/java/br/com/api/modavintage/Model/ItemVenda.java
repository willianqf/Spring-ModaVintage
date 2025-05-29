package br.com.api.modavintage.Model;

import com.fasterxml.jackson.annotation.JsonBackReference; // Importar
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@Entity
@Table(name = "itens_venda")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Adicionar
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)

    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    @JsonBackReference 
    private Venda venda;

    @Column(nullable = false)
    private Integer quantidadeVendida;

    @Column(nullable = false)
    private Double precoUnitario;

    public ItemVenda(Produto produto, Integer quantidadeVendida, Double precoUnitario) {
        this.produto = produto;
        this.quantidadeVendida = quantidadeVendida;
        this.precoUnitario = precoUnitario;
    }
}