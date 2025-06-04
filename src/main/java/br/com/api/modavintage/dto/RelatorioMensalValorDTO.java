package br.com.api.modavintage.dto;

public class RelatorioMensalValorDTO {
    private String mesAno; // Formato: "YYYY-MM"
    private Double valor;

    public RelatorioMensalValorDTO(String mesAno, Double valor) {
        this.mesAno = mesAno;
        this.valor = valor;
    }

    // Getters
    public String getMesAno() { return mesAno; }
    public Double getValor() { return valor; }
}