package br.com.api.modavintage.Notification; // Seu pacote

public interface EmailService {
    void enviarEmailResetSenha(String paraEmail, String nomeUsuario, String token);
}