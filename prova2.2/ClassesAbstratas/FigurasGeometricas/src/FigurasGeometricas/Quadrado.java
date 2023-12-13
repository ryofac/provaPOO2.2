package FigurasGeometricas;
public class Quadrado extends FiguraGeometrica {
    public int lado;

    public Quadrado(int lado){
        this.lado = lado;
        
    }

    @Override
    public double calcularArea() {
        return lado * lado;
    }

    @Override
    public double calcularPerimetro() {
        return lado * 4;
    }
    



    
}
