package br.com.api.modavintage.dto; //  DTO

public class VendasPorMesDTO {
    private String mesAno; // Formato "YYYY-MM" o
    private Double totalVendido;

    public VendasPorMesDTO(String mesAno, Double totalVendido) {
        this.mesAno = mesAno;
        this.totalVendido = totalVendido;
    }

    // Getters (Lombok @Data r)
    public String getMesAno() {
        return mesAno;
    }

    public Double getTotalVendido() {
        return totalVendido;
    }

    // Setters ()
    public void setMesAno(String mesAno) {
        this.mesAno = mesAno;
    }

    public void setTotalVendido(Double totalVendido) {
        this.totalVendido = totalVendido;
    }
}