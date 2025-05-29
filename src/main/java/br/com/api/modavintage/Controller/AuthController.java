package br.com.api.modavintage.Controller; // Seu pacote

import br.com.api.modavintage.Model.Usuario;
import br.com.api.modavintage.Security.JwtUtil; // Verifique se é este o nome do pacote/classe
import br.com.api.modavintage.Service.UsuarioService;
import br.com.api.modavintage.dto.ResetarSenhaDTO; // Importar DTO
import br.com.api.modavintage.dto.SolicitarResetSenhaDTO; // Importar DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


// DTO para a resposta do login contendo o token (pode mover para pacote dto)
class JwtResponse {
    private String token;
    public JwtResponse(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

// DTO para a requisição de login (pode mover para pacote dto)
class LoginRequest {
    private String email;
    private String senha;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
            // Criar um DTO para resposta ou limpar campos sensíveis
            Usuario respostaUsuario = new Usuario();
            respostaUsuario.setId(novoUsuario.getId());
            respostaUsuario.setEmail(novoUsuario.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(respostaUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }

        final UserDetails userDetails = usuarioService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    // Novo endpoint para solicitar reset de senha
      @PostMapping("/solicitar-reset-senha")
    public ResponseEntity<?> solicitarResetSenha(@RequestBody SolicitarResetSenhaDTO solicitarResetDTO) {
        try {
            usuarioService.solicitarResetSenha(solicitarResetDTO.getEmail());
            // Mensagem genérica para o usuário. O token real (simulado) estará no console do backend.
            return ResponseEntity.ok().body(java.util.Map.of("mensagem", "Se um email correspondente for encontrado em nossos registros, instruções para redefinir a senha foram enviadas."));
        } catch (Exception e) { // Captura exceções genéricas do serviço, se houver
            System.err.println("Erro ao processar solicitação de reset de senha: " + e.getMessage());
            // Ainda assim, retorne uma mensagem genérica para não vazar informações.
            return ResponseEntity.ok().body(java.util.Map.of("mensagem", "Se um email correspondente for encontrado em nossos registros, instruções para redefinir a senha foram enviadas."));
            // Ou um erro 500 se for algo inesperado no servidor:
            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar sua solicitação.");
        }
    }
    // Novo endpoint para efetivamente resetar a senha
    @PostMapping("/resetar-senha")
    public ResponseEntity<?> resetarSenha(@RequestBody ResetarSenhaDTO resetarSenhaDTO) {
        try {
            boolean sucesso = usuarioService.resetarSenha(resetarSenhaDTO.getToken(), resetarSenhaDTO.getNovaSenha());
            if (sucesso) {
                return ResponseEntity.ok().body(java.util.Map.of("mensagem", "Senha redefinida com sucesso."));
            } else {
                // O serviço retorna false para token inválido/expirado ou outros erros não excepcionais
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("erro", "Token inválido, expirado ou nova senha inválida."));
            }
        } catch (Exception e) {
            // Captura exceções inesperadas do serviço
            System.err.println("Erro ao resetar senha: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("erro", "Ocorreu um erro ao tentar redefinir a senha."));
        }
    }
}