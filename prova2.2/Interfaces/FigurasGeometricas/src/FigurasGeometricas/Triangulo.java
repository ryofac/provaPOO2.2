package FigurasGeometricas;
public class Triangulo implements IFiguraGeometrica, IComparavel {
    private int lado1;
    private int lado2;
    private int lado3;

    public Triangulo(int lado1, int lado2, int lado3){
        this.lado1 = lado1;
        this.lado2 = lado2;
        this.lado3 = lado3;
    }

    @Override
    public double calcularArea() {
        // Cálculo realizado segundo fórmula de Heron
        double sp = calcularPerimetro() / 2;
        return Math.sqrt(sp * (sp - lado1) * (sp - lado2) * (sp - lado3));
    }

    @Override
    public double calcularPerimetro() {
        return lado1 + lado2 + lado3;
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
