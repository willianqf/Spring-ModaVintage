package br.com.api.modavintage.Service; // Seu pacote

import br.com.api.modavintage.Model.Usuario;
import br.com.api.modavintage.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.api.modavintage.Notification.EmailService; 

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import java.security.SecureRandom; // Para gerar números aleatórios seguros
import java.text.DecimalFormat; // Para formatar o número com zeros à esquerda

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

     @Autowired
    private EmailService emailService; // Injetar o EmailService

    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final int TAMANHO_TOKEN_RESET_NUMERICO = 6; // 6 dígitos

    private static final long EXPIRACAO_TOKEN_RESET_MS = 3600000; // 1 hora

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        return new User(usuario.getEmail(), usuario.getSenha(), new ArrayList<>());
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado: " + usuario.getEmail());
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setTokenResetSenha(null);
        usuario.setDataExpiracaoTokenReset(null);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticar(String email, String senhaLogin) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isPresent()) {
            Usuario usuarioNoBanco = usuarioOptional.get();
            if (passwordEncoder.matches(senhaLogin, usuarioNoBanco.getSenha())) {
                return usuarioOptional;
            }
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

 @Transactional
    public void solicitarResetSenha(String email) { // Alterado para void, não retorna mais o token
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            System.err.println("Tentativa de reset para email não cadastrado (ou para não vazar informação): " + email);
            // NÃO lance uma exceção que revele se o email existe ou não.
            // Apenas retorne ou envie o email (simulado) se existir.
            // Para o usuário, a mensagem será sempre "Se o email existir..."
            // A simulação de email abaixo só ocorrerá se o usuário for encontrado.
            return; // Sai silenciosamente se o email não for encontrado
        }

        Usuario usuario = usuarioOptional.get();
        SecureRandom random = new SecureRandom();
        int numero = random.nextInt((int) Math.pow(10, TAMANHO_TOKEN_RESET_NUMERICO));
        String token = new DecimalFormat("0".repeat(TAMANHO_TOKEN_RESET_NUMERICO)).format(numero);

        usuario.setTokenResetSenha(token);
        usuario.setDataExpiracaoTokenReset(new Date(System.currentTimeMillis() + EXPIRACAO_TOKEN_RESET_MS));
        usuarioRepository.save(usuario);

        // Enviar o "email" (logar no console)
        // Idealmente, o nome do usuário viria da entidade Usuario se você tiver um campo 'nome' lá.
        // Se não, podemos usar o email ou uma saudação genérica.
        // Supondo que Usuario tem getNome() ou passamos o email como nome.
        String nomeParaEmail = usuario.getEmail(); // Ou usuario.getNome() se existir
        emailService.enviarEmailResetSenha(usuario.getEmail(), nomeParaEmail, token);

        System.out.println("Simulação de envio de token de reset para " + email + " concluída. Verifique o console do backend.");
        // NÃO RETORNE MAIS O TOKEN AQUI DIRETAMENTE PARA A API
    }

    @Transactional
    public boolean resetarSenha(String token, String novaSenha) {
        if (token == null || token.trim().isEmpty() || novaSenha == null || novaSenha.trim().isEmpty()) {
            System.err.println("Tentativa de reset de senha com token ou nova senha vazios.");
            return false;
        }
        if (novaSenha.length() < 6) { // Validação de tamanho da nova senha
            System.err.println("Nova senha muito curta.");
            return false; // Ou lançar exceção
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByTokenResetSenha(token);

        if (usuarioOptional.isEmpty()) {
            System.err.println("Nenhum usuário encontrado com o token de reset fornecido: " + token);
            return false;
        }

        Usuario usuario = usuarioOptional.get();

        if (usuario.getDataExpiracaoTokenReset() == null || usuario.getDataExpiracaoTokenReset().before(new Date())) {
            System.err.println("Token de reset expirado para o usuário: " + usuario.getEmail());
            usuario.setTokenResetSenha(null);
            usuario.setDataExpiracaoTokenReset(null);
            usuarioRepository.save(usuario);
            return false;
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTokenResetSenha(null);
        usuario.setDataExpiracaoTokenReset(null);
        usuarioRepository.save(usuario);
        System.out.println("Senha resetada com sucesso para o usuário: " + usuario.getEmail());
        return true;
    }
}