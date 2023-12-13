package FigurasGeometricas;
public class Circulo extends FiguraGeometrica {

    private int raio;

    public Circulo(int raio) {
        this.raio = raio;
    }
    @Override
    public double calcularArea() {
        return Math.pow(raio, 2) * Math.PI; 
    }

    @Override
    public double calcularPerimetro() {
        return 2 * Math.PI * raio;
    }
    
}
