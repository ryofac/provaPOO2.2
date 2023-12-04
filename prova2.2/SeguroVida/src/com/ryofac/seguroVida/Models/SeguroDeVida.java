package com.ryofac.seguroVida.Models;

public class SeguroDeVida implements ITributavel {

    @Override
    public double calcularTributos() {
        return 50.0;
    }
    
}
