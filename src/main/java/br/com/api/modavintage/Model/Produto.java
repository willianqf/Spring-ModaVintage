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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(nullable = true) // Permitir nulo inicialmente se houver dados existentes sem ele
    private Double precoCusto; // NOVO CAMPO: Preço de custo

    private Double preco;      // Este campo representa o Preço de Venda

    private Integer estoque;

    private String tamanho;
    private String categoria;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    // Getters e Setters são gerenciados pelo Lombok @Data
}