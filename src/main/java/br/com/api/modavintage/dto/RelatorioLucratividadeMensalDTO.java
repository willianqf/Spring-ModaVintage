package br.com.api.modavintage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioLucratividadeMensalDTO {

    private String periodo; 
    private Double totalReceita;
    private Double totalCmv; 
    private Double totalLucroBruto;
}