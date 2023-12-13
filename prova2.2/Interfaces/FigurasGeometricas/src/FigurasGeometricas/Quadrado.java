package FigurasGeometricas;
public class Quadrado implements IFiguraGeometrica, IComparavel {
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
