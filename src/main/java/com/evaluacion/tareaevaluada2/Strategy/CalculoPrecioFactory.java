package com.evaluacion.tareaevaluada2.Strategy;
import com.evaluacion.tareaevaluada2.Modelo.Variante.TipoVariante;

public class CalculoPrecioFactory {

    public static CalculoPrecioStrategy getStrategy(TipoVariante tipo) {
        switch (tipo) {
            case SUMA_FIJA:
                return new SumaFijaStrategy();
            case PORCENTAJE:
                return new PorcentajeStrategy();
            default:
                throw new IllegalArgumentException("Tipo de variante no soportado: " + tipo);
        }
    }
}