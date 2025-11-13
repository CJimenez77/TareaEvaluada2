package com.evaluacion.tareaevaluada2.Servicio;

import com.evaluacion.tareaevaluada2.Dto.CrearMuebleRequest;
import com.evaluacion.tareaevaluada2.Dto.MuebleDto;
import com.evaluacion.tareaevaluada2.Modelo.Mueble.Estado;
import com.evaluacion.tareaevaluada2.Modelo.Mueble;
import org.springframework.stereotype.Component;

@Component
public class MuebleMapper {

    public Mueble toEntity(CrearMuebleRequest dto) {
        return Mueble.builder()
                .nombreMueble(dto.getNombre_mueble())
                .tipo(dto.getTipo())
                .precioBase(dto.getPrecio_base())
                .stock(dto.getStock())
                .material(dto.getMaterial())
                .tamano(Mueble.Tamano.valueOf(dto.getTamano().toUpperCase()))
                .estado(Estado.ACTIVO)
                .build();
    }

    public MuebleDto toDTO(Mueble mueble) {
        MuebleDto dto = new MuebleDto();
        dto.setId(mueble.getIdMueble());
        dto.setNombre(mueble.getNombreMueble());
        dto.setPrecioBase(mueble.getPrecioBase());
        dto.setStock(mueble.getStock());
        dto.setEstado(mueble.getEstado().toString());
        dto.setTamano(mueble.getTamano().toString());
        dto.setMaterial(mueble.getMaterial());
        return dto;
    }
}