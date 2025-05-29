package br.com.api.modavintage.Model; // Seu pacote

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importar
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes") // Se você renomeou a tabela no DataLoader
// Adicione esta anotação para ignorar propriedades específicas do Hibernate Proxy
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
}