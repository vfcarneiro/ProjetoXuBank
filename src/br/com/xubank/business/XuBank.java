package br.com.xubank.business;

import br.com.xubank.model.*;
import java.util.ArrayList;
import java.util.List;

public class XuBank {
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Conta> contas = new ArrayList<>();

    public Cliente cadastrarCliente(String nome, String cpf, String senha) {
        if (clientes.stream().anyMatch(c -> c.getCpf().equals(cpf))) {
            System.out.println("Erro: Cliente com CPF " + cpf + " já existe.");
            return null; // Cliente já existe
        }
        Cliente novoCliente = new Cliente(nome, cpf, senha);
        clientes.add(novoCliente);
        System.out.println("Cliente " + nome + " cadastrado com sucesso!");
        return novoCliente;
    }

    public Cliente autenticarCliente(String cpf, String senha) {
        return clientes.stream()
            .filter(c -> c.getCpf().equals(cpf) && c.validarSenha(senha))
            .findFirst()
            .orElse(null);
    }

    public Conta criarConta(Cliente cliente, String tipoConta, double ...args) {
        Conta novaConta = null;
        double saldoInicial = 0.0;
        double limiteChequeEspecial = 100.0; // Padrão para ContaCorrente

       
        if (args.length > 0) {
            saldoInicial = args[0];
            if (tipoConta.equalsIgnoreCase("corrente") && args.length > 1) {
                limiteChequeEspecial = args[1];
            }
        }

        switch (tipoConta.toLowerCase()) {
            case "corrente":
                novaConta = new ContaCorrente(cliente, limiteChequeEspecial);
                break;
            case "poupanca":
                novaConta = new ContaPoupança(cliente); 
                break;
            case "rendafixa":
                novaConta = new ContaRendaFixa(cliente);
                break;
            case "investimento":
                novaConta = new ContaInvestimento(cliente);
                break;
            default:
                System.out.println("Tipo de conta inválido: " + tipoConta);
                return null;
        }

        if (novaConta != null) {
            if (saldoInicial > 0) {
                 novaConta.depositar(saldoInicial);
            }
            cliente.adicionarConta(novaConta);
            contas.add(novaConta);
            System.out.println("Conta " + tipoConta + " criada com sucesso para " + cliente.getNome() + ". Número: " + novaConta.getNumero());
        }
        return novaConta;
    }

    //esse método é chamado pela área da diretoria
    public void aplicarRendimentosETaxasMensaisATodasAsContas() {
        System.out.println("\n--- Aplicando Rendimentos e Taxas Mensais ---");
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta para aplicar rendimentos/taxas.");
            return;
        }
        for (Conta conta : contas) {
            conta.aplicarRendimentosETaxasMensais();
            System.out.printf("Conta %d atualizada. Novo saldo: R$ %.2f\n", conta.getNumero(), conta.getSaldo());
        }
        System.out.println("--- Aplicação de rendimentos e taxas concluída. ---");
    }

    // Métodos para relatórios da diretoria
    public void exibirValorEmCustodiaPorTipo() {
        double totalCorrente = contas.stream().filter(c -> c instanceof ContaCorrente).mapToDouble(Conta::getSaldo).sum();
        double totalPoupanca = contas.stream().filter(c -> c instanceof ContaPoupança).mapToDouble(Conta::getSaldo).sum();
        double totalInvestimento = contas.stream().filter(c -> c instanceof ContaInvestimento).mapToDouble(Conta::getSaldo).sum();
        double totalRendaFixa = contas.stream().filter(c -> c instanceof ContaRendaFixa).mapToDouble(Conta::getSaldo).sum();

        System.out.println("\n--- Valor Total em Custódia por Tipo de Conta ---");
        System.out.printf("Total em Contas Correntes: R$ %.2f\n", totalCorrente);
        System.out.printf("Total em Contas Poupança: R$ %.2f\n", totalPoupanca);
        System.out.printf("Total em Contas Investimento: R$ %.2f\n", totalInvestimento);
        System.out.printf("Total em Contas Renda Fixa: R$ %.2f\n", totalRendaFixa);
    }
    
    public void exibirClientesExtremos() {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }

        Cliente maisRico = null;
        Cliente maisPobre = null;
        double maiorSaldo = -1;
        double menorSaldo = Double.MAX_VALUE;

        for (Cliente cliente : clientes) {
            double saldoAtualCliente = cliente.getSaldoTotal();

            if (maisRico == null || saldoAtualCliente > maiorSaldo) {
                maiorSaldo = saldoAtualCliente;
                maisRico = cliente;
            }

            if (maisPobre == null || saldoAtualCliente < menorSaldo) {
                menorSaldo = saldoAtualCliente;
                maisPobre = cliente;
            }
        }

        if (maisRico != null) {
            System.out.printf("Cliente mais rico: %s (R$ %.2f)\n", maisRico.getNome(), maisRico.getSaldoTotal());
        }
        if (maisPobre != null) {
            System.out.printf("Cliente mais pobre: %s (R$ %.2f)\n", maisPobre.getNome(), maisPobre.getSaldoTotal());
        }
    }
}
