package br.com.xubank.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transacao {
    private LocalDate data;
    private String tipo;
    private double valor;
    private String descricao;

    
    public Transacao(LocalDate data, String tipo, double valor) {
        this.data = data;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = "";
    }

    
    public Transacao(String tipo, double valor, String descricao) {
        this.data = LocalDate.now(); 
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
    }

   
    public LocalDate getData() {
        return data;
    }

    
    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        return data.format(formatter) + " | " + String.format("%-10s", tipo) + " | R$ " + String.format("%.2f", valor) + " | " + descricao;
    }
}