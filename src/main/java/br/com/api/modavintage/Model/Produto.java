package br.com.api.modavintage.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produtos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Adicionar
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double preco;
    private Integer estoque; // Quantidade em estoque

    // Campos que reintroduzimos
    private String tamanho;
    private String categoria;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    // A relação com ItemVenda é opcional (Ainda em análise ;/)
    // @OneToMany(mappedBy = "produto", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    // private List<ItemVenda> itensDeVenda;
}