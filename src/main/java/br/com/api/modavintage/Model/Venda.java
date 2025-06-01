package br.com.api.modavintage.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vendas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O link para o cliente original pode ser mantido para referência,
    // mesmo que o cliente seja desativado (ativo=false).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = true) // Cliente pode ser nulo para vendas anônimas
    private Cliente cliente;

    // Campos de Snapshot para dados do cliente no momento da venda
    // Estes campos são preenchidos se um cliente for associado à venda.
    @Column(nullable = true)
    private String nomeClienteSnapshot;

    @Column(nullable = true)
    private String emailClienteSnapshot;

    @Column(nullable = true)
    private String telefoneClienteSnapshot;


    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false)
    private Double totalVenda;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataVenda;

    // O método adicionarItem foi removido daqui, pois a lógica de criação de ItemVenda
    // com snapshots será gerenciada no VendaService ao construir a Venda.
    // Se você tiver um caso de uso para Venda.adicionarItem() diretamente,
    // precisaria garantir que os snapshots em ItemVenda sejam preenchidos corretamente.
}