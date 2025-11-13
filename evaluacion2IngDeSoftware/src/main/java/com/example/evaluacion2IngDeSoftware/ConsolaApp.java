package com.example.evaluacion2IngDeSoftware;

import com.example.evaluacion2IngDeSoftware.Modelo.*;
import com.example.evaluacion2IngDeSoftware.Servicios.CotizacionServicio;
import com.example.evaluacion2IngDeSoftware.Servicios.MuebleServicio;
import com.example.evaluacion2IngDeSoftware.Servicios.VarianteServicio;
import com.example.evaluacion2IngDeSoftware.Servicios.VentaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
@Profile({"default","cli"})
public class ConsolaApp implements CommandLineRunner {

    @Autowired
    private MuebleServicio muebleServicio;
    @Autowired
    private VarianteServicio varianteServicio;
    @Autowired
    private CotizacionServicio cotizacionServicio;
    @Autowired
    private VentaServicio ventaServicio;


    @Override
    public void run(String... args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== MUNÚ MUEBLERIA ISW ===");
            System.out.println("=======================================================================================");
            System.out.println("1) Listar Muebles");
            System.out.println("2) Administrar Muebles");
            System.out.println("3) Administrar Variantes");
            System.out.println("=======================================================================================");
            System.out.println("4) Iniciar una venta");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            String op = in.nextLine().trim();
            try {
                switch (op) {
                    case "1":
                        listarMuebles(false);
                        break;
                    case "2":
                        administrarMuebles(in);
                        break;
                    case "3":
                        administrarVariantes(in);
                        break;
                    case "4":
                        procesoDeVentaGuiado(in);
                        break;
                    case "0":
                        System.out.println("Adios");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción inválida.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void procesoDeVentaGuiado(Scanner sc) {
        System.out.println("\nCREANDO UNA VENTA");
        Cotizacion cotizacion;
        try {
            cotizacion = cotizacionServicio.crear();
            System.out.println("Se ha creado la Cotización N°: " + cotizacion.getId());
        } catch (Exception e) {
            System.out.println("Error al crear cotización: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\nCotización N°: " + cotizacion.getId());
            System.out.println("1) Agregar Mueble a la cotización");
            System.out.println("2) Quitar Mueble de la cotización");
            System.out.println("3) Ver items y total");
            System.out.println("=======================================================================================");
            System.out.println("4) Confirmar venta");
            System.out.println("0) Cancelar venta");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();

            if (op.equals("1")) {
                try {
                    listarMuebles(true);
                    System.out.print("Ingrese el ID del mueble: ");
                    long muebleId = Long.parseLong(sc.nextLine().trim());
                    mostrarListaDeVariantes();
                    System.out.print("Ingrese el ID de la variante (dejar vacío si no aplica): ");
                    String variante = sc.nextLine().trim();
                    Long varianteId = (variante.isBlank() || variante.equals("0")) ? null : Long.parseLong(variante);
                    System.out.print("Cantidad: ");
                    int cantidad = Integer.parseInt(sc.nextLine().trim());

                    cotizacion = cotizacionServicio.agregarItem(cotizacion.getId(), muebleId, varianteId, cantidad);

                    System.out.println("Se ha agregado el Item");
                    verCotizacionActual(cotizacion);
                } catch (NumberFormatException e) {
                    System.out.println("El ID y la cantidad deben ser números.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }

            } else if (op.equals("2")) {
                try {
                    verCotizacionActual(cotizacion);
                    if (cotizacion.getItems().isEmpty()) {
                        System.out.println("No hay items para quitar.");
                        continue;
                    }
                    System.out.print("ID del Item a quitar (no el ID del mueble): ");
                    long itemId = Long.parseLong(sc.nextLine().trim());

                    cotizacion = cotizacionServicio.quitarItem(cotizacion.getId(), itemId);

                    System.out.println("Item quitado.");
                    verCotizacionActual(cotizacion);
                } catch (NumberFormatException e) {
                    System.out.println("El ID del item debe ser un número.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }

            } else if (op.equals("3")) {
                cotizacion = cotizacionServicio.obtener(cotizacion.getId());
                verCotizacionActual(cotizacion);

            } else if (op.equals("4".trim())) {
                try {
                    System.out.println();
                    Venta venta = ventaServicio.confirmarVentaDesdeCotizacion(cotizacion.getId());

                    System.out.println("VENTA GENERADA CON ÉXITO");
                    System.out.println("ID Venta: " + venta.getId());
                    System.out.println("Total venta: " + venta.getTotal());
                    System.out.println("El stock se ha actualizado");
                    break;

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("La cotización sigue abierta");
                }
            } else if (op.equals("0".trim())) {
                System.out.println("Proceso de venta cancelado. La cotización N° " + cotizacion.getId() + " fue descartada.");
                break;
            }
        }
    }

    private void verCotizacionActual(Cotizacion c) {
        System.out.println("Resumen Cotización N°: " + c.getId());
        if (c.getItems().isEmpty()) {
            System.out.println("No existe la cotizacion");
        } else {
            System.out.printf("%-7s | %-25s | %-15s | %-25s | %-12s%n",
                    "ID_Item", "Mueble", "Cantidad", "Precio Unitario", "Subtotal");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
            for (CotizacionItem item : c.getItems()) {
                System.out.printf("%-7d | %-25s | %-15d | %25s | %12s%n",
                        item.getId(), item.getMueble().getNombre(), item.getCantidad(), item.getPrecioUnitarioAplicado(), item.getSubtotal());
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("TOTAL: " + c.getTotal());
        System.out.println("Confirmada: " + (c.getConfirmada() ? "SI" : "NO"));
    }

    private void administrarMuebles(Scanner sc) {
        System.out.println("\nADMINISTRAR MUEBLES");
        System.out.println("===========================================================================================");
        System.out.println("1) Crear mueble");
        System.out.println("2) Desactivar mueble");
        System.out.println("3) Activar mueble");
        System.out.println("0) Volver al menú principal");
        System.out.print("Opción: ");
        String op = sc.nextLine().trim();
        try {
            switch (op) {
                case "1": {
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine().trim();
                    System.out.println("ingrese el tipo de mueble: ");
                    System.out.println("SILLA - MESA - CAJON - ESTANTE - SILLON");
                    System.out.print("tipo: ");
                    TipoMueble tipo = TipoMueble.valueOf(sc.nextLine().trim().toUpperCase());
                    System.out.println("Ingrese el tamaño del mueble: ");
                    System.out.println("PEQUENO - MEDIANO - GRANDE): ");
                    System.out.print("Tamaño: ");
                    Tamano tam = Tamano.valueOf(sc.nextLine().trim().toUpperCase());
                    System.out.print("Material: ");
                    String material = sc.nextLine().trim();
                    System.out.print("Precio base: ");
                    BigDecimal precio = new BigDecimal(sc.nextLine().trim());
                    System.out.print("Stock: ");
                    int stock = Integer.parseInt(sc.nextLine().trim());

                    Mueble mueble = new Mueble();
                    mueble.setNombre(nombre);
                    mueble.setTipo(tipo);
                    mueble.setTamano(tam);
                    mueble.setMaterial(material);
                    mueble.setPrecioBase(precio);
                    mueble.setStock(stock);
                    mueble.setEstado(EstadoMueble.ACTIVO);

                    mueble = muebleServicio.crear(mueble);

                    System.out.println("Se ha creado el mueble con ID=" + mueble.getId());
                    break;
                }
                case "2": {
                    listarMuebles(false);
                    System.out.print("ID mueble que desea DESACTIVAR: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Mueble m = muebleServicio.desactivar(id);
                    System.out.println("Mueble desactivado");
                    System.out.println("ID: " + m.getId());
                    System.out.println("Estado: " + m.getEstado());
                    break;
                }
                case "3": {
                    listarMuebles(false);
                    System.out.print("ID mueble que desea ACTIVAR: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Mueble m = muebleServicio.activar(id);
                    System.out.println("Mueble activado");
                    System.out.println("ID: " + m.getId());
                    System.out.println("Estado: " + m.getEstado());
                    break;
                }
                case "0": {
                    break;
                }
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void mostrarListaDeVariantes() {
        System.out.println("\nVARIANTES CREADAS");
        System.out.println("===========================================================================================");
        try {
            List<Variante> variantes = varianteServicio.listar();
            if (variantes.isEmpty()) {
                System.out.println("No se han creado variantes");
            } else {
                System.out.printf("%-5s | %-25s | %12s%n", "ID", "Nombre", "Incremento");
                System.out.println("------------------------------------------------------------------------------------------------------------------------------");
                for (Variante var : variantes) {
                    System.out.printf("%-5d | %-25s | %12s%n",
                            var.getId(),
                            var.getNombre(),
                            var.getIncrementoPrecio());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void administrarVariantes(Scanner in) {
        System.out.println("\nADMINISTRAR VARIANTES");
        System.out.println("===========================================================================================");
        mostrarListaDeVariantes();
        System.out.println("===========================================================================================");
        System.out.println("1) Crear variante");
        System.out.println("0) Volver al menú principal");
        System.out.print("Opción: ");
        String op = in.nextLine().trim();

        if (op.equals("1")) {
            try {
                System.out.print("Nombre: ");
                String nombre = in.nextLine().trim();
                System.out.print("Incremento de precio: ");
                BigDecimal inc = new BigDecimal(in.nextLine().trim());

                Variante v = new Variante();

                v.setNombre(nombre);
                v.setIncrementoPrecio(inc);
                v = varianteServicio.crear(v);

                System.out.println("Se ha creado la variante con id: " + v.getId());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listarMuebles(boolean soloActivos) {
        if (soloActivos) {
            System.out.println("\nLISTADO DE MUEBLES EN VENTA");
        } else {
            System.out.println("\nLISTADO DE MUEBLES EXISTENTES");
        }

        List<Mueble> lista;
        if (soloActivos) {
            lista = muebleServicio.listarActivos();
        } else {
            lista = muebleServicio.listar();
        }

        if (lista.isEmpty()) {
            System.out.println("(Sin muebles para mostrar)");
        } else {
            System.out.printf("%-5s | %-25s | %-10s | %-7s | %12s%n",
                    "ID", "Nombre", "Estado", "Stock", "Precio Base");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");

            for (Mueble m : lista) {
                System.out.printf("%-5d | %-25s | %-10s | %-7d | %12s%n",
                        m.getId(), m.getNombre(), m.getEstado(), m.getStock(), m.getPrecioBase());
            }
        }
    }
}