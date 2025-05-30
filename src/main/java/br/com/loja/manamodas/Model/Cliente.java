package br.com.loja.manamodas.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente extends Registro {

    private String sexo;
    @OneToMany(mappedBy = "cliente")
    private List<Venda> comprasRealizadas;
}
