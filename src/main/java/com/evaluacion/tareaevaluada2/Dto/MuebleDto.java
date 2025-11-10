package com.evaluacion.tareaevaluada2.Dto;
import lombok.Data;

@Data
public class MuebleDto {
    private Long id;
    private String nombre;
    private Double precioBase;
    private Integer stock;
    private String estado;
    private String tamano;
    private String material;
}