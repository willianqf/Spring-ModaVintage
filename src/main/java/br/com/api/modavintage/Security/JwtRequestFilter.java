package br.com.api.modavintage.Security; // Seu pacote

import br.com.api.modavintage.Service.UsuarioService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException; // Corrigido para io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI(); // Para logar o URI

        System.out.println("JwtRequestFilter: Processando requisição para " + requestURI); // DEBUG

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JwtRequestFilter: Token JWT extraído: " + jwt); // DEBUG
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("JwtRequestFilter: Username extraído do token: " + username); // DEBUG
            } catch (IllegalArgumentException e) {
                System.err.println("JwtRequestFilter: Não foi possível obter o token JWT (IllegalArgumentException): " + e.getMessage());
            } catch (ExpiredJwtException e) {
                System.err.println("JwtRequestFilter: Token JWT expirou: " + e.getMessage());
                // Para APIs, é melhor deixar o Spring Security lidar com a resposta de token expirado
                // ou tratar de forma explícita mais adiante, em vez de escrever na response aqui.
            } catch (SignatureException e) { // io.jsonwebtoken.SignatureException
                System.err.println("JwtRequestFilter: Assinatura JWT inválida: " + e.getMessage());
            } catch (MalformedJwtException e) {
                System.err.println("JwtRequestFilter: Token JWT malformado: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                System.err.println("JwtRequestFilter: Token JWT não suportado: " + e.getMessage());
            } catch (Exception e) { // Captura genérica para outros erros de JWT
                System.err.println("JwtRequestFilter: Erro inesperado ao processar token JWT: " + e.getMessage());
            }
        } else {
            System.out.println("JwtRequestFilter: Cabeçalho Authorization não encontrado ou não começa com Bearer String para " + requestURI); // DEBUG
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("JwtRequestFilter: Username (" + username + ") obtido, SecurityContext está vazio. Carregando UserDetails..."); // DEBUG
            UserDetails userDetails = null;
            try {
                userDetails = this.usuarioService.loadUserByUsername(username);
            } catch (Exception e) {
                System.err.println("JwtRequestFilter: Erro ao carregar UserDetails para " + username + ": " + e.getMessage()); //DEBUG
            }


            if (userDetails != null) {
                 System.out.println("JwtRequestFilter: UserDetails carregado para: " + userDetails.getUsername() + " com authorities: " + userDetails.getAuthorities()); // DEBUG
                try {
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        System.out.println("JwtRequestFilter: Token VALIDADO com sucesso para " + username); // DEBUG
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        System.out.println("JwtRequestFilter: SecurityContextHolder atualizado para " + username + ". Autenticação: " + SecurityContextHolder.getContext().getAuthentication()); // DEBUG
                    } else {
                        System.err.println("JwtRequestFilter: VALIDAÇÃO DO TOKEN FALHOU (jwtUtil.validateToken retornou false) para " + username); // DEBUG
                    }
                } catch (Exception e) {
                    System.err.println("JwtRequestFilter: Erro durante jwtUtil.validateToken para " + username + ": " + e.getMessage()); // DEBUG
                }
            } else {
                 System.err.println("JwtRequestFilter: UserDetails não pôde ser carregado para o username: " + username); // DEBUG
            }
        } else if (username == null) {
            System.out.println("JwtRequestFilter: Username não foi extraído do token para " + requestURI); // DEBUG
        } else {
            System.out.println("JwtRequestFilter: SecurityContext já tem uma autenticação para " + username + " em " + requestURI + ": " + SecurityContextHolder.getContext().getAuthentication()); // DEBUG
        }

        chain.doFilter(request, response);
        System.out.println("JwtRequestFilter: Finalizou o processamento para " + requestURI + ". Autenticação no contexto: " + SecurityContextHolder.getContext().getAuthentication()); //DEBUG
    }
}