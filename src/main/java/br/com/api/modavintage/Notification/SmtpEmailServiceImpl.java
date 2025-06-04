package br.com.api.modavintage.Notification; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary; // IMPORTAR ANOTAÇÃO
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("smtpEmailService") // O nome do bean : "smtpEmailService"
@Primary // O
public class SmtpEmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailRemetente;

    @Override
    public void enviarEmailResetSenha(String paraEmail, String nomeUsuario, String token) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailRemetente);
            mailMessage.setTo(paraEmail);
            mailMessage.setSubject("Seu Código de Recuperação de Senha - Moda Vintage");

            String textoEmail = "Olá " + (nomeUsuario != null && !nomeUsuario.isEmpty() ? nomeUsuario : "Usuário") + ",\n\n" +
                                "Você solicitou a redefinição de senha para sua conta na Moda Vintage.\n" +
                                "Seu código de recuperação é: " + token + "\n\n" +
                                "Este código é válido por 1 hora.\n" +
                                "Se você não solicitou isso, por favor ignore este email.\n\n" +
                                "Atenciosamente,\n" +
                                "Equipe Moda Vintage";
            mailMessage.setText(textoEmail);

            javaMailSender.send(mailMessage);
            System.out.println("Email de reset de senha enviado para: " + paraEmail + " (via SMTP)");

        } catch (MailException e) {
            System.err.println("Erro ao enviar email de reset de senha para " + paraEmail + " via SMTP: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Não foi possível enviar o email de recuperação. Tente novamente mais tarde.");
        }
    }
}