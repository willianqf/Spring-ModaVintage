package br.com.api.modavintage.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "O nome do produto não pode ser vazio.")
    @Size(min = 2, max = 100, message = "O nome do produto deve ter entre 2 e 100 caracteres.")
    private String nome;

    @NotNull(message = "O preço de custo é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço de custo deve ser maior que zero.")
    @Column(nullable = false)
    private Double precoCusto;

    @NotNull(message = "O preço de venda é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço de venda deve ser maior que zero.")
    private Double preco; // Preço de Venda

    @NotNull(message = "O estoque é obrigatório.")
    @Min(value = 0, message = "O estoque não pode ser negativo.")
    private Integer estoque;

    private String tamanho;
    private String categoria;

    // NOVO CAMPO ADICIONADO
    @Column(nullable = true) // Permite que o campo seja nulo
    private String imagemUri;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo = true;

}