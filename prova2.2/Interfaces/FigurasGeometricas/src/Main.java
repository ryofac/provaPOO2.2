import FigurasGeometricas.Circulo;
import FigurasGeometricas.Quadrado;
import FigurasGeometricas.Triangulo;

public class Main {
    public static void main(String[] args) {
        Triangulo triangulo = new Triangulo(3, 4, 5);
        Quadrado quadrado = new Quadrado(5);
        Circulo circulo = new Circulo(5);
        System.out.println("TRIANGULO: Area: " + triangulo.calcularArea() + " Perimetro: " + triangulo.calcularPerimetro());
        System.out.println("QUADRADO: Area: " + quadrado.calcularArea() + " Perimetro: " + quadrado.calcularPerimetro());
        System.out.println("CIRCULO: Area: " + circulo.calcularArea() + " Perimetro: " + circulo.calcularPerimetro());

        System.out.println("Area triangulo eh maior ou igual que quadrado? " +
         (triangulo.compararCom(quadrado) == -1? "Não" : "Sim"));

         System.out.println("Area circulo eh maior ou igual que triangulo? " +
         (circulo.compararCom(triangulo) == -1? "Não" : "Sim"));

         System.out.println("Area triangulo eh igual a triangulo? " +
         (triangulo.compararCom(triangulo) == 0? "Sim" : "Não"));
    }
    
}
