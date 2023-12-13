package com.ryofac.seguroVida.Models;

public class ContaCorrente extends Conta implements ITributavel{
    public ContaCorrente(String nome, double saldo){
       super(nome, saldo);
    }
    @Override
    public double calcularTributos() {
        return getSaldo() * 10/100;
    }
    
}
