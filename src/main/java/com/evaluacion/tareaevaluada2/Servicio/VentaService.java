package com.evaluacion.tareaevaluada2.Servicio;

import com.evaluacion.tareaevaluada2.Dto.ItemVentaRequest;
import com.evaluacion.tareaevaluada2.Dto.VentaResponse;
import com.evaluacion.tareaevaluada2.Modelo.Mueble;
import com.evaluacion.tareaevaluada2.Modelo.Variante;
import com.evaluacion.tareaevaluada2.Repositorio.MuebleRepository;
import com.evaluacion.tareaevaluada2.Repositorio.VarianteRepository;
import com.evaluacion.tareaevaluada2.Strategy.CalculoPrecioFactory;
import com.evaluacion.tareaevaluada2.Strategy.CalculoPrecioStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaService {

    @Autowired
    private MuebleRepository muebleRepository;
    @Autowired
    private VarianteRepository varianteRepository;

    public double calcularCotizacion(List<ItemVentaRequest> items) {
        double totalGeneral = 0;

        for (ItemVentaRequest item : items) {
            Mueble mueble = muebleRepository.findById(item.getMuebleId())
                    .orElseThrow(() -> new RuntimeException("Mueble no encontrado"));

            double precioItemUnitario = mueble.getPrecioBase();

            if (item.getVarianteIds() != null && !item.getVarianteIds().isEmpty()) {
                List<Variante> variantes = varianteRepository.findAllById(item.getVarianteIds());
                for (Variante v : variantes) {
                    CalculoPrecioStrategy Strategy = CalculoPrecioFactory.getStrategy(v.getTipo());
                    precioItemUnitario = Strategy.calcular(precioItemUnitario, v);
                }
            }

            totalGeneral += precioItemUnitario * item.getCantidad();
        }
        return totalGeneral;
    }

    @Transactional
    public VentaResponse confirmarVenta(List<ItemVentaRequest> items) {

        double totalVenta = calcularCotizacion(items);

        for (ItemVentaRequest item : items) {
            Mueble mueble = muebleRepository.findById(item.getMuebleId())
                    .orElseThrow(() -> new RuntimeException("Mueble no encontrado"));

            if (mueble.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + mueble.getNombreMueble());
            }
        }

        for (ItemVentaRequest item : items) {
            Mueble mueble = muebleRepository.findById(item.getMuebleId()).get();

            mueble.setStock(mueble.getStock() - item.getCantidad());
            muebleRepository.save(mueble);
        }

        return new VentaResponse("Venta confirmada exitosamente", totalVenta);
    }
}