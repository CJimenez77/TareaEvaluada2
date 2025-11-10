package com.evaluacion.tareaevaluada2.Servicio;

import com.evaluacion.tareaevaluada2.Dto.CrearMuebleRequest;
import com.evaluacion.tareaevaluada2.Dto.MuebleDto;
import com.evaluacion.tareaevaluada2.Modelo.Mueble.Estado;
import com.evaluacion.tareaevaluada2.Modelo.Mueble;
import com.evaluacion.tareaevaluada2.Repositorio.MuebleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MuebleService {

    @Autowired
    private MuebleRepository muebleRepository;

    @Autowired
    private MuebleMapper muebleMapper; // Inyectamos el mapper

    /**
     * 3. Gestión de catálogo (Crear)
     * Recibe un DTO, lo convierte a Entidad y lo guarda.
     */
    @Transactional
    public MuebleDto crearMueble(CrearMuebleRequest Dto) {
        // Usamos el mapper para convertir el DTO a Entidad
        Mueble mueble = muebleMapper.toEntity(Dto);

        // El mapper ya debería setear el estado por defecto a ACTIVO
        // mueble.setEstado(Estado.ACTIVO);

        Mueble muebleGuardado = muebleRepository.save(mueble);

        // Convertimos la entidad guardada de vuelta a DTO para la respuesta
        return muebleMapper.toDTO(muebleGuardado);
    }

    /**
     * 3. Gestión de catálogo (Listar)
     * Obtiene todos los muebles y los convierte a una lista de DTOs.
     */
    @Transactional(readOnly = true)
    public List<MuebleDto> listarMuebles() {
        return muebleRepository.findAll()
                .stream()
                .map(muebleMapper::toDTO) // (mueble) -> muebleMapper.toDTO(mueble)
                .collect(Collectors.toList());
    }

    /**
     * 3. Gestión de catálogo (Leer por ID)
     */
    @Transactional(readOnly = true)
    public Optional<MuebleDto> obtenerMueblePorId(Long id) {
        return muebleRepository.findById(id)
                .map(muebleMapper::toDTO); // Convierte el Optional<Mueble> a Optional<MuebleDto>
    }

    /**
     * 3. Gestión de catálogo (Actualizar)
     * Actualiza un mueble existente.
     */
    @Transactional
    public MuebleDto actualizarMueble(Long id, CrearMuebleRequest Dto) {
        // 1. Buscar el mueble existente
        Mueble muebleExistente = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));

        // 2. Actualizar los campos desde el DTO
        muebleExistente.setNombreMueble(Dto.getNombre_mueble());
        muebleExistente.setTipo(Dto.getTipo());
        muebleExistente.setPrecioBase(Dto.getPrecio_base());
        muebleExistente.setStock(Dto.getStock());
        muebleExistente.setMaterial(Dto.getMaterial());
        muebleExistente.setTamano(Mueble.Tamano.valueOf(Dto.getTamano().toUpperCase()));
        // (No actualizamos el estado aquí, para eso hacemos un método separado)

        // 3. Guardar y devolver como DTO
        Mueble muebleActualizado = muebleRepository.save(muebleExistente);
        return muebleMapper.toDTO(muebleActualizado);
    }

    /**
     * 3. Gestión de catálogo (Desactivar)
     * Cambia el estado de un mueble a INACTIVO.
     */
    @Transactional
    public MuebleDto desactivarMueble(Long id) {
        Mueble mueble = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));

        mueble.setEstado(Estado.INACTIVO);

        Mueble muebleDesactivado = muebleRepository.save(mueble);
        return muebleMapper.toDTO(muebleDesactivado);
    }
}