package com.ryofac.seguroVida;

import com.ryofac.seguroVida.Models.ContaCorrente;
import com.ryofac.seguroVida.Models.SeguroDeVida;

public class Teste {
    public static void main(String[] args) {
        AuditoriaInterna myAuditoriaInterna = new AuditoriaInterna();
        ContaCorrente ryan = new ContaCorrente("Ryan", 500);
        ContaCorrente ely = new ContaCorrente("Ely", 200);
        SeguroDeVida pamf = new SeguroDeVida();
        SeguroDeVida paxUniao = new SeguroDeVida();

        myAuditoriaInterna.adcionar(ryan);
        myAuditoriaInterna.adcionar(ely);
        myAuditoriaInterna.adcionar(pamf);
        myAuditoriaInterna.adcionar(paxUniao);

        System.out.println(myAuditoriaInterna.calcularTributos());
    }
    
}
