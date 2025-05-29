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
            Fornecedor f1 = new Fornecedor();
            f1.setNome("Fornecedor Alpha Peças");
            f1.setCnpj("11.111.111/0001-11");
            f1.setContato("Contato Alpha - (11) 5555-1111");
            fornecedorRepository.save(f1);

            Fornecedor f2 = new Fornecedor();
            f2.setNome("Distribuidora Beta Tecidos");
            f2.setCnpj("22.222.222/0001-22");
            f2.setContato("Contato Beta - (22) 5555-2222");
            fornecedorRepository.save(f2);
            System.out.println("Fornecedores de teste criados.");
        }

        //  Criar Produtos
        if (produtoRepository.count() == 0) { // Só cria se não houver nenhum
            Produto p1 = new Produto();
            p1.setNome("Camiseta Clássica Branca");
            p1.setPreco(59.90);
            p1.setEstoque(10);
            p1.setTamanho("P");
            p1.setCategoria("Camisetas");
            p1.setDataCadastro(new Date());
            produtoRepository.save(p1);

            Produto p2 = new Produto();
            p2.setNome("Calça Jeans Vintage Azul");
            p2.setPreco(129.90);
            p2.setEstoque(5);
            p2.setTamanho("M");
            p2.setCategoria("Calças");
            p2.setDataCadastro(new Date());
            produtoRepository.save(p2);

            Produto p3 = new Produto();
            p3.setNome("Jaqueta de Couro Retrô");
            p3.setPreco(299.90);
            p3.setEstoque(2);
            p3.setTamanho("G");
            p3.setCategoria("Jaquetas");
            p3.setDataCadastro(new Date());
            produtoRepository.save(p3);
            System.out.println("Produtos de teste criados.");
        }

        // Criar Clientes
        if (clienteRepository.count() == 0) { // Só cria se não houver nenhum
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