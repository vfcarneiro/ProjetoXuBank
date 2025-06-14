package br.com.xubank.model;

import java.util.Random;

public class ContaInvestimento extends Conta {
    private static final double IMPOSTO_SOBRE_RENDIMENTO = 0.225; // 22.5%

    //Calculo de lucro
    private double capitalInvestido = 0.0;

    public ContaInvestimento(Cliente titular) {
        super(titular);
    }
    
    @Override
    public void depositar(double valor) {
        if (valor > 0) {
            super.depositar(valor);
            this.capitalInvestido += valor;
        }
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) return false;

        double lucroAtual = this.saldo - this.capitalInvestido;
        double impostoDevido = 0;

        if (lucroAtual > 0) {
            double valorDeLucroNoSaque = Math.min(valor, lucroAtual);
            impostoDevido = valorDeLucroNoSaque * IMPOSTO_SOBRE_RENDIMENTO;
        }

        double valorTotalADebitar = valor + impostoDevido;

        if (this.saldo >= valorTotalADebitar) {
            this.saldo -= valorTotalADebitar;
            
            double valorDeCapitalNoSaque = valor - Math.min(valor, lucroAtual);
            if (valorDeCapitalNoSaque > 0) {
                this.capitalInvestido -= valorDeCapitalNoSaque;
            }
            
            this.transacoes.add(new Transacao("SAQUE", -valor, "Saque em Investimentos"));
            if (impostoDevido > 0) {
                this.transacoes.add(new Transacao("IMPOSTO", -impostoDevido, "IR sobre rendimento"));
            }
            return true;
        }
        
        System.out.println(" Saldo insuficiente para saque mais impostos.");
        return false;
    }

    @Override
    public void aplicarRendimentosETaxasMensais() {
        //entre -0.60% e 1.50%
        double taxaDeRendimento = -0.0060 + new Random().nextDouble() * (0.0150 - (-0.0060));
        double rendimento = this.saldo * taxaDeRendimento;
        this.saldo += rendimento;
        this.transacoes.add(new Transacao("RENDIMENTO", rendimento, "Variação da carteira"));

        // Cobra a tarifa sobre o rendimento, só se ele for positivo
        if (rendimento > 0) {
            double tarifaSobreRendimento = rendimento * 0.01; // 1% do lucro do mês
            this.saldo -= tarifaSobreRendimento;
            this.transacoes.add(new Transacao("TARIFA", -tarifaSobreRendimento, "Taxa de performance"));
        }
    }
}