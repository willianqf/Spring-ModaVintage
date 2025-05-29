package br.com.api.modavintage.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Importar
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vendas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Adicionar
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Adicionar aqui
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false)
    private Double totalVenda;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataVenda;

    public void adicionarItem(Produto produto, Integer quantidade) {
        if (produto == null || produto.getPreco() == null) {
            throw new IllegalArgumentException("Produto ou preço do produto não pode ser nulo.");
        }
        ItemVenda item = new ItemVenda(produto, quantidade, produto.getPreco());
        item.setVenda(this);
        this.itens.add(item);
    }
}