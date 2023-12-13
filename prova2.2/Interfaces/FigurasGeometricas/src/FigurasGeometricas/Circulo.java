package FigurasGeometricas;
public class Circulo implements IFiguraGeometrica, IComparavel {

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
    @Override
    public int compararCom(IFiguraGeometrica figuraGeometrica) {
        if (this.calcularArea() > figuraGeometrica.calcularArea()) {
            return 1;
        } else if (this.calcularArea() < figuraGeometrica.calcularArea()) {
            return -1;
        } else {
            return 0;
        }
    }
    
}
