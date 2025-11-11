package com.example.evaluacion2IngDeSoftware;

import com.example.evaluacion2IngDeSoftware.Modelo.*;
import com.example.evaluacion2IngDeSoftware.Servicios.CotizacionServicio;
import com.example.evaluacion2IngDeSoftware.Servicios.MuebleServicio;
import com.example.evaluacion2IngDeSoftware.Servicios.VarianteServicio;
import com.example.evaluacion2IngDeSoftware.Servicios.VentaServicio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
@Profile({"default","cli"}) // se ejecuta en default y en cli; tus tests usan @ActiveProfiles("test") y no se dispara
public class ConsolaApp implements CommandLineRunner {

    private final MuebleServicio muebleSrv;
    private final VarianteServicio varianteSrv;
    private final CotizacionServicio cotSrv;
    private final VentaServicio ventaSrv;

    public ConsolaApp(MuebleServicio muebleSrv,
                      VarianteServicio varianteSrv,
                      CotizacionServicio cotSrv,
                      VentaServicio ventaSrv) {
        this.muebleSrv = muebleSrv;
        this.varianteSrv = varianteSrv;
        this.cotSrv = cotSrv;
        this.ventaSrv = ventaSrv;
    }

    @Override
    public void run(String... args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Mueblería (CLI minimal) ===");
            System.out.println("1) Crear Variante");
            System.out.println("2) Crear Mueble");
            System.out.println("3) Crear Cotización");
            System.out.println("4) Agregar Item a Cotización");
            System.out.println("5) Confirmar Cotización");
            System.out.println("6) Confirmar Venta desde Cotización");
            System.out.println("7) Listar Muebles");
            System.out.println("8) Desactivar Mueble");
            System.out.println("9) Activar Mueble");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            String op = in.nextLine().trim();
            try {
                switch (op) {
                    case "1" -> {
                        System.out.print("Nombre variante: ");
                        String nombre = in.nextLine().trim();
                        System.out.print("Incremento de precio (ej 1500): ");
                        BigDecimal inc = new BigDecimal(in.nextLine().trim());

                        Variante v = new Variante();
                        v.setNombre(nombre);
                        v.setIncrementoPrecio(inc);
                        v = varianteSrv.crear(v);
                        System.out.println("OK -> Variante id=" + v.getId());
                    }
                    case "2" -> {
                        System.out.print("Nombre mueble: ");
                        String nombre = in.nextLine().trim();
                        System.out.print("Tipo (SILLA/MESA/CAJON/ESTANTE/SILLON): ");
                        TipoMueble tipo = TipoMueble.valueOf(in.nextLine().trim().toUpperCase());
                        System.out.print("Tamaño (PEQUENO/MEDIANO/GRANDE): ");
                        Tamano tam = Tamano.valueOf(in.nextLine().trim().toUpperCase());
                        System.out.print("Material: ");
                        String material = in.nextLine().trim();
                        System.out.print("Precio base: ");
                        BigDecimal precio = new BigDecimal(in.nextLine().trim());
                        System.out.print("Stock: ");
                        int stock = Integer.parseInt(in.nextLine().trim());

                        Mueble m = new Mueble();
                        m.setNombre(nombre);
                        m.setTipo(tipo);
                        m.setTamano(tam);
                        m.setMaterial(material);
                        m.setPrecioBase(precio);
                        m.setStock(stock);
                        m.setEstado(EstadoMueble.ACTIVO);

                        m = muebleSrv.crear(m);
                        System.out.println("OK -> Mueble id=" + m.getId());
                    }
                    case "3" -> {
                        Cotizacion c = cotSrv.crear();
                        System.out.println("OK -> Cotización id=" + c.getId());
                    }
                    case "4" -> {
                        System.out.print("ID cotización: ");
                        long cotId = Long.parseLong(in.nextLine().trim());
                        System.out.print("ID mueble: ");
                        long muebleId = Long.parseLong(in.nextLine().trim());
                        System.out.print("ID variante (vacío si ninguna): ");
                        String vtxt = in.nextLine().trim();
                        Long varId = vtxt.isBlank() ? null : Long.parseLong(vtxt);
                        System.out.print("Cantidad: ");
                        int cantidad = Integer.parseInt(in.nextLine().trim());

                        cotSrv.agregarItem(cotId, muebleId, varId, cantidad);
                        cotSrv.recalcular(cotId);
                        System.out.println("OK -> Item agregado y total recalculado.");
                    }
                    case "5" -> {
                        System.out.print("ID cotización: ");
                        long cotId = Long.parseLong(in.nextLine().trim());
                        Cotizacion c = cotSrv.confirmar(cotId);
                        System.out.println("OK -> Cotización confirmada. Total=" + c.getTotal());
                    }
                    case "6" -> {
                        // dentro del case 6:
                        try {
                            System.out.print("ID cotización confirmada: ");
                            long id = Long.parseLong(in.nextLine().trim());
                            var v = ventaSrv.confirmarVentaDesdeCotizacion(id);
                            System.out.println("Venta confirmada (id = " + v.getId() + ").");
                        } catch (Exception ex) {
                            System.out.println("Error: " + ex.getMessage());
                        }

                    }
                    case "7" -> {
                        List<Mueble> lista = muebleSrv.listar();
                        if (lista.isEmpty()) {
                            System.out.println("(sin muebles)");
                        } else {
                            System.out.println("ID | Nombre | Estado | Stock | Precio");
                            for (Mueble m : lista) {
                                System.out.printf("%d | %s | %s | %d | %s%n",
                                        m.getId(), m.getNombre(), m.getEstado(), m.getStock(), m.getPrecioBase());
                            }
                        }
                    }
                    case "8" -> {
                        System.out.print("ID mueble a desactivar: ");
                        long id = Long.parseLong(in.nextLine().trim());
                        Mueble m = muebleSrv.desactivar(id);
                        System.out.println("OK -> Desactivado: id=" + m.getId() + " estado=" + m.getEstado());
                    }
                    case "9" -> {
                        System.out.print("ID mueble a activar: ");
                        long id = Long.parseLong(in.nextLine().trim());
                        Mueble m = muebleSrv.activar(id);
                        System.out.println("OK -> Activado: id=" + m.getId() + " estado=" + m.getEstado());
                    }
                    case "0" -> {
                        System.out.println("Saliendo...");
                        System.exit(0);
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
