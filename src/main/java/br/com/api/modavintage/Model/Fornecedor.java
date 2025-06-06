package br.com.api.modavintage.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // Importar
import jakarta.validation.constraints.Size; // 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do fornecedor não pode ser vazio.")
    @Size(min = 2, max = 100, message = "O nome do fornecedor deve ter entre 2 e 100 caracteres.")
    private String nome;

    @Size(max = 18, message = "O CNPJ não pode exceder 18 caracteres.")
    private String cnpj;
    
    @Size(max = 255)
    private String contato;
}