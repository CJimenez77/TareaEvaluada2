package com.evaluacion.tareaevaluada2.Dto;
import java.util.List;
import lombok.Data;

@Data
public class ItemVentaRequest {
    private Long muebleId;
    private List<Long> varianteIds;
    private int cantidad;
}