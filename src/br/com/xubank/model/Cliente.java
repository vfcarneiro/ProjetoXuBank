package br.com.xubank.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private final String nome;
    private final String cpf;
    private final String senha;
    private final List<Conta> contas;

    public Cliente(String nome, String cpf, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.contas = new ArrayList<>();
    }

    public void adicionarConta(Conta conta) {
        this.contas.add(conta);
    }

    public boolean validarSenha(String tentativa) {
        return this.senha.equals(tentativa);
    }
    
    public double getSaldoTotal() {
        return this.contas.stream().mapToDouble(Conta::getSaldo).sum();
    }

    // Getters
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public List<Conta> getContas() { return contas; }
}