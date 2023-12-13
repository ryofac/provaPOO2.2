package Entities;

public class Diretor extends Funcionario {

    public Diretor(double salario){
        super(salario);
    }
    
    @Override
    public double getBonificacao() {
        return getSalario() * 60 / 100;
    }
}
