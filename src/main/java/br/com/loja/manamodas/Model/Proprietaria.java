package br.com.loja.manamodas.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "proprietaria")
public class Proprietaria extends Registro {
    @Column(unique = true)
    private String login;
    private String senha;
}
