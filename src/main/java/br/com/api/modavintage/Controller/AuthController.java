package br.com.api.modavintage.Controller;

import br.com.api.modavintage.Model.Usuario;
import br.com.api.modavintage.Security.JwtUtil;
import br.com.api.modavintage.Service.UsuarioService;
import br.com.api.modavintage.dto.JwtResponse; // Importar DTO 
import br.com.api.modavintage.dto.LoginRequest; // Importar DTO 
import br.com.api.modavintage.dto.ResetarSenhaDTO;
import br.com.api.modavintage.dto.SolicitarResetSenhaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // Para Map.of

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
            // retornar um DTO de resposta para usuário 
            Usuario respostaUsuario = new Usuario(); // OBS: Não expor senha hasheada. Verificar no log
            respostaUsuario.setId(novoUsuario.getId());
            respostaUsuario.setEmail(novoUsuario.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(respostaUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Email ou senha inválidos."));
        }

        final UserDetails userDetails = usuarioService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/solicitar-reset-senha")
    public ResponseEntity<?> solicitarResetSenha(@RequestBody SolicitarResetSenhaDTO solicitarResetDTO) {
        try {
            usuarioService.solicitarResetSenha(solicitarResetDTO.getEmail());
            return ResponseEntity.ok().body(Map.of("mensagem", "Se um email correspondente for encontrado em nossos registros, instruções para redefinir a senha foram enviadas."));
        } catch (Exception e) {
            return ResponseEntity.ok().body(Map.of("mensagem", "Se um email correspondente for encontrado em nossos registros, instruções para redefinir a senha foram enviadas."));
        }
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity<?> resetarSenha(@RequestBody ResetarSenhaDTO resetarSenhaDTO) {
        try {
            boolean sucesso = usuarioService.resetarSenha(resetarSenhaDTO.getToken(), resetarSenhaDTO.getNovaSenha());
            if (sucesso) {
                return ResponseEntity.ok().body(Map.of("mensagem", "Senha redefinida com sucesso."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Token inválido, expirado ou nova senha inválida."));
            }
        } catch (Exception e) {
            System.err.println("Erro ao resetar senha: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Ocorreu um erro ao tentar redefinir a senha."));
        }
    }
}