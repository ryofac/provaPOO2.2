package FigurasGeometricas;


public interface IFiguraGeometrica {
    /**
     * Método responsável por calcular área da figura geométrica
     * @return a área calculada
     */
    public double calcularArea();

    /**
     * Método responsável por calcular perímetro (ou circunferência) da figura geométrica
     * @return o perímetro calculado
     */
    public double calcularPerimetro();
}
