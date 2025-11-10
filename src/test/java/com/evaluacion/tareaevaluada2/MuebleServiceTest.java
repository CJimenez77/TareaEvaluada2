package com.evaluacion.tareaevaluada2;

import com.evaluacion.tareaevaluada2.Dto.CrearMuebleRequest;
import com.evaluacion.tareaevaluada2.Dto.MuebleDto;
import com.evaluacion.tareaevaluada2.Modelo.Mueble.Estado;
import com.evaluacion.tareaevaluada2.Modelo.Mueble;
import com.evaluacion.tareaevaluada2.Repositorio.MuebleRepository;
import com.evaluacion.tareaevaluada2.Servicio.MuebleMapper;
import com.evaluacion.tareaevaluada2.Servicio.MuebleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MuebleServiceTest {

    @Mock
    private MuebleRepository muebleRepository;

    @Mock
    private MuebleMapper muebleMapper; // Hacemos Mock del Mapper

    @InjectMocks
    private MuebleService muebleService;

    private Mueble muebleEntidad;
    private MuebleDto muebleDTO;
    private CrearMuebleRequest crearRequest;

    @BeforeEach
    void setUp() {
        // --- Datos de Prueba ---
        crearRequest = new CrearMuebleRequest();
        crearRequest.setNombre_mueble("Silla Oficina");
        crearRequest.setPrecio_base(150.0);
        crearRequest.setStock(20);
        crearRequest.setTamano("MEDIANO");

        muebleEntidad = Mueble.builder()
                .idMueble(1L)
                .nombreMueble("Silla Oficina")
                .precioBase(150.0)
                .stock(20)
                .tamano(Mueble.Tamano.MEDIANO)
                .estado(Estado.ACTIVO)
                .build();

        muebleDTO = new MuebleDto();
        muebleDTO.setId(1L);
        muebleDTO.setNombre("Silla Oficina");
        muebleDTO.setPrecioBase(150.0);
        muebleDTO.setStock(20);
        muebleDTO.setEstado("ACTIVO");

        // --- Comportamiento de Mocks ---

        // Cuando se llame al mapper para convertir Request -> Entidad
        when(muebleMapper.toEntity(any(CrearMuebleRequest.class))).thenReturn(muebleEntidad);

        // Cuando se llame al mapper para convertir Entidad -> DTO
        when(muebleMapper.toDTO(any(Mueble.class))).thenReturn(muebleDTO);

        // Comportamiento del Repositorio
        when(muebleRepository.save(any(Mueble.class))).thenReturn(muebleEntidad);
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleEntidad));
        when(muebleRepository.findById(99L)).thenReturn(Optional.empty());
        when(muebleRepository.findAll()).thenReturn(List.of(muebleEntidad));
    }

    @Test
    @DisplayName("Crear Mueble: Exitoso")
    void testCrearMueble_Exitoso() {
        MuebleDto resultado = muebleService.crearMueble(crearRequest);

        assertNotNull(resultado);
        assertEquals(muebleDTO.getId(), resultado.getId());
        assertEquals("Silla Oficina", resultado.getNombre());

        // Verificar que los mocks fueron llamados
        verify(muebleMapper, times(1)).toEntity(crearRequest);
        verify(muebleRepository, times(1)).save(muebleEntidad);
        verify(muebleMapper, times(1)).toDTO(muebleEntidad);
    }

    @Test
    @DisplayName("Listar Muebles: Con resultados")
    void testListarMuebles_ConResultados() {
        List<MuebleDto> resultados = muebleService.listarMuebles();

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals("Silla Oficina", resultados.get(0).getNombre());

        verify(muebleRepository, times(1)).findAll();
        verify(muebleMapper, times(1)).toDTO(muebleEntidad);
    }

    @Test
    @DisplayName("Listar Muebles: Vacio")
    void testListarMuebles_Vacio() {
        // Sobrescribimos el mock para este test
        when(muebleRepository.findAll()).thenReturn(Collections.emptyList());

        List<MuebleDto> resultados = muebleService.listarMuebles();

        assertTrue(resultados.isEmpty());
        verify(muebleRepository, times(1)).findAll();
        verify(muebleMapper, never()).toDTO(any()); // Nunca debe llamarse si la lista está vacía
    }

    @Test
    @DisplayName("Obtener Mueble por ID: Existente")
    void testObtenerMueble_IdExistente() {
        Optional<MuebleDto> resultado = muebleService.obtenerMueblePorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(muebleDTO.getNombre(), resultado.get().getNombre());

        verify(muebleRepository, times(1)).findById(1L);
        verify(muebleMapper, times(1)).toDTO(muebleEntidad);
    }

    @Test
    @DisplayName("Obtener Mueble por ID: No Existente")
    void testObtenerMueble_IdNoExistente() {
        Optional<MuebleDto> resultado = muebleService.obtenerMueblePorId(99L);

        assertTrue(resultado.isEmpty());

        verify(muebleRepository, times(1)).findById(99L);
        verify(muebleMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Actualizar Mueble: Exitoso")
    void testActualizarMueble_Exitoso() {
        // 1. Datos de la solicitud de actualización
        CrearMuebleRequest requestActualizado = new CrearMuebleRequest();
        requestActualizado.setNombre_mueble("Silla Gerencial");
        requestActualizado.setPrecio_base(200.0);
        requestActualizado.setStock(5);
        requestActualizado.setTamano("GRANDE");

        // Capturador para verificar la entidad que se guarda
        ArgumentCaptor<Mueble> muebleCaptor = ArgumentCaptor.forClass(Mueble.class);

        // 2. Mock DTO de respuesta (¡AQUÍ ESTÁ PARTE DEL ARREGLO!)
        //    Asegúrate de que el DTO que "devuelve" el mapper tenga TODOS los datos
        MuebleDto dtoActualizado = new MuebleDto();
        dtoActualizado.setId(1L);
        dtoActualizado.setNombre("Silla Gerencial");
        dtoActualizado.setPrecioBase(200.0); // <-- Añade el precio al mock
        dtoActualizado.setStock(5);

        // Cuando el mapper convierta la Entidad (actualizada) a DTO, devolverá nuestro mock
        when(muebleMapper.toDTO(any(Mueble.class))).thenReturn(dtoActualizado);

        // 3. Ejecución
        MuebleDto resultado = muebleService.actualizarMueble(1L, requestActualizado);

        // 4. Verificaciones (¡AQUÍ ESTÁ LA OTRA PARTE DEL ARREGLO!)
        assertNotNull(resultado);

        // ¡CORREGIDO! Compara nombre con nombre y precio con precio
        assertEquals("Silla Gerencial", resultado.getNombre());
        assertEquals(200.0, resultado.getPrecioBase());
        assertEquals(5, resultado.getStock());

        // Verificar que se buscó el mueble
        verify(muebleRepository, times(1)).findById(1L);
        // Verificar que se llamó a save()
        verify(muebleRepository, times(1)).save(muebleCaptor.capture());

        // Opcional: Verificar que los datos de la *entidad* que se guardó son correctos
        Mueble entidadGuardada = muebleCaptor.getValue();
        assertEquals("Silla Gerencial", entidadGuardada.getNombreMueble());
        assertEquals(200.0, entidadGuardada.getPrecioBase());
    }

    @Test
    @DisplayName("Actualizar Mueble: Falla si ID no existe")
    void testActualizarMueble_IdNoExistente() {
        Exception e = assertThrows(RuntimeException.class, () -> {
            muebleService.actualizarMueble(99L, crearRequest);
        });

        assertEquals("Mueble no encontrado con id: 99", e.getMessage());
        verify(muebleRepository, times(1)).findById(99L);
        verify(muebleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Desactivar Mueble: Exitoso")
    void testDesactivarMueble_Exitoso() {
        ArgumentCaptor<Mueble> muebleCaptor = ArgumentCaptor.forClass(Mueble.class);

        muebleService.desactivarMueble(1L);

        verify(muebleRepository, times(1)).findById(1L);
        verify(muebleRepository, times(1)).save(muebleCaptor.capture());

        // Verificar que el estado de la entidad guardada sea INACTIVO
        assertEquals(Estado.INACTIVO, muebleCaptor.getValue().getEstado());
    }
}