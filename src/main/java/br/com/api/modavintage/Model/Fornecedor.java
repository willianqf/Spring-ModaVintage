package br.com.api.modavintage.Model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor; // Adicionado
import lombok.AllArgsConstructor; // Adicionado

@Data
@NoArgsConstructor // Adicionado
@AllArgsConstructor // Adicionado
@Entity
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cnpj;
    private String contato; // Campo reintroduzido
}