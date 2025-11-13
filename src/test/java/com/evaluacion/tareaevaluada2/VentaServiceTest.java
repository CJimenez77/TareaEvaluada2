package com.evaluacion.tareaevaluada2;

import com.evaluacion.tareaevaluada2.Dto.ItemVentaRequest;
import com.evaluacion.tareaevaluada2.Dto.VentaResponse;
import com.evaluacion.tareaevaluada2.Modelo.Mueble;
import com.evaluacion.tareaevaluada2.Modelo.Variante.TipoVariante;
import com.evaluacion.tareaevaluada2.Modelo.Variante;
import com.evaluacion.tareaevaluada2.Repositorio.MuebleRepository;
import com.evaluacion.tareaevaluada2.Repositorio.VarianteRepository;
import com.evaluacion.tareaevaluada2.Servicio.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class VentaServiceTest {

    @Mock
    private MuebleRepository muebleRepository;

    @Mock
    private VarianteRepository varianteRepository;

    @InjectMocks
    private VentaService ventaService;

    private Mueble muebleStock10;
    private Mueble muebleStock1;
    private Variante varianteFija;
    private Variante variantePorcentaje;

    @BeforeEach
    void setUp() {

        muebleStock10 = Mueble.builder()
                .idMueble(1L)
                .nombreMueble("Silla Gamer")
                .precioBase(100.0)
                .stock(10)
                .build();

        muebleStock1 = Mueble.builder()
                .idMueble(2L)
                .nombreMueble("Escritorio L")
                .precioBase(500.0)
                .stock(1)
                .build();

        varianteFija = new Variante();
        varianteFija.setId(10L);
        varianteFija.setTipo(TipoVariante.SUMA_FIJA);
        varianteFija.setValor(20.0);

        variantePorcentaje = new Variante();
        variantePorcentaje.setId(11L);
        variantePorcentaje.setTipo(TipoVariante.PORCENTAJE);
        variantePorcentaje.setValor(0.10);

        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleStock10));
        when(muebleRepository.findById(2L)).thenReturn(Optional.of(muebleStock1));
        when(muebleRepository.findById(99L)).thenReturn(Optional.empty()); // Mueble inexistente

        when(varianteRepository.findAllById(List.of(10L))).thenReturn(List.of(varianteFija));
        when(varianteRepository.findAllById(List.of(11L))).thenReturn(List.of(variantePorcentaje));
        when(varianteRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(varianteFija, variantePorcentaje));
    }

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 2, Sin variantes")
    void testCotizacion_ItemUnico_SinVariantes() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L);
        item.setCantidad(2);
        item.setVarianteIds(Collections.emptyList());

        double total = ventaService.calcularCotizacion(List.of(item));

        assertEquals(200.0, total);
    }

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 1, Con variante fija")
    void testCotizacion_ItemUnico_ConVarianteFija() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L);
        item.setCantidad(1);
        item.setVarianteIds(List.of(10L));

        double total = ventaService.calcularCotizacion(List.of(item));

        assertEquals(120.0, total);
    }

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 1, Con variante porcentaje")
    void testCotizacion_ItemUnico_ConVariantePorcentaje() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L);
        item.setCantidad(1);
        item.setVarianteIds(List.of(11L));

        double total = ventaService.calcularCotizacion(List.of(item));

        assertEquals(110.0, total, 0.001);
    }

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 2, Con múltiples variantes (fija + porcentaje)")
    void testCotizacion_ItemUnico_ConMultiplesVariantes() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L);
        item.setCantidad(2);
        item.setVarianteIds(List.of(10L, 11L));

        double total = ventaService.calcularCotizacion(List.of(item));
        assertEquals(264.0, total);
    }

    @Test
    @DisplayName("Cotización: Múltiples items (mixto)")
    void testCotizacion_MultiplesItemsMixtos() {
        ItemVentaRequest item1 = new ItemVentaRequest();
        item1.setMuebleId(1L);
        item1.setCantidad(2);
        item1.setVarianteIds(Collections.emptyList());

        ItemVentaRequest item2 = new ItemVentaRequest();
        item2.setMuebleId(2L);
        item2.setCantidad(1);
        item2.setVarianteIds(List.of(10L));

        double total = ventaService.calcularCotizacion(List.of(item1, item2));

        assertEquals(720.0, total);
    }

    @Test
    @DisplayName("Cotización: Falla si el Mueble ID no existe")
    void testCotizacion_MuebleNoExiste() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(99L);
        item.setCantidad(1);

        Exception e = assertThrows(RuntimeException.class, () -> {
            ventaService.calcularCotizacion(List.of(item));
        });

        assertEquals("Mueble no encontrado", e.getMessage());
    }

    @Test
    @DisplayName("Venta: Exitosa, con stock suficiente")
    void testVenta_Exitosa_StockSuficiente() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L);
        item.setCantidad(3);

        ArgumentCaptor<Mueble> muebleCaptor = ArgumentCaptor.forClass(Mueble.class);

        VentaResponse response = ventaService.confirmarVenta(List.of(item));

        assertNotNull(response);
        assertEquals("Venta confirmada exitosamente", response.getMensaje());
        assertEquals(300.0, response.getTotalPagado());

        verify(muebleRepository, times(1)).save(muebleCaptor.capture());

        assertEquals(7, muebleCaptor.getValue().getStock());
    }

    @Test
    @DisplayName("Venta: Exitosa, con stock exacto")
    void testVenta_Exitosa_StockExacto() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(2L);
        item.setCantidad(1);

        ArgumentCaptor<Mueble> muebleCaptor = ArgumentCaptor.forClass(Mueble.class);

        ventaService.confirmarVenta(List.of(item));

        verify(muebleRepository, times(1)).save(muebleCaptor.capture());
        assertEquals(0, muebleCaptor.getValue().getStock());
    }

    @Test
    @DisplayName("Venta: Falla, con stock insuficiente")
    void testVenta_Falla_StockInsuficiente() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(2L);
        item.setCantidad(2);

        Exception e = assertThrows(RuntimeException.class, () -> {
            ventaService.confirmarVenta(List.of(item));
        });

        assertEquals("Stock insuficiente para: Escritorio L", e.getMessage());

        verify(muebleRepository, never()).save(any(Mueble.class));

        assertEquals(1, muebleStock1.getStock());
    }

    @Test
    @DisplayName("Venta: Falla, Mueble ID no existe")
    void testVenta_Falla_MuebleNoExiste() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(99L);
        item.setCantidad(1);

        Exception e = assertThrows(RuntimeException.class, () -> {
            ventaService.confirmarVenta(List.of(item));
        });

        assertEquals("Mueble no encontrado", e.getMessage());
        verify(muebleRepository, never()).save(any(Mueble.class));
    }

    @Test
    @DisplayName("Venta: Falla Parcial (Rollback Transaccional)")
    void testVenta_FallaParcial_Rollback() {
        ItemVentaRequest item1 = new ItemVentaRequest();
        item1.setMuebleId(1L);
        item1.setCantidad(2);

        ItemVentaRequest item2 = new ItemVentaRequest();
        item2.setMuebleId(2L);
        item2.setCantidad(2);

        Exception e = assertThrows(RuntimeException.class, () -> {
            ventaService.confirmarVenta(List.of(item1, item2));
        });

        assertEquals("Stock insuficiente para: Escritorio L", e.getMessage());

        verify(muebleRepository, never()).save(any(Mueble.class));

        assertEquals(10, muebleStock10.getStock());
    }

    @Test
    @DisplayName("Venta: Falla si la cantidad es cero o negativa")
    void testVenta_Falla_CantidadInvalida() {

        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L);
        item.setCantidad(0);
    }
}