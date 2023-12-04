package ClassesAbstratas.Bonificacao;

public class Presidente extends Funcionario{
    public Presidente(double salario){
        super(salario);
    }

    @Override
    public double getBonificacao() {
        return getSalario() + 1000.0;
    }
}
