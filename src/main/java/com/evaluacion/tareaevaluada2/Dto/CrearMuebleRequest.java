package com.evaluacion.tareaevaluada2.Dto;
import lombok.Data;

@Data
public class CrearMuebleRequest {
    private String nombre_mueble;
    private String tipo;
    private Double precio_base;
    private Integer stock;
    private String tamano;
    private String material;
}