package services;

import enums.EstadoPago;
import enums.FormaPago;
import model.*;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FacturaService {

    private final SistemaGestion sistema;
    private final Scanner scanner;

    private static final double EPS = 0.005;

    public FacturaService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE FACTURAS ---");
            System.out.println("1. Crear factura");
            System.out.println("2. Listar facturas");
            System.out.println("3. Mostrar detalle de factura");
            System.out.println("4. Mostrar todas las facturas");
            System.out.println("5. Registrar pago de factura");
            System.out.println("6. Imprimir todas las facturas PDF");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> crear();
                    case 2 -> listar();
                    case 3 -> detalle();
                    case 4 -> mostrarTodas();
                    case 5 -> registrarPago();
                    case 6 -> imprimirTodasPdf();
                    case 0 -> { /* volver */ }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    public void crear() {
        System.out.println("\n--- CREAR NUEVA FACTURA ---");

        if (sistema.getClientes().isEmpty() || sistema.getEmpleados().isEmpty()
                || (sistema.getProductos().isEmpty() && sistema.getServicios().isEmpty())) {
            System.out.println("No hay suficientes datos para crear una factura.");
            System.out.println("Necesita al menos un cliente, un empleado y un producto o servicio.");
            return;
        }

        try {
            System.out.print("Número de factura: ");
            String numero = scanner.nextLine();

            // Cliente
            System.out.println("Seleccione el cliente:");
            for (int i = 0; i < sistema.getClientes().size(); i++) {
                System.out.println((i + 1) + ". " + sistema.getClientes().get(i).getNombre());
            }
            int idxCliente = seleccionarIndice("Seleccione el número del cliente: ", sistema.getClientes().size());
            Cliente cliente = sistema.getClientes().get(idxCliente);

            // Empleado
            System.out.println("Seleccione el empleado:");
            for (int i = 0; i < sistema.getEmpleados().size(); i++) {
                System.out.println((i + 1) + ". " + sistema.getEmpleados().get(i).getNombre());
            }
            int idxEmpleado = seleccionarIndice("Seleccione el número del empleado: ", sistema.getEmpleados().size());
            Empleado empleado = sistema.getEmpleados().get(idxEmpleado);

            // Forma de pago
            System.out.println("Seleccione la forma de pago:");
            FormaPago[] formas = FormaPago.values();
            for (int i = 0; i < formas.length; i++) {
                System.out.println((i + 1) + ". " + formas[i]);
            }
            int idxPago = seleccionarIndice("Seleccione el número de la forma de pago: ", formas.length);
            FormaPago formaPago = formas[idxPago];

            // Crear factura
            Factura factura = new Factura(numero, new Date(), cliente, empleado, formaPago);

            // Ítems
            int opcionItem;
            do {
                System.out.println("\n¿Qué desea agregar?");
                System.out.println("1. Producto");
                System.out.println("2. Servicio");
                System.out.println("0. Finalizar");
                System.out.print("Seleccione una opción: ");
                opcionItem = scanner.nextInt();
                scanner.nextLine();

                if (opcionItem == 1 && !sistema.getProductos().isEmpty()) {
                    for (int i = 0; i < sistema.getProductos().size(); i++) {
                        Producto p = sistema.getProductos().get(i);
                        System.out.println((i + 1) + ". " + p.getNombre() + " - $" + p.getPrecio());
                    }
                    int idxProd = seleccionarIndice("Número de producto: ", sistema.getProductos().size());
                    factura.agregarItem(sistema.getProductos().get(idxProd));

                } else if (opcionItem == 2 && !sistema.getServicios().isEmpty()) {
                    for (int i = 0; i < sistema.getServicios().size(); i++) {
                        Servicio s = sistema.getServicios().get(i);
                        System.out.println((i + 1) + ". " + s.getNombre() + " - $" + s.getPrecio());
                    }
                    int idxServ = seleccionarIndice("Número de servicio: ", sistema.getServicios().size());
                    factura.agregarItem(sistema.getServicios().get(idxServ));

                } else if (opcionItem != 0) {
                    System.out.println("Opción no válida o no hay elementos disponibles.");
                }
            } while (opcionItem != 0);

            // Descuento
            System.out.print("Ingrese % de descuento (0 si no aplica): ");
            double d = 0;
            try {
                d = scanner.nextDouble();
                scanner.nextLine();
                if (d < 0) d = 0;
                if (d > 100) d = 100;
            } catch (InputMismatchException e) {
                System.out.println("Descuento inválido. Se aplicará 0%.");
                scanner.nextLine();
            }
            factura.setDescuento(d);

            // ===== Impuesto EXTRA =====
            double extra = 0.0;
            while (true) {
                try {
                    System.out.print("Ingrese % de impuesto EXTRA (0 si no aplica): ");
                    extra = scanner.nextDouble();
                    scanner.nextLine();
                    if (extra < 0) {
                        System.out.println("No puede ser negativo.");
                        continue;
                    }
                    if (extra > 100) {
                        System.out.println("Demasiado alto. Ingrese un valor razonable (0 a 100).");
                        continue;
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Por favor, ingrese un número válido.");
                    scanner.nextLine();
                }
            }
            factura.setImpuestoExtra(extra);

            // Resumen
            double subtotal = redondear2(factura.calcularSubtotal());
            double iva      = redondear2(factura.calcularImpuestos()); // 21%
            double impExtra = redondear2(subtotal * (extra / 100.0));
            double total    = redondear2(factura.calcularTotal());

            System.out.println("Subtotal: $" + String.format("%.2f", subtotal));
            System.out.println("IVA (21%): $" + String.format("%.2f", iva));
            if (extra > 0) {
                System.out.println("Impuesto extra (" + String.format("%.2f", extra) + "%): $" + String.format("%.2f", impExtra));
            }
            System.out.println("TOTAL: $" + String.format("%.2f", total));

            sistema.agregarFactura(factura);
            System.out.println("Factura creada exitosamente.");

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida.");
            scanner.nextLine();
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE FACTURAS ---");
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }
        for (int i = 0; i < sistema.getFacturas().size(); i++) {
            Factura f = sistema.getFacturas().get(i);
            String estado = (f.getPago() != null) ? f.getPago().getEstado().toString() : "SIN PAGO";
            System.out.println((i + 1) + ". Factura #" + f.getNumero() +
                    " - Cliente: " + f.getCliente().getNombre() +
                    " - Total: $" + String.format("%.2f", f.calcularTotal()) +
                    " - Estado: " + estado);
        }
    }

    public void detalle() {
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }

        System.out.println("Seleccione la factura a mostrar:");
        for (int i = 0; i < sistema.getFacturas().size(); i++) {
            Factura f = sistema.getFacturas().get(i);
            System.out.println((i + 1) + ". Factura #" + f.getNumero() +
                    " - Cliente: " + f.getCliente().getNombre());
        }
        int idx = seleccionarIndice("Seleccione el número de la factura: ", sistema.getFacturas().size());
        imprimirEnConsola(sistema.getFacturas().get(idx));
    }

    public void mostrarTodas() {
        System.out.println("\n=== TODAS LAS FACTURAS ===");
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }
        for (Factura f : sistema.getFacturas()) {
            imprimirEnConsola(f);
        }
    }

    public void registrarPago() {
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }

        System.out.println("\n--- REGISTRAR PAGO DE FACTURA ---");
        for (int i = 0; i < sistema.getFacturas().size(); i++) {
            Factura f = sistema.getFacturas().get(i);
            String estadoActual = (f.getPago() != null ? f.getPago().getEstado().toString() : "PENDIENTE");
            System.out.println((i + 1) + ". Factura #" + f.getNumero()
                    + " - Cliente: " + f.getCliente().getNombre()
                    + " - Estado actual: " + estadoActual
                    + " - Total: $" + String.format("%.2f", redondear2(f.calcularTotal())));
        }

        int idx = seleccionarIndice("Seleccione el número de la factura: ", sistema.getFacturas().size());
        Factura factura = sistema.getFacturas().get(idx);

        double total  = redondear2(factura.calcularTotal());
        double pagado = (factura.getPago() != null) ? redondear2(factura.getPago().getMonto()) : 0.0;
        double due    = redondear2(total - pagado);          // lo que falta pagar ahora

        if (due <= EPS) {
            if (factura.getPago() != null) factura.getPago().setEstado(EstadoPago.CANCELADO);
            System.out.println("La factura ya se encuentra CANCELADA.");
            return;
        }

        System.out.print("Ingrese el monto a pagar: ");
        double monto;
        try {
            monto = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Monto inválido.");
            scanner.nextLine();
            return;
        }

        double abono = redondear2(Math.max(0, monto));
        double nuevoPagado;
        EstadoPago nuevoEstado;
        double saldo;

        if (abono >= due - EPS) {
            nuevoPagado = total;
            nuevoEstado = EstadoPago.CANCELADO;
            saldo = 0.0;
        } else if (abono > 0) {
            nuevoPagado = redondear2(pagado + abono);
            nuevoEstado = EstadoPago.PARCIAL;
            saldo = redondear2(total - nuevoPagado);
        } else {
            nuevoPagado = pagado;
            nuevoEstado = (pagado > EPS) ? EstadoPago.PARCIAL : EstadoPago.PENDIENTE;
            saldo = redondear2(total - nuevoPagado);
        }

        if (factura.getPago() == null) {
            factura.setPago(new Pago(nuevoPagado, new Date(), factura.getFormaPago(), nuevoEstado));
        } else {
            factura.getPago().setMonto(nuevoPagado);
            factura.getPago().setEstado(nuevoEstado);
        }

        System.out.println("Pago " + (nuevoEstado == EstadoPago.CANCELADO ? "completo"
                : (nuevoEstado == EstadoPago.PARCIAL ? "parcial" : "no registrado")) +
                ". Estado: " + nuevoEstado);
        System.out.println("Monto pagado acumulado: $" + String.format("%.2f", nuevoPagado));
        System.out.println("Saldo pendiente: $" + String.format("%.2f", Math.max(0, saldo)));
    }

    public void imprimirTodasPdf() {
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }

        System.out.println("\n=== GENERANDO TODOS LOS PDF ===");
        for (Factura f : sistema.getFacturas()) {
            new ReciboFacturaPdf(f).imprimir();
        }
        System.out.println("PDFs generados correctamente.");
    }

    private int seleccionarIndice(String prompt, int size) {
        while (true) {
            try {
                System.out.print(prompt);
                int opcion = scanner.nextInt();
                scanner.nextLine();
                if (opcion >= 1 && opcion <= size) return opcion - 1;
                System.out.println("Opción fuera de rango. Debe ser entre 1 y " + size + ".");
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    private void imprimirEnConsola(Factura f) {
        System.out.println("=== FACTURA #" + f.getNumero() + " ===");
        System.out.println("Fecha: " + f.getFechaEmision());
        System.out.println("Cliente: " + f.getCliente().getNombre() + " (" + f.getCliente().getCategoria() + ")");
        System.out.println("Empleado: " + f.getEmpleado().getNombre() + " (" + f.getEmpleado().getPuesto() + ")");
        if (f.getEmpleado().getDepartamento() != null) {
            System.out.println("Departamento: " + f.getEmpleado().getDepartamento().getNombre());
        }

        System.out.println("\nITEMS:");
        for (Object it : f.getItems()) {
            if (it instanceof Producto p) {
                System.out.println("- " + p.getNombre() + ": $" + p.getPrecio());
            } else if (it instanceof Servicio s) {
                System.out.println("- " + s.getNombre() + ": $" + s.getPrecio());
            } else if (it instanceof ItemComercial ic) {
                System.out.println("- " + ic.getCodigo() + ": $" + ic.getPrecio());
            }
        }

        double subtotal = redondear2(f.calcularSubtotal());
        double iva      = redondear2(f.calcularImpuestos()); // 21%
        double extraPct = f.getImpuestoExtra();              // 0 si no aplica
        double impExtra = redondear2(subtotal * (extraPct / 100.0));
        double total    = redondear2(f.calcularTotal());
        double pagado   = (f.getPago() != null) ? redondear2(f.getPago().getMonto()) : 0.0;
        double saldo    = redondear2(total - pagado);
        if (saldo < 0) saldo = 0;

        System.out.println("Total (items): $" + String.format("%.2f", subtotal));
        System.out.println("Forma de pago: " + f.getFormaPago());
        System.out.println("Estado de pago: " + (f.getPago() != null ? f.getPago().getEstado() : "PENDIENTE"));
        System.out.println("=============================");
        System.out.println("Subtotal: $" + String.format("%.2f", subtotal));
        System.out.println("IVA (21%): $" + String.format("%.2f", iva));
        if (extraPct > 0) {
            System.out.println("Impuesto extra (" + String.format("%.2f", extraPct) + "%): $" + String.format("%.2f", impExtra));
        }
        System.out.println("Descuento aplicado: " + String.format("%.2f", f.getDescuento()) + "%");
        System.out.println("TOTAL: $" + String.format("%.2f", total));
        System.out.println("Monto pagado: $" + String.format("%.2f", pagado));
        System.out.println("Saldo: $" + String.format("%.2f", saldo));
        System.out.println();
    }

    private double redondear2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
