package br.com.api.modavintage.Model; // Seu pacote

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor; // Adicionado se ainda não estiver
import lombok.AllArgsConstructor; // Adicionado se ainda não estiver

import java.util.Date; // Importar Date

@Data
@NoArgsConstructor  // Lombok para construtor sem argumentos
@AllArgsConstructor // Lombok para construtor com todos os argumentos
@Entity
@Table(name = "usuario") // ou "usuarios" se for o nome da sua tabela
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

    // Construtores, Getters e Setters são gerenciados pelo Lombok @Data,
    // @NoArgsConstructor e @AllArgsConstructor.
    // Se não estiver usando @AllArgsConstructor, crie construtores conforme necessário.
}