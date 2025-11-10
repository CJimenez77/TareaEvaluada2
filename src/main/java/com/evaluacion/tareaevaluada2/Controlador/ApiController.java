package com.evaluacion.tareaevaluada2.Controlador;

import com.evaluacion.tareaevaluada2.Dto.CrearMuebleRequest;
import com.evaluacion.tareaevaluada2.Dto.ItemVentaRequest;
import com.evaluacion.tareaevaluada2.Dto.MuebleDto;
import com.evaluacion.tareaevaluada2.Dto.VentaResponse;
import com.evaluacion.tareaevaluada2.Servicio.MuebleMapper;
import com.evaluacion.tareaevaluada2.Servicio.MuebleService; // Necesitamos un MuebleService
import com.evaluacion.tareaevaluada2.Servicio.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    // (Necesitamos crear un MuebleService para el CRUD)
    // Asumamos que MuebleService maneja el CRUD básico
    @Autowired
    private MuebleService muebleService;

    @Autowired
    private VentaService ventaService;

    // --- Endpoints de Muebles (Usando DTOs) ---

    @PostMapping("/muebles")
    public ResponseEntity<MuebleDto> crearMueble(@RequestBody CrearMuebleRequest Dto) {
        MuebleDto muebleCreado = muebleService.crearMueble(Dto);
        return new ResponseEntity<>(muebleCreado, HttpStatus.CREATED);
    }

    @GetMapping("/muebles")
    public ResponseEntity<List<MuebleDto>> listarMuebles() {
        return ResponseEntity.ok(muebleService.listarMuebles());
    }

    // --- Endpoints de Ventas ---

    @PostMapping("/cotizar")
    public ResponseEntity<Double> cotizar(@RequestBody List<ItemVentaRequest> items) {
        double total = ventaService.calcularCotizacion(items);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/ventas")
    public ResponseEntity<VentaResponse> vender(@RequestBody List<ItemVentaRequest> items) {
        try {
            VentaResponse respuesta = ventaService.confirmarVenta(items);
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            // Manejo de error (ej: stock insuficiente)
            return ResponseEntity.badRequest().body(new VentaResponse(e.getMessage(), 0.0));
        }
    }
}