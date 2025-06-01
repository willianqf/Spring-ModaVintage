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

    @Column(nullable = true)
    private Double precoCusto;

    private Double preco; // Preço de Venda

    private Integer estoque;

    private String tamanho;
    private String categoria;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE") // Novo campo para soft delete
    private boolean ativo = true; // Default true para novos produtos e produtos existentes

    // Getters e Setters são gerenciados pelo Lombok @Data
    // Se não estiver usando Lombok ou precisar de lógica customizada nos getters/setters,
    // adicione-os manualmente.
    // Ex:
    // public boolean isAtivo() {
    // return ativo;
    // }
    //
    // public void setAtivo(boolean ativo) {
    // this.ativo = ativo;
    // }
}