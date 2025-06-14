package br.com.xubank.model;

public class ContaPoupança extends Conta {
    private static final double TAXA_RENDIMENTO = 0.006; //Porcentagem de 0.60%

    public ContaPoupança(Cliente titular) {
        super(titular);
    }

    @Override
    public void aplicarRendimentosETaxasMensais() {
        double rendimento = this.saldo * TAXA_RENDIMENTO;
        if (rendimento > 0) {
            this.saldo += rendimento;
            this.transacoes.add(new Transacao("RENDIMENTO", rendimento, "Rendimento da poupança"));
        }
    }
}