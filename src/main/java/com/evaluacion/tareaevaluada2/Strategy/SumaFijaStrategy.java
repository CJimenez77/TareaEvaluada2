package com.evaluacion.tareaevaluada2.Strategy;
import com.evaluacion.tareaevaluada2.Modelo.Variante;

// Esta clase podría ser un @Component de Spring si quisiéramos
public class SumaFijaStrategy implements CalculoPrecioStrategy {
    @Override
    public double calcular(double precioActual, Variante variante) {
        return precioActual + variante.getValor();
    }
}