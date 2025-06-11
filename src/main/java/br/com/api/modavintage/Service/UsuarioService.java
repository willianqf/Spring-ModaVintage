package br.com.api.modavintage.Service;

import br.com.api.modavintage.Model.Usuario;
import br.com.api.modavintage.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.api.modavintage.Notification.EmailService; 

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.security.SecureRandom;
import java.text.DecimalFormat;

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

        // ===== CORREÇÃO APLICADA =====
        // Adicionando a autoridade "ROLE_USER" para que o Spring Security autorize o acesso.
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        
        return new User(usuario.getEmail(), usuario.getSenha(), authorities);
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
    public void solicitarResetSenha(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            System.err.println("Tentativa de reset para email não cadastrado (ou para não vazar informação): " + email);
            return; // Sai silenciosamente se o email não for encontrado
        }

        Usuario usuario = usuarioOptional.get();
        SecureRandom random = new SecureRandom();
        int numero = random.nextInt((int) Math.pow(10, TAMANHO_TOKEN_RESET_NUMERICO));
        String token = new DecimalFormat("0".repeat(TAMANHO_TOKEN_RESET_NUMERICO)).format(numero);

        usuario.setTokenResetSenha(token);
        usuario.setDataExpiracaoTokenReset(new Date(System.currentTimeMillis() + EXPIRACAO_TOKEN_RESET_MS));
        usuarioRepository.save(usuario);

        String nomeParaEmail = usuario.getEmail();
        emailService.enviarEmailResetSenha(usuario.getEmail(), nomeParaEmail, token);

        System.out.println("Simulação de envio de token de reset para " + email + " concluída. Verifique o console do backend.");
    }

    @Transactional
    public boolean resetarSenha(String token, String novaSenha) {
        if (token == null || token.trim().isEmpty() || novaSenha == null || novaSenha.trim().isEmpty()) {
            System.err.println("Tentativa de reset de senha com token ou nova senha vazios.");
            return false;
        }
        if (novaSenha.length() < 6) {
            System.err.println("Nova senha muito curta.");
            return false;
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
