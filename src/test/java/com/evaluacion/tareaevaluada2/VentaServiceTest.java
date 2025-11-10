package com.evaluacion.tareaevaluada2;

import com.evaluacion.tareaevaluada2.Modelo.Mueble;
import com.evaluacion.tareaevaluada2.Modelo.Variante.TipoVariante;
import com.evaluacion.tareaevaluada2.Modelo.Variante;
import com.evaluacion.tareaevaluada2.Repositorio.MuebleRepository;
import com.evaluacion.tareaevaluada2.Repositorio.VarianteRepository;
import com.evaluacion.tareaevaluada2.Dto.ItemVentaRequest;
import com.evaluacion.tareaevaluada2.Servicio.VentaService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VentaServiceTest {

    @Mock
    private MuebleRepository muebleRepository;

    @Mock
    private VarianteRepository varianteRepository;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void testConfirmarVenta_ConStockInsuficiente() {
        // 1. Preparación usando el Patrón Builder
        Mueble mesa = Mueble.builder()
                .idMueble(1L)
                .nombreMueble("Mesa de Centro")
                .stock(1) // Solo 1 en stock
                .precioBase(100.0)
                .build();

        when(muebleRepository.findById(1L)).thenReturn(Optional.of(mesa));

        ItemVentaRequest pedido = new ItemVentaRequest();
        pedido.setMuebleId(1L);
        pedido.setCantidad(2); // Pide 2

        // 2. Ejecución y Verificación
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            ventaService.confirmarVenta(List.of(pedido));
        });

        assertEquals("Stock insuficiente para: Mesa de Centro", e.getMessage());
        assertEquals(1, mesa.getStock()); // El stock no cambió
        verify(muebleRepository, never()).save(any()); // No se guardó
    }

    @Test
    void testCalcularCotizacion_ConFactoryYStrategy() {
        // 1. Preparación con Builder
        Mueble silla = Mueble.builder()
                .idMueble(1L)
                .precioBase(100.0) // Precio base $100
                .build();

        Variante ruedas = new Variante();
        ruedas.setId(10L);
        ruedas.setTipo(TipoVariante.SUMA_FIJA);
        ruedas.setValor(20.0); // +$20

        Variante barniz = new Variante();
        barniz.setId(11L);
        barniz.setTipo(TipoVariante.PORCENTAJE);
        barniz.setValor(0.10); // +10%

        when(muebleRepository.findById(1L)).thenReturn(Optional.of(silla));
        when(varianteRepository.findAllById(List.of(10L, 11L)))
                .thenReturn(List.of(ruedas, barniz));

        ItemVentaRequest pedido = new ItemVentaRequest();
        pedido.setMuebleId(1L);
        pedido.setCantidad(2); // Pedimos 2 sillas
        pedido.setVarianteIds(List.of(10L, 11L));

        // 2. Ejecución
        double total = ventaService.calcularCotizacion(List.of(pedido));

        // 3. Verificación
        // Cálculo esperado por silla:
        // 1. Precio base: $100
        // 2. Strategy SUMA_FIJA: $100 + $20 = $120
        // 3. Strategy PORCENTAJE: $120 * (1 + 0.10) = $132
        // Total (cantidad 2): $132 * 2 = $264

        assertEquals(264.0, total);
    }
}