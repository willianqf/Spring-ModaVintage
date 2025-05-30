package br.com.api.modavintage.Config; // 

import br.com.api.modavintage.Model.*;
import br.com.api.modavintage.Repository.*;
import br.com.api.modavintage.Service.UsuarioService; // serviço para hashear senha
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.security.crypto.password.PasswordEncoder; // Para quando for hashear

import java.util.Date;
import java.util.List;
import java.util.Random;

@Component // Torna esta classe um bean gerenciado pelo Spring
public class configuracao implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    // @Autowired
    // private PasswordEncoder passwordEncoder; // Descomentar quando for usar segurança

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private UsuarioRepository UsuarioRepository;

    @Autowired // Injetar o PasswordEncoder
    private PasswordEncoder passwordEncoder;



    @Override
    @Transactional // Adicionar se houver múltiplas operações de save que devem ser atômicas
    public void run(String... args) throws Exception {
        System.out.println("Carregando dados iniciais...");
        // Criar Usuário (Proprietária)
        if (usuarioRepository.findByEmail("proprietaria.teste@modavintage.com").isEmpty()) {
            Usuario proprietaria = new Usuario();
            proprietaria.setEmail("proprietaria.teste@modavintage.com");

            // Hashear a senha
            proprietaria.setSenha(passwordEncoder.encode("senha123"));
            usuarioRepository.save(proprietaria);

            System.out.println("Usuário proprietaria.teste@modavintage.com criado com senha hasheada.");
        }
        //  Criar Fornecedores
        if (fornecedorRepository.count() == 0) { // Só cria se não houver nenhum
            String[] nomeFornecedor = {"Alpha Tecidos", "Beta Peças", "Gamma Confecções", "Delta Atacado", "Epsilon Malhas"};
            String[] contatoFornecedor = {
                "Contato Alpha - (11) 5555-1111",
                "Contato Beta - (22) 5555-2222",
                "Contato Gamma - (33) 5555-3333",
                "Contato Delta - (44) 5555-4444",
                "Contato Epsilon - (55) 5555-5555"
            };

            Random random = new Random();

            for (int i = 0; i < 100; i++) {
                Fornecedor f = new Fornecedor();
                String nome = nomeFornecedor[random.nextInt(nomeFornecedor.length)] + " " + i;
                String contato = contatoFornecedor[random.nextInt(contatoFornecedor.length)];
                String cnpj = String.format("%02d.%03d.%03d/0001-%02d",
                        random.nextInt(100),
                        random.nextInt(1000),
                        random.nextInt(1000),
                        random.nextInt(100));

                f.setNome(nome);
                f.setContato(contato);
                f.setCnpj(cnpj);

                fornecedorRepository.save(f);
            }

            Fornecedor f2 = new Fornecedor();
            f2.setNome("Distribuidora Beta Tecidos");
            f2.setCnpj("22.222.222/0001-22");
            f2.setContato("Contato Beta - (22) 5555-2222");
            fornecedorRepository.save(f2);
            System.out.println("Fornecedores de teste criados.");
        }

        //  Criar Produtos
        if (produtoRepository.count() == 0) { // Só cria se não houver nenhum

            String[] nomeProduto = {"Camiseta", "Calça", "Jeans", "Jaqueta", "Blusa"};
            String[] tipoProduto = {"Básica", "Vintage", "Moderna", "Baile", "Caracol", "Laranja"};
            String[] tamanhoProduto = {"P", "M", "G", "GG"};
            String[] categoria = {"Baixa", "Médio", "Grande"};
            for(int x = 0; x < 100; x++){
                Random random = new Random();
                String nome = nomeProduto[random.nextInt(nomeProduto.length)] + " " + tipoProduto[random.nextInt(tipoProduto.length)] + " " + tamanhoProduto[random.nextInt(tamanhoProduto.length)] + " " + x;
                Produto p1 = new Produto();
                p1.setNome(nome);
                p1.setPreco(59.90);
                p1.setEstoque(10);
                p1.setTamanho("P");
                p1.setCategoria(categoria[random.nextInt(categoria.length)]);
                p1.setDataCadastro(new Date());
                produtoRepository.save(p1);

            }
        }

        // Criar Clientes
        if (clienteRepository.count() == 0) { // Só cria se não houver nenhum

            String nomes1[] = {"Maria", "Rogerio", "Lara", "Ivone", "Ana", "Martins", "Rodrigo"};
            String nomes2[] = {"De Paula", " Roger", " Da Conceição", " Hermelis"};

            for(int x = 0; x < 100; x++)
            {
                Random random = new Random();
                String nome = nomes1[random.nextInt(nomes1.length)] + nomes2[random.nextInt(nomes2.length)] + " " + x;
                Cliente c1 = new Cliente();
                c1.setNome(nome);
                c1.setEmail("ana.silva.teste@example.com");
                c1.setTelefone("(34) 91111-1111");
                clienteRepository.save(c1);
            }
            Cliente c1 = new Cliente();
            c1.setNome("Ana Silva");
            c1.setEmail("ana.silva.teste@example.com");
            c1.setTelefone("(34) 91111-1111");
            clienteRepository.save(c1);
            Cliente c2 = new Cliente();
            c2.setNome("Bruno Costa");
            c2.setEmail("bruno.costa.teste@example.com");
            c2.setTelefone("(34) 92222-2222");
            clienteRepository.save(c2);
            System.out.println("Clientes de teste criados.");
        }

        System.out.println("Carga de dados iniciais concluída.");
    }
}