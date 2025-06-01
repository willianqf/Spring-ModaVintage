package br.com.api.modavintage.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor; // Adicionado para construtor padrão
import lombok.AllArgsConstructor; // Adicionado para construtor com todos os args

@Data
@NoArgsConstructor  // Garante construtor padrão exigido pelo JPA
@AllArgsConstructor // Opcional, mas pode ser útil
@Entity
@Table(name = "clientes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE") // Novo campo para soft delete
    private boolean ativo = true; // Default true para novos clientes e clientes existentes

    // Lombok @Data cuida dos getters e setters.
}