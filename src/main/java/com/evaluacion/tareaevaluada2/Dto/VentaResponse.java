package com.evaluacion.tareaevaluada2.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VentaResponse {
    private String mensaje;
    private Double totalPagado;
}