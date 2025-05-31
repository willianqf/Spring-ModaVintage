package br.com.api.modavintage.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Se você tem o construtor explícito abaixo, @AllArgsConstructor pode ser redundante ou conflitar dependendo dos campos.
                  // Considere remover o construtor explícito se @AllArgsConstructor já cobre suas necessidades
                  // ou remova @AllArgsConstructor se preferir manter o construtor explícito.
                  // Para este exemplo, manterei ambos, mas é um ponto de atenção.
@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id") // Assumindo que nullable = false foi removido ou é padrão e está OK
    @JsonBackReference
    private Venda venda;

    @ManyToOne(fetch = FetchType.EAGER) // Mantido EAGER como no seu original
    @JoinColumn(name = "produto_id")    // Assumindo que nullable = false foi removido ou é padrão e está OK
    private Produto produto;

    private Integer quantidade;
    private Double precoUnitario; // Preço de venda unitário no momento da venda

    // Construtor existente no seu arquivo - verifique a necessidade dele vs @AllArgsConstructor
    public ItemVenda(Produto produto, Integer quantidade, Double precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // MÉTODO ADICIONADO PARA CALCULAR O SUBTOTAL DO ITEM
    public Double getSubtotal() {
        if (this.precoUnitario == null || this.quantidade == null) {
            // Retornar 0.0 ou lançar uma exceção, dependendo da sua lógica de negócio
            // para itens com dados incompletos.
            return 0.0;
        }
        return this.precoUnitario * this.quantidade;
    }

    // Se o erro persistir para getQuantidade() mesmo com @Data,
    // você pode adicionar o getter explicitamente como um teste:
    // public Integer getQuantidade() {
    //     return this.quantidade;
    // }
    // E similar para outros campos se necessário.
}