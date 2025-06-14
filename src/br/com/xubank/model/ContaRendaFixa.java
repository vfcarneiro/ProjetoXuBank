package br.com.xubank.model;

import java.util.Random;

public class ContaRendaFixa extends Conta {
    private static final double IMPOSTO_SOBRE_RENDIMENTO = 0.15; 
    private static final double TAXA_MENSAL = 20.0;
    
    private double capitalInvestido = 0.0;

    public ContaRendaFixa(Cliente titular) {
        super(titular);
    }

    @Override
    public void depositar(double valor) {
        super.depositar(valor);
        this.capitalInvestido += valor;
    }

    @Override
    public boolean sacar(double valor) {
        double lucroAtual = this.saldo - this.capitalInvestido;
        double imposto = 0;

        if (lucroAtual > 0) {
            double valorDeLucroNoSaque = Math.min(valor, lucroAtual);
            imposto = valorDeLucroNoSaque * IMPOSTO_SOBRE_RENDIMENTO;
        }

        double valorTotalDebito = valor + imposto;
        if (valor > 0 && this.saldo >= valorTotalDebito) {
            this.saldo -= valorTotalDebito;
            double valorDeCapitalNoSaque = valor - Math.min(valor, lucroAtual);
            this.capitalInvestido -= valorDeCapitalNoSaque;
            
            this.transacoes.add(new Transacao("SAQUE", -valor, "Saque em Renda Fixa"));
            if (imposto > 0) {
                this.transacoes.add(new Transacao("IMPOSTO", -imposto, "IR sobre rendimento"));
            }
            return true;
        }
        return false;
    }

    @Override
    public void aplicarRendimentosETaxasMensais() {
        // 1. Aplica o rendimento
        double taxa = 0.0050 + new Random().nextDouble() * (0.0085 - 0.0050); // Entre 0.50% e 0.85%
        double rendimento = this.saldo * taxa;
        this.saldo += rendimento;
        this.transacoes.add(new Transacao("RENDIMENTO", rendimento, "Rendimento Renda Fixa"));

        // 2. Cobra a tarifa
        this.saldo -= TAXA_MENSAL;
        this.transacoes.add(new Transacao("TARIFA", -TAXA_MENSAL, "Taxa de manutenção"));
    }
}