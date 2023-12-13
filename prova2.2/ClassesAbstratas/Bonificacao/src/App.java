import Entities.*;

public class App {
    public static void main(String[] args) throws Exception {
        Diretor diretor = new Diretor(10000.0);
        Gerente gerente = new Gerente(10000.0);
        Presidente presidente = new Presidente(20000.0);

        System.out.println("DIRETOR: R$ " + diretor.getBonificacao());
        System.out.println("GERENTE: R$" + gerente.getBonificacao());
        System.out.println("PRESIDENTE: R$" + presidente.getBonificacao());

    }
}
