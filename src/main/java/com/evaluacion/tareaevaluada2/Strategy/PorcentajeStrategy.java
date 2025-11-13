package com.evaluacion.tareaevaluada2.Strategy;
import com.evaluacion.tareaevaluada2.Modelo.Variante;

public class PorcentajeStrategy implements CalculoPrecioStrategy {
    @Override
    public double calcular(double precioActual, Variante variante) {
        return precioActual * (1 + variante.getValor());
    }
}