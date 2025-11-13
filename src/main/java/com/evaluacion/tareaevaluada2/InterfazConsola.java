package com.evaluacion.tareaevaluada2;

import com.evaluacion.tareaevaluada2.Dto.CrearMuebleRequest;
import com.evaluacion.tareaevaluada2.Dto.ItemVentaRequest;
import com.evaluacion.tareaevaluada2.Dto.MuebleDto;
import com.evaluacion.tareaevaluada2.Dto.VentaResponse;
import com.evaluacion.tareaevaluada2.Modelo.Variante;
import com.evaluacion.tareaevaluada2.Servicio.MuebleService;
import com.evaluacion.tareaevaluada2.Servicio.VentaService;
import com.evaluacion.tareaevaluada2.Servicio.VarianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Profile("!test")
@Component
public class InterfazConsola implements CommandLineRunner {

    @Autowired
    private MuebleService muebleService;
    @Autowired
    private VarianteService varianteService;
    @Autowired
    private VentaService ventaService;

    private Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

    private List<ItemVentaRequest> ultimaCotizacion = null;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- ¡Bienvenido a la Mueblería Hermanos S.A.! ---");

        while (true) {
            imprimirMenu();
            int opcion = 0;
            try {
                opcion = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
                continue;
            }
            scanner.nextLine();

            try {
                switch (opcion) {
                    case 1:
                        crearMueble();
                        break;
                    case 2:
                        crearVariante();
                        break;
                    case 3:
                        listarMuebles();
                        break;
                    case 4:
                        activarMueble();
                        break;
                    case 5:
                        desactivarMueble();
                        break;
                    case 6:
                        crearCotizacion();
                        break;
                    case 7:
                        confirmarVenta();
                        break;
                    case 8:
                        System.out.println("¡Hasta luego!");
                        System.exit(0);
                        return;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error: " + e.getMessage());
            }

            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
        }
    }

    private void imprimirMenu() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Crear Mueble");
        System.out.println("2. Crear Variante");
        System.out.println("3. Listar Muebles");
        System.out.println("4. Activar Mueble");
        System.out.println("5. Desactivar Mueble");
        System.out.println("6. Crear Cotización");
        System.out.println("7. Confirmar Venta");
        System.out.println("8. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void crearMueble() {
        CrearMuebleRequest dto = new CrearMuebleRequest(); //
        System.out.println("--- Crear Nuevo Mueble ---");
        System.out.print("Nombre: ");
        dto.setNombre_mueble(scanner.nextLine());
        System.out.print("Tipo: ");
        dto.setTipo(scanner.nextLine());
        System.out.print("Material: ");
        dto.setMaterial(scanner.nextLine());
        System.out.print("Tamaño (GRANDE, MEDIANO, PEQUENO): ");
        dto.setTamano(scanner.nextLine().toUpperCase());

        System.out.print("Precio Base (ej: 100.0): ");
        dto.setPrecio_base(scanner.nextDouble());
        scanner.nextLine();

        System.out.print("Stock inicial: ");
        dto.setStock(scanner.nextInt());
        scanner.nextLine();

        MuebleDto muebleCreado = muebleService.crearMueble(dto); //
        System.out.println("¡Mueble creado exitosamente! ID: " + muebleCreado.getId()); //
    }

    private void crearVariante() {
        Variante v = new Variante(); //
        System.out.println("--- Crear Nueva Variante ---");
        System.out.print("Nombre (ej: Ruedas): ");
        v.setNombre(scanner.nextLine());
        System.out.print("Tipo (SUMA_FIJA o PORCENTAJE): ");
        v.setTipo(Variante.TipoVariante.valueOf(scanner.nextLine().toUpperCase())); //
        System.out.print("Valor (ej: 20.0 o 0.15): ");
        v.setValor(scanner.nextDouble());
        scanner.nextLine();

        Variante varianteCreada = varianteService.crearVariante(v);
        System.out.println("¡Variante creada exitosamente! ID: " + varianteCreada.getId());
    }

    private void listarMuebles() {
        System.out.println("--- Listado de Muebles ---");
        List<MuebleDto> muebles = muebleService.listarMuebles(); //
        if (muebles.isEmpty()) {
            System.out.println("No hay muebles registrados.");
            return;
        }
        muebles.forEach(System.out::println);
    }

    private void activarMueble() {
        System.out.println("--- Activar Mueble ---");
        System.out.print("Ingrese el ID del mueble a activar: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        MuebleDto mueble = muebleService.activarMueble(id);
        System.out.println("Mueble activado: " + mueble.getNombre() + ", Estado: " + mueble.getEstado()); //
    }

    private void desactivarMueble() {
        System.out.println("--- Desactivar Mueble ---");
        System.out.print("Ingrese el ID del mueble a desactivar: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        MuebleDto mueble = muebleService.desactivarMueble(id); //
        System.out.println("Mueble desactivado: " + mueble.getNombre() + ", Estado: " + mueble.getEstado()); //
    }

    private void crearCotizacion() {
        System.out.println("--- Nueva Cotización ---");
        List<ItemVentaRequest> items = crearListaDeItems();
        if (items.isEmpty()) {
            System.out.println("Cotización cancelada.");
            this.ultimaCotizacion = null;
            return;
        }

        double total = ventaService.calcularCotizacion(items); //
        System.out.printf("El total de la cotización es: $%.2f%n", total);

        this.ultimaCotizacion = items;
        System.out.println("Cotización guardada. Use la opción 7 para confirmarla.");
    }

    private void confirmarVenta() {
        System.out.println("--- Confirmar Venta ---");

        if (this.ultimaCotizacion == null || this.ultimaCotizacion.isEmpty()) {
            System.out.println("Error: No hay ninguna cotización pendiente.");
            System.out.println("Por favor, cree una cotización primero (Opción 6).");
            return;
        }

        System.out.println("Se encontró una cotización pendiente. ¿Desea confirmarla? (S/N)");
        String confirmacion = scanner.nextLine().toUpperCase();

        if (confirmacion.equals("S")) {
            VentaResponse respuesta = ventaService.confirmarVenta(this.ultimaCotizacion); //

            System.out.println("¡Venta Exitosa!");
            System.out.println("Mensaje: " + respuesta.getMensaje()); //
            System.out.printf("Total Pagado: $%.2f%n", respuesta.getTotalPagado()); //

            this.ultimaCotizacion = null;

        } else {
            System.out.println("Venta cancelada. La cotización sigue pendiente.");
        }
    }

    private List<ItemVentaRequest> crearListaDeItems() {
        List<ItemVentaRequest> items = new ArrayList<>();
        while (true) {
            System.out.print("Ingrese ID del mueble (o 0 para terminar): ");
            long muebleId = scanner.nextLong();
            scanner.nextLine();
            if (muebleId == 0) {
                break;
            }

            Optional<MuebleDto> muebleOpt = muebleService.obtenerMueblePorId(muebleId); //

            if (muebleOpt.isEmpty()) {
                System.out.println("Error: No se encontró ningún mueble con el ID: " + muebleId);
                System.out.println("Por favor, intente de nuevo.");
                continue;
            }

            MuebleDto mueble = muebleOpt.get(); //
            System.out.println("Mueble encontrado: " + mueble.getNombre()); //
            System.out.println("Stock disponible: " + mueble.getStock()); //

            System.out.print("Ingrese Cantidad: ");
            int cantidad = scanner.nextInt();
            scanner.nextLine();

            if (cantidad <= 0) {
                System.out.println("Error: La cantidad debe ser mayor a cero.");
                System.out.println("Por favor, intente de nuevo.");
                continue;
            }
            if (cantidad > mueble.getStock()) { //
                System.out.println("Error: Stock insuficiente.");
                System.out.println("Stock disponible para '" + mueble.getNombre() + "': " + mueble.getStock()); //
                System.out.println("Por favor, intente de nuevo.");
                continue;
            }

            System.out.print("Ingrese IDs de Variantes (separados por coma, ej: 1,2) o deje vacío: ");
            String variantesInput = scanner.nextLine();

            List<Long> varianteIds;
            if (variantesInput.isBlank()) {
                varianteIds = new ArrayList<>();
            } else {
                varianteIds = Arrays.stream(variantesInput.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            }

            ItemVentaRequest item = new ItemVentaRequest(); //
            item.setMuebleId(muebleId);
            item.setCantidad(cantidad);
            item.setVarianteIds(varianteIds);
            items.add(item);
            System.out.println("... Item añadido. ...");
        }
        return items;
    }
}