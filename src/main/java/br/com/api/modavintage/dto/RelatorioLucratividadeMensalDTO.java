package br.com.api.modavintage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioLucratividadeMensalDTO {

    private String periodo; // Ex: "2024-05"
    private Double totalReceita;
    private Double totalCmv; // Custo da Mercadoria Vendida
    private Double totalLucroBruto;
}