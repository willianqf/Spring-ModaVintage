package br.com.api.modavintage.Model; 

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor; // 
import lombok.AllArgsConstructor; //

import java.util.Date; // Importar Date

@Data
@NoArgsConstructor  // Lombok para construtor sem argumentos
@AllArgsConstructor // Lombok para construtor com todos os argumentos
@Entity
@Table(name = "usuario") // 
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Email deve ser único
    private String email;

    @Column(nullable = false)
    private String senha;

    // Novos campos para reset de senha
    private String tokenResetSenha;

    @Temporal(TemporalType.TIMESTAMP) // Define como a data/hora será persistida
    private Date dataExpiracaoTokenReset;

}