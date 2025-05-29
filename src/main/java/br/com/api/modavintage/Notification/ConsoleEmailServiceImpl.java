package br.com.api.modavintage.Notification; // Seu pacote

import org.springframework.stereotype.Service;

@Service // Marcar como um serviço Spring
public class ConsoleEmailServiceImpl implements EmailService {

    @Override
    public void enviarEmailResetSenha(String paraEmail, String nomeUsuario, String token) {
        // Simula o envio de um email logando no console
        System.out.println("**************************************************");
        System.out.println("SIMULANDO ENVIO DE EMAIL:");
        System.out.println("Para: " + paraEmail);
        System.out.println("De: noreply@modavintage.com");
        System.out.println("Assunto: Seu Código de Recuperação de Senha - Moda Vintage");
        System.out.println("--------------------------------------------------");
        System.out.println("Olá " + (nomeUsuario != null ? nomeUsuario : "Usuário") + ",");
        System.out.println("\nVocê solicitou a redefinição de senha para sua conta na Moda Vintage.");
        System.out.println("Seu código de recuperação é: " + token);
        System.out.println("\nEste código é válido por 1 hora.");
        System.out.println("Se você não solicitou isso, por favor ignore este email (simulado).");
        System.out.println("\nAtenciosamente,");
        System.out.println("Equipe Moda Vintage");
        System.out.println("**************************************************");
    }
}