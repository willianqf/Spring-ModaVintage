package br.com.api.modavintage.Service; 

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    /**
     * Salva o arquivo no sistema de arquivos.
     * @param file O arquivo a ser salvo.
     * @param produtoId O ID do produto, para possível uso na nomenclatura ou estrutura de pastas.
     * @return O nome do arquivo (ou caminho relativo) que foi salvo e deve ser armazenado no banco.
     * @throws IOException Se ocorrer um erro ao salvar.
     */
    String salvarArquivo(MultipartFile file, Long produtoId) throws IOException;

    /**
     * Deleta um arquivo do sistema de arquivos.
     * @param nomeArquivo O nome do arquivo a ser deletado.
     */
    void deletarArquivo(String nomeArquivo);
}