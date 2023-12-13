package Entities;

public class Gerente extends Funcionario {
    public Gerente(double salario){
        super(salario);
    }

    @Override
    public double getBonificacao() {
        return 40 * getSalario() / 100;
    }

}
