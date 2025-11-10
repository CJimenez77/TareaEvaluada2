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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class VentaServiceTest {

    @Mock
    private MuebleRepository muebleRepository;

    @Mock
    private VarianteRepository varianteRepository;

    @InjectMocks // Inyecta los Mocks de arriba en VentaService
    private VentaService ventaService;

    // --- Objetos de Prueba (Mock Data) ---
    private Mueble muebleStock10;
    private Mueble muebleStock1;
    private Variante varianteFija;
    private Variante variantePorcentaje;

    @BeforeEach
    void setUp() {
        // Configuración inicial antes de CADA prueba

        // Mueble 1: Con mucho stock
        muebleStock10 = Mueble.builder()
                .idMueble(1L)
                .nombreMueble("Silla Gamer")
                .precioBase(100.0)
                .stock(10)
                .build();

        // Mueble 2: Con stock crítico
        muebleStock1 = Mueble.builder()
                .idMueble(2L)
                .nombreMueble("Escritorio L")
                .precioBase(500.0)
                .stock(1)
                .build();

        // Variante 1: Suma fija
        varianteFija = new Variante();
        varianteFija.setId(10L);
        varianteFija.setTipo(TipoVariante.SUMA_FIJA);
        varianteFija.setValor(20.0); // +$20

        // Variante 2: Porcentaje
        variantePorcentaje = new Variante();
        variantePorcentaje.setId(11L);
        variantePorcentaje.setTipo(TipoVariante.PORCENTAJE);
        variantePorcentaje.setValor(0.10); // +10%

        // Definimos el comportamiento de los Mocks
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(muebleStock10));
        when(muebleRepository.findById(2L)).thenReturn(Optional.of(muebleStock1));
        when(muebleRepository.findById(99L)).thenReturn(Optional.empty()); // Mueble inexistente

        when(varianteRepository.findAllById(List.of(10L))).thenReturn(List.of(varianteFija));
        when(varianteRepository.findAllById(List.of(11L))).thenReturn(List.of(variantePorcentaje));
        when(varianteRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(varianteFija, variantePorcentaje));
    }

    // --- Pruebas de calcularCotizacion() ---

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 2, Sin variantes")
    void testCotizacion_ItemUnico_SinVariantes() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L); // Silla ($100)
        item.setCantidad(2);
        item.setVarianteIds(Collections.emptyList());

        double total = ventaService.calcularCotizacion(List.of(item));

        assertEquals(200.0, total); // $100 * 2
    }

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 1, Con variante fija")
    void testCotizacion_ItemUnico_ConVarianteFija() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L); // Silla ($100)
        item.setCantidad(1);
        item.setVarianteIds(List.of(10L)); // +$20

        double total = ventaService.calcularCotizacion(List.of(item));

        assertEquals(120.0, total); // ($100 + $20) * 1
    }

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 1, Con variante porcentaje")
    void testCotizacion_ItemUnico_ConVariantePorcentaje() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L); // Silla ($100)
        item.setCantidad(1);
        item.setVarianteIds(List.of(11L)); // +10%

        double total = ventaService.calcularCotizacion(List.of(item));

        // Le decimos a JUnit que acepte un margen de error (delta) de 0.001
        assertEquals(110.0, total, 0.001); // ($100 * 1.10) * 1
    }

    @Test
    @DisplayName("Cotización: 1 item, Cantidad 2, Con múltiples variantes (fija + porcentaje)")
    void testCotizacion_ItemUnico_ConMultiplesVariantes() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L); // Silla ($100)
        item.setCantidad(2);
        item.setVarianteIds(List.of(10L, 11L)); // +$20, luego +10%

        double total = ventaService.calcularCotizacion(List.of(item));

        // Cálculo: ($100 + $20) = $120
        //          ($120 * 1.10) = $132
        //          $132 * 2 (cantidad) = $264
        assertEquals(264.0, total);
    }

    @Test
    @DisplayName("Cotización: Múltiples items (mixto)")
    void testCotizacion_MultiplesItemsMixtos() {
        // Item 1: 2 Sillas ($100 c/u) sin variantes
        ItemVentaRequest item1 = new ItemVentaRequest();
        item1.setMuebleId(1L);
        item1.setCantidad(2);
        item1.setVarianteIds(Collections.emptyList()); // Total item 1 = $200

        // Item 2: 1 Escritorio ($500) con variante fija (+$20)
        ItemVentaRequest item2 = new ItemVentaRequest();
        item2.setMuebleId(2L);
        item2.setCantidad(1);
        item2.setVarianteIds(List.of(10L)); // Total item 2 = $520

        double total = ventaService.calcularCotizacion(List.of(item1, item2));

        assertEquals(720.0, total); // $200 + $520
    }

    @Test
    @DisplayName("Cotización: Falla si el Mueble ID no existe")
    void testCotizacion_MuebleNoExiste() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(99L); // ID Inexistente
        item.setCantidad(1);

        // Verificamos que lance una RuntimeException
        Exception e = assertThrows(RuntimeException.class, () -> {
            ventaService.calcularCotizacion(List.of(item));
        });

        assertEquals("Mueble no encontrado", e.getMessage());
    }

    // --- Pruebas de confirmarVenta() ---

    @Test
    @DisplayName("Venta: Exitosa, con stock suficiente")
    void testVenta_Exitosa_StockSuficiente() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L); // Silla (Stock 10)
        item.setCantidad(3);

        // Capturador para verificar qué se guarda en la DB
        ArgumentCaptor<Mueble> muebleCaptor = ArgumentCaptor.forClass(Mueble.class);

        // Ejecución
        VentaResponse response = ventaService.confirmarVenta(List.of(item));

        // Verificaciones
        assertNotNull(response);
        assertEquals("Venta confirmada exitosamente", response.getMensaje());
        assertEquals(300.0, response.getTotalPagado()); // $100 * 3

        // Verificar que se llamó a save() UNA vez
        verify(muebleRepository, times(1)).save(muebleCaptor.capture());

        // Verificar que el stock se descontó correctamente
        assertEquals(7, muebleCaptor.getValue().getStock()); // 10 - 3 = 7
    }

    @Test
    @DisplayName("Venta: Exitosa, con stock exacto")
    void testVenta_Exitosa_StockExacto() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(2L); // Escritorio (Stock 1)
        item.setCantidad(1);

        ArgumentCaptor<Mueble> muebleCaptor = ArgumentCaptor.forClass(Mueble.class);

        ventaService.confirmarVenta(List.of(item));

        verify(muebleRepository, times(1)).save(muebleCaptor.capture());
        assertEquals(0, muebleCaptor.getValue().getStock()); // 1 - 1 = 0
    }

    @Test
    @DisplayName("Venta: Falla, con stock insuficiente")
    void testVenta_Falla_StockInsuficiente() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(2L); // Escritorio (Stock 1)
        item.setCantidad(2); // Pide 2

        // Verificamos que lance la excepción
        Exception e = assertThrows(RuntimeException.class, () -> {
            ventaService.confirmarVenta(List.of(item));
        });

        assertEquals("Stock insuficiente para: Escritorio L", e.getMessage());

        // ¡MUY IMPORTANTE! Verificar que NUNCA se llamó a save()
        verify(muebleRepository, never()).save(any(Mueble.class));

        // Verificar que el stock del objeto mock no cambió
        assertEquals(1, muebleStock1.getStock());
    }

    @Test
    @DisplayName("Venta: Falla, Mueble ID no existe")
    void testVenta_Falla_MuebleNoExiste() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(99L); // ID Inexistente
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
        // Item 1: 2 Sillas ($100 c/u), Stock 10 (Suficiente)
        ItemVentaRequest item1 = new ItemVentaRequest();
        item1.setMuebleId(1L);
        item1.setCantidad(2);

        // Item 2: 2 Escritorios ($500), Stock 1 (Insuficiente)
        ItemVentaRequest item2 = new ItemVentaRequest();
        item2.setMuebleId(2L);
        item2.setCantidad(2);

        // Verificamos que falle por el item 2
        Exception e = assertThrows(RuntimeException.class, () -> {
            ventaService.confirmarVenta(List.of(item1, item2));
        });

        assertEquals("Stock insuficiente para: Escritorio L", e.getMessage());

        // Verificación de Rollback: NINGÚN 'save' debe ejecutarse
        verify(muebleRepository, never()).save(any(Mueble.class));

        // Verificamos que el stock del item 1 (que sí tenía) NO se descontó
        assertEquals(10, muebleStock10.getStock());
    }

    @Test
    @DisplayName("Venta: Falla si la cantidad es cero o negativa")
    void testVenta_Falla_CantidadInvalida() {
        // (Nota: Esto requiere una validación en VentaService)
        // Añadamos esta validación en VentaService (al inicio de confirmarVenta):
        // for (ItemVentaRequest item : items) {
        //     if (item.getCantidad() <= 0) {
        //         throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        //     }
        // }

        ItemVentaRequest item = new ItemVentaRequest();
        item.setMuebleId(1L);
        item.setCantidad(0);

        // Asumiendo que añadiste la validación de arriba:
        // Exception e = assertThrows(IllegalArgumentException.class, () -> {
        //     ventaService.confirmarVenta(List.of(item));
        // });
        // assertEquals("La cantidad debe ser mayor a cero", e.getMessage());
        // verify(muebleRepository, never()).save(any());
    }
}