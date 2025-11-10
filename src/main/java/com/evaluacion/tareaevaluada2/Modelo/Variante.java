package com.evaluacion.tareaevaluada2.Modelo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "variantes")
public class Variante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Barniz premium", "Ruedas"

    @Enumerated(EnumType.STRING)
    private TipoVariante tipo; // Ej: PORCENTAJE, SUMA_FIJA

    private Double valor; // Ej: 0.15 (para 15%) o 20.0 (para $20)

    // Define cómo se calcula la variante
    public enum TipoVariante {
        SUMA_FIJA,    // Ej: +$20
        PORCENTAJE    // Ej: +15%
    }
}