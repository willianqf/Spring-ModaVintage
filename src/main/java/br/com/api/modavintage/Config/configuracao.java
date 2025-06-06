package br.com.api.modavintage.Config;

import br.com.api.modavintage.Model.*;
import br.com.api.modavintage.Repository.*;
// Removido import de UsuarioService pois não é usado diretamente aqui para cadastro, PasswordEncoder é usado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random; // Removido import de java.util.List 

@Component
public class configuracao implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        /*
        System.out.println("Carregando dados iniciais...");

        // Criar Usuário (Proprietária)
        if (usuarioRepository.findByEmail("proprietaria.teste@modavintage.com").isEmpty()) {
            Usuario proprietaria = new Usuario();
            proprietaria.setEmail("proprietaria.teste@modavintage.com");
            proprietaria.setSenha(passwordEncoder.encode("senha123"));
            usuarioRepository.save(proprietaria);
            System.out.println("Usuário proprietaria.teste@modavintage.com criado com senha hasheada.");
        }

        // Criar Fornecedores
        if (fornecedorRepository.count() == 0) {
            String[] nomeFornecedor = {"Alpha Tecidos", "Beta Peças", "Gamma Confecções", "Delta Atacado", "Epsilon Malhas"};
            String[] contatoFornecedor = {
                "(11) 5555-1111", "(22) 5555-2222",
                "(33) 5555-3333", "(44) 5555-4444",
                "(55) 5555-5555"
            };
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                Fornecedor f = new Fornecedor();
                f.setNome(nomeFornecedor[random.nextInt(nomeFornecedor.length)] + " " + i);
                f.setContato(contatoFornecedor[random.nextInt(contatoFornecedor.length)]);
                f.setCnpj(String.format("%02d.%03d.%03d/0001-%02d",
                        random.nextInt(100), random.nextInt(1000),
                        random.nextInt(1000), random.nextInt(100)));
                fornecedorRepository.save(f);
            }
            System.out.println("10 Fornecedores de teste criados.");
        }

        // Criar Produtos
        if (produtoRepository.count() == 0) {
            String[] nomeProdutoBase = {"Camiseta", "Calça Jeans", "Jaqueta de Couro", "Blusa de Seda", "Vestido Floral"};
            String[] tipoProdutoDetalhe = {"Básica", "Vintage", "Moderna Retrô", "Estilo Baile", "Descolada"};
            String[] tamanhoProduto = {"P", "M", "G", "GG", "Único"};
            String[] categoriaProduto = {"Casual", "Festa", "Trabalho", "Verão", "Inverno"};
            Random random = new Random();

            for(int x = 0; x < 10; x++){
                Produto p = new Produto();
                p.setNome(nomeProdutoBase[random.nextInt(nomeProdutoBase.length)] + " " + tipoProdutoDetalhe[random.nextInt(tipoProdutoDetalhe.length)] + " " + (x+1));
                double precoCusto = 20.0; // Preço de custo entre 20 e 70
                p.setPrecoCusto(Math.round(precoCusto * 100.0) / 100.0); // Arredonda para 2 casas decimais
                double multiplicadorVenda = 35.0; // Vende por 1.5x a 2.5x o custo
                p.setPreco(Math.round((precoCusto * multiplicadorVenda) * 100.0) / 100.0); // Arredonda para 2 casas decimais
                p.setEstoque(random.nextInt(50) + 5); // Estoque entre 5 e 54
                p.setTamanho(tamanhoProduto[random.nextInt(tamanhoProduto.length)]);
                p.setCategoria(categoriaProduto[random.nextInt(categoriaProduto.length)]);
                p.setDataCadastro(new Date());
                produtoRepository.save(p);
            }
            System.out.println("10 Produtos de teste criados com precoCusto.");
        }

        // Criar Clientes
        if (clienteRepository.count() == 0) {
            String[] nomes1 = {"Maria", "Rogério", "Lara", "Ivone", "Ana", "Martins", "Rodrigo", "Beatriz", "Carlos", "Fernanda"};
            String[] nomes2 = {"de Paula", "Roger", "da Conceição", "Hermelis", "Silva", "Costa", "Oliveira", "Santos"};
            Random random = new Random();
            for(int x = 0; x < 10; x++) {
                Cliente c = new Cliente();
                c.setNome(nomes1[random.nextInt(nomes1.length)] + " " + nomes2[random.nextInt(nomes2.length)] + " " + (x+1));
                c.setEmail("cliente" + x + "@example.com");
                c.setTelefone(String.format("(%02d) 9%04d-%04d", random.nextInt(90)+10, random.nextInt(10000), random.nextInt(10000)));
                clienteRepository.save(c);
            }
            System.out.println("10 Clientes de teste criados.");
        }
        */
        System.out.println("Carga de dados iniciais concluída.");
    }
}