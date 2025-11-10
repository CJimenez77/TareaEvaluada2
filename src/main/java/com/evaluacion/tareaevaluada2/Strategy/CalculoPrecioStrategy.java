package com.evaluacion.tareaevaluada2.Strategy;
import com.evaluacion.tareaevaluada2.Modelo.Variante;

public interface CalculoPrecioStrategy {
    double calcular(double precioActual, Variante variante);
}