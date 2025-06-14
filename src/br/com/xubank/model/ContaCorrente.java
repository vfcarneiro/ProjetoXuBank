package br.com.xubank.model;

public class ContaCorrente extends Conta {
    private final double limiteChequeEspecial;

    public ContaCorrente(Cliente titular, double limiteChequeEspecial) {
        super(titular);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    @Override
    public boolean sacar(double valor) {
        double saldoDisponivel = this.saldo + this.limiteChequeEspecial;
        if (valor > 0 && valor <= saldoDisponivel) {
            this.saldo -= valor;
            this.transacoes.add(new Transacao("SAQUE", -valor, "Saque com uso de limite"));
            return true;
        }
        return false;
    }

    @Override
    public void depositar(double valor) {
        if (this.saldo < 0) {
            double taxaFixa = 10.0;
            double taxaPercentual = Math.abs(this.saldo) * 0.03;
            double taxaTotal = taxaFixa + taxaPercentual;
            
            this.saldo -= taxaTotal;
            this.transacoes.add(new Transacao("TARIFA", -taxaTotal, "Taxa s/ saldo negativo"));
        }
        super.depositar(valor);
    }
    
    @Override
    public void aplicarRendimentosETaxasMensais() {
        // Conta corrente não possui rendimentos nem taxas mensais.
    }
}