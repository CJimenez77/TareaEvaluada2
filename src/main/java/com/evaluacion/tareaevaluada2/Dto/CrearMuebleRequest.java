package com.evaluacion.tareaevaluada2.Dto;
import lombok.Data;

@Data
public class CrearMuebleRequest {
    // Solo los campos que el cliente debe enviar
    private String nombre_mueble;
    private String tipo;
    private Double precio_base;
    private Integer stock;
    private String tamano; // Asumimos que se envía como String
    private String material;
}