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
    private MuebleRepository muebleRepository; //

    @Autowired
    private MuebleMapper muebleMapper; //

    @Transactional
    public MuebleDto crearMueble(CrearMuebleRequest Dto) {
        Mueble mueble = muebleMapper.toEntity(Dto); //
        Mueble muebleGuardado = muebleRepository.save(mueble);
        return muebleMapper.toDTO(muebleGuardado); //
    }

    @Transactional(readOnly = true)
    public List<MuebleDto> listarMuebles() {
        return muebleRepository.findAll()
                .stream()
                .map(muebleMapper::toDTO) //
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MuebleDto> obtenerMueblePorId(Long id) {
        return muebleRepository.findById(id)
                .map(muebleMapper::toDTO); //
    }

    @Transactional
    public MuebleDto actualizarMueble(Long id, CrearMuebleRequest Dto) {
        Mueble muebleExistente = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));

        muebleExistente.setNombreMueble(Dto.getNombre_mueble()); //
        muebleExistente.setTipo(Dto.getTipo()); //
        muebleExistente.setPrecioBase(Dto.getPrecio_base()); //
        muebleExistente.setStock(Dto.getStock()); //
        muebleExistente.setMaterial(Dto.getMaterial()); //
        muebleExistente.setTamano(Mueble.Tamano.valueOf(Dto.getTamano().toUpperCase())); //

        Mueble muebleActualizado = muebleRepository.save(muebleExistente);
        return muebleMapper.toDTO(muebleActualizado); //
    }

    @Transactional
    public MuebleDto desactivarMueble(Long id) {
        Mueble mueble = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));

        mueble.setEstado(Estado.INACTIVO); //

        Mueble muebleDesactivado = muebleRepository.save(mueble);
        return muebleMapper.toDTO(muebleDesactivado); //
    }

    @Transactional
    public MuebleDto activarMueble(Long id) {
        Mueble mueble = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado con id: " + id));

        mueble.setEstado(Estado.ACTIVO); //

        Mueble muebleActivado = muebleRepository.save(mueble);
        return muebleMapper.toDTO(muebleActivado); //
    }
}