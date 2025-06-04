package br.com.api.modavintage.dto;

public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    // Getter 
    public String getToken() {
        return token;
    }
    public void setToken(String token) { // Adicionado para consistência se Lombok não for usado
        this.token = token;
    }
}