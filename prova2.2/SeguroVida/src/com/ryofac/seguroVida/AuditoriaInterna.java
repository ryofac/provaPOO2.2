package com.ryofac.seguroVida;

import java.util.ArrayList;
import java.util.List;

import com.ryofac.seguroVida.Models.ITributavel;

public class AuditoriaInterna {

    List<ITributavel> tributaveis = new ArrayList<>();

    public void adcionar(ITributavel trib){
        // TODO: Analisar possíveis erros
        tributaveis.add(trib);
    }
    
    public double calcularTributos(){
        double tudo = tributaveis.stream()
            .map(elemt -> elemt.calcularTributos())
            .reduce(0.0, (acc, atual) -> acc + atual);
        return tudo;
    }




    
}
