package br.com.api.modavintage.Service; // Seu pacote

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils; // Para limpar nomes de arquivo

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service("localStorageFileService")
// @org.springframework.context.annotation.Primary // Se esta for a implementação padrão
public class LocalStorageFileServiceImpl implements FileStorageService {

    private final Path diretorioDeUpload;

    // Injeta o valor do application.properties
    public LocalStorageFileServiceImpl(@Value("${app.upload.dir:${user.dir}/product-images}") String uploadDir) {
        this.diretorioDeUpload = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.diretorioDeUpload);
            System.out.println("Diretório de upload criado/verificado em: " + this.diretorioDeUpload.toString());
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório de upload de arquivos.", ex);
        }
    }

    @Override
    public String salvarArquivo(MultipartFile file, Long produtoId) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Falha ao armazenar arquivo vazio.");
        }

        // Limpa o nome do arquivo para remover caracteres problemáticos
        String nomeOriginal = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "imagem_produto");
        // Adiciona um UUID para garantir unicidade e um sufixo com o ID do produto
        String extensao = "";
        int i = nomeOriginal.lastIndexOf('.');
        if (i > 0) {
            extensao = nomeOriginal.substring(i); // ex: .jpg, .png
        }
        // Nome do arquivo: produto_{id}_uuid.{extensao}
        String nomeArquivoUnico = "produto_" + produtoId + "_" + UUID.randomUUID().toString() + extensao;

        Path caminhoDestino = this.diretorioDeUpload.resolve(nomeArquivoUnico).normalize();

        // Verifica se o caminho de destino está dentro do diretório de upload pai (segurança)
        if (!caminhoDestino.getParent().equals(this.diretorioDeUpload)) {
            throw new IOException("Não é possível salvar arquivo fora do diretório de upload raiz.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, caminhoDestino, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("Arquivo salvo com sucesso: " + nomeArquivoUnico + " em " + caminhoDestino.toString());
        return nomeArquivoUnico; // Retorna apenas o nome do arquivo, pois será servido via /uploads/nomeArquivoUnico
    }

    @Override
    public void deletarArquivo(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.trim().isEmpty()) {
            System.err.println("Tentativa de deletar arquivo com nome nulo ou vazio.");
            return;
        }
        try {
            Path arquivoParaDeletar = this.diretorioDeUpload.resolve(nomeArquivo).normalize();
            // Verifica se o arquivo está dentro do diretório de upload (segurança)
            if (Files.exists(arquivoParaDeletar) && arquivoParaDeletar.getParent().equals(this.diretorioDeUpload)) {
                Files.delete(arquivoParaDeletar);
                System.out.println("Arquivo deletado com sucesso: " + nomeArquivo);
            } else {
                System.err.println("Arquivo não encontrado para deleção ou fora do diretório permitido: " + nomeArquivo);
            }
        } catch (IOException ex) {
            System.err.println("Não foi possível deletar o arquivo " + nomeArquivo + ". " + ex.getMessage());
            // Não lança exceção aqui para não quebrar o fluxo principal se o arquivo já não existir
        }
    }
}