package br.com.xubank.model;

import java.time.LocalDate;
import java.util.ArrayList; 
import java.util.List;
import java.util.stream.Collectors;

public abstract class Conta {
    private static int SEQUENCIAL = 1;

    protected final int numero;
    protected double saldo;
    protected final Cliente titular;
    protected final List<Transacao> transacoes;

    public Conta(Cliente titular) {
        this.numero = SEQUENCIAL++;
        this.titular = titular;
        this.saldo = 0.0;
        this.transacoes = new ArrayList<>();
    }

    public void depositar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
            this.transacoes.add(new Transacao("DEPÓSITO", valor, "Depósito em conta"));
        }
    }

    public boolean sacar(double valor) {
        if (valor > 0 && this.saldo >= valor) {
            this.saldo -= valor;
            this.transacoes.add(new Transacao("SAQUE", -valor, "Saque em conta"));
            return true;
        }
        return false;
    }

    public String getExtratoUltimoMes() {
    
        LocalDate umMesAtras = LocalDate.now().minusMonths(1); 

        String extratoTransacoes = transacoes.stream()
            .filter(t -> t.getData() != null && t.getData().isAfter(umMesAtras))
            .map(Transacao::toString)
            .collect(Collectors.joining("\n"));

        return "--- Extrato da Conta " + this.numero + " ---\n"
             + extratoTransacoes + "\n"
             + String.format("SALDO ATUAL: R$ %.2f\n", this.saldo);
    }

    public abstract void aplicarRendimentosETaxasMensais();

    public int getNumero() { return numero; }
    public double getSaldo() { return saldo; }
    public Cliente getTitular() { return titular; }
}