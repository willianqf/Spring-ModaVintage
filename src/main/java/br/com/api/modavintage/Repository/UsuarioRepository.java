package br.com.api.modavintage.Repository; // Seu pacote

import br.com.api.modavintage.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Adicionar @Repository se ainda não tiver

import java.util.Optional;

@Repository // Adicionar @Repository se ainda não tiver
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    // Novo método para buscar por token de reset de senha
    Optional<Usuario> findByTokenResetSenha(String tokenResetSenha);
}