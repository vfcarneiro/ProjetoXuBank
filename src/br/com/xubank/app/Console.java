package br.com.xubank.app;

import br.com.xubank.business.XuBank;
import br.com.xubank.model.Cliente;
import br.com.xubank.model.Conta;
import br.com.xubank.model.ContaCorrente;
import br.com.xubank.model.ContaInvestimento;
import br.com.xubank.model.ContaPoupança;
import br.com.xubank.model.ContaRendaFixa;
import java.util.Optional;
import java.util.Scanner;

public class Console {

    public static void main(String[] args) {
        
        try (Scanner scanner = new Scanner(System.in)) {
            XuBank banco = new XuBank();

            // dados iniciais para teste (os clientes e contas são pre-cadastrados)
            System.out.println("--- Dados iniciais para teste ---");
            Cliente ana = banco.cadastrarCliente("Ana Paula Souza", "123", "senha1");
            if (ana != null) {
                banco.criarConta(ana, "corrente", 500.0, 200.0); // Saldo inicial 500, limite cheque especial 200
                banco.criarConta(ana, "poupanca", 1000.0); // Saldo inicial 1000
            }

            Cliente joao = banco.cadastrarCliente("João Pedro Santos", "456", "senha2");
            if (joao != null) {
                banco.criarConta(joao, "poupanca", 2000.0);
                banco.criarConta(joao, "investimento", 5000.0);
                banco.criarConta(joao, "rendafixa", 3000.0);
            }

            // loop principal
            while (true) {
                exibirMenuPrincipal();

                int opcao = lerInteiro(scanner, "Escolha uma opção: ");

                switch (opcao) {
                    case 1 -> areaCliente(banco, scanner);
                    case 2 -> areaDiretoria(banco, scanner);
                    case 0 -> {
                        System.out.println("Obrigado por usar o XuBank!");
                        return; // encerra o programa
                    }
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            }
        }
    }

    // menu principal do sistema
    private static void exibirMenuPrincipal() {
        System.out.println("\n--- BEM-VINDO AO XUBANK ---");
        System.out.println("1 - Área do Cliente");
        System.out.println("2 - Área da Diretoria");
        System.out.println("0 - Sair");
    }

    // gerencia operações do cliente
    private static void areaCliente(XuBank banco, Scanner scanner) {
        System.out.print("Digite seu CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        Cliente clienteLogado = banco.autenticarCliente(cpf, senha);

        if (clienteLogado == null) {
            System.out.println("CPF ou senha inválidos.");
            return;
        }

        System.out.println("\nLogin bem-sucedido, " + clienteLogado.getNome() + "!");

        // menu do cliente logado
        while (true) {
            exibirMenuContasCliente(clienteLogado); 

            int numConta = lerInteiro(scanner, "Digite o número da conta para operar (ou 0 para voltar): ");

            if (numConta == 0) {
                break; // sai do menu
            }

            // busca conta por numero
            Optional<Conta> contaOptional = clienteLogado.getContas().stream()
                    .filter(c -> c.getNumero() == numConta)
                    .findFirst();

            if (contaOptional.isPresent()) {
                Conta contaSelecionada = contaOptional.get();
                menuOperacoesConta(contaSelecionada, scanner);
            } else {
                System.out.println("Conta não encontrada para este cliente.");
            }
        }
    }

    // mostra contas do cliente logado
    private static void exibirMenuContasCliente(Cliente clienteLogado) { 
        System.out.println("\n--- Suas Contas ---");
        if (clienteLogado.getContas().isEmpty()) {
            System.out.println("Nenhuma conta encontrada para este cliente.");
            return;
        }
        for (Conta conta : clienteLogado.getContas()) {
            String tipoConta = "Desconhecida";
            if (conta instanceof ContaCorrente) tipoConta = "Corrente";
            else if (conta instanceof ContaPoupança) tipoConta = "Poupança";
            else if (conta instanceof ContaInvestimento) tipoConta = "Investimento";
            else if (conta instanceof ContaRendaFixa) tipoConta = "Renda Fixa";

            System.out.printf("-> Conta %s nº %d | Saldo: R$ %.2f\n", tipoConta, conta.getNumero(), conta.getSaldo());
        }
    }

    // gerencia operações de uma conta
    private static void menuOperacoesConta(Conta contaSelecionada, Scanner scanner) {
        while (true) {
            System.out.println("\n--- Conta " + contaSelecionada.getNumero() + " | Saldo Atual: R$ " + String.format("%.2f", contaSelecionada.getSaldo()) + " ---");
            System.out.println("1 - Depositar");
            System.out.println("2 - Sacar");
            System.out.println("3 - Ver Extrato");
            System.out.println("0 - Voltar para seleção de contas");
            System.out.print("Escolha uma opção: ");

            int opConta = lerInteiro(scanner, null);

            switch (opConta) {
                case 1 -> {
                    double valor = lerDouble(scanner, "Valor do depósito: R$ ");
                    contaSelecionada.depositar(valor);
                    System.out.println("Depósito realizado.");
                }
                case 2 -> {
                    double valor = lerDouble(scanner, "Valor do saque: R$ ");
                    if (contaSelecionada.sacar(valor)) {
                        System.out.println("Saque realizado.");
                    } else {
                        System.out.println("Saldo insuficiente ou valor inválido.");
                    }
                }
                case 3 -> System.out.println(contaSelecionada.getExtratoUltimoMes());
                case 0 -> {
                    return; // volta para seleção de contas
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    //gerencia ações da diretoria
    private static void areaDiretoria(XuBank banco, Scanner scanner) {
        while (true) {
            System.out.println("\n--- RELATÓRIOS DA DIRETORIA ---");
            System.out.println("1 - Valor em Custódia por Tipo de Conta");
            System.out.println("2 - Clientes com Saldo Extremo");
            System.out.println("3 - Aplicar Rendimentos e Taxas Mensais");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opDiretoria = lerInteiro(scanner, null);

            switch (opDiretoria) {
                case 1 -> banco.exibirValorEmCustodiaPorTipo();
                case 2 -> banco.exibirClientesExtremos();
                case 3 -> banco.aplicarRendimentosETaxasMensaisATodasAsContas();
                case 0 -> {
                    return; // volta ao menu principal
                }
                default -> System.out.println("Opção inválida.");
            }
            System.out.println("\nPressione Enter para continuar...");
            scanner.nextLine();
        }
    }

    // metodo para ler número inteiro com validação de entrada
    private static int lerInteiro(Scanner scanner, String mensagem) {
        if (mensagem != null) {
            System.out.print(mensagem);
        }
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida. Por favor, digite um número inteiro.");
            scanner.next();
            if (mensagem != null) {
                System.out.print(mensagem);
            }
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    // metodo para ler número decimal com validação de entrada
    private static double lerDouble(Scanner scanner, String mensagem) {
        if (mensagem != null) {
            System.out.print(mensagem);
        }
        while (!scanner.hasNextDouble()) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scanner.next();
            if (mensagem != null) {
                System.out.print(mensagem);
            }
        }
        double valor = scanner.nextDouble();
        scanner.nextLine();
        return valor;
    }
}
