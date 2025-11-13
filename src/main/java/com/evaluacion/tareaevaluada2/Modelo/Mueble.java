package com.evaluacion.tareaevaluada2.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "muebles")
public class Mueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMueble;

    @Column(nullable = false)
    private String nombreMueble;

    private String tipo;

    @Column(nullable = false)
    private Double precioBase;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    private Tamano tamano;

    private String material;

    public enum Estado { ACTIVO, INACTIVO }
    public enum Tamano { GRANDE, MEDIANO, PEQUENO }

}