package FigurasGeometricas;

public interface IComparavel {
    /**
     * Método que é utilizado para comparar duas figuras geométricas
     * @param figuraGeometrica a figura geométrica para ser comparada
     * @return 1 se a área é maior, -1 se for menor e 0 se for igual
     */
    int compararCom(IFiguraGeometrica figuraGeometrica);
}
