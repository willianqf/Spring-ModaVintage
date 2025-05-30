package br.com.loja.manamodas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id_registro")
    private int codID;
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;
    @Column(name = "data_nasc", nullable = false)
    private String dataNascimento;
    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

}
