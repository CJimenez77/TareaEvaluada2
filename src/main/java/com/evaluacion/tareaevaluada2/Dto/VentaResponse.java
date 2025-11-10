package com.evaluacion.tareaevaluada2.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Útil para crear la respuesta
public class VentaResponse {
    private String mensaje;
    private Double totalPagado; // Opcional, pero útil
}