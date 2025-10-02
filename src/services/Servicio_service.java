package services;

import enums.TipoProductoServicio;
import model.Proveedor;
import model.Servicio;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Servicio_service {
    private static Scanner scanner;
    public static void gestionarServicios() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE SERVICIOS ---");
            System.out.println("1. Crear servicio");
            System.out.println("2. Listar servicios");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearServicio();
                    break;
                case 2:
                    listarServicios();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearServicio() {
        System.out.println("\n--- CREAR NUEVO SERVICIO ---");

        if (sistema.getProveedores().isEmpty()) {
            System.out.println("No hay proveedores registrados. Debe crear un proveedor primero.");
            return;
        }
        try {
            System.out.print("Código: ");
            String codigo = scanner.nextLine();

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Precio: ");
            double precio = scanner.nextDouble();
            scanner.nextLine();

            // Seleccionar tipo
            System.out.println("Seleccione el tipo:");
            TipoProductoServicio[] tipos = TipoProductoServicio.values();
            for (int i = 0; i < tipos.length; i++) {
                System.out.println((i + 1) + ". " + tipos[i]);
            }
            System.out.print("Seleccione el número del tipo: ");
            int tipoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            TipoProductoServicio tipo = tipos[tipoIndex];

            // Seleccionar proveedor
            System.out.println("Seleccione el proveedor:");
            for (int i = 0; i < sistema.getProveedores().size(); i++) {
                System.out.println((i + 1) + ". " + sistema.getProveedores().get(i).getNombre());
            }
            System.out.print("Seleccione el número del proveedor: ");
            int proveedorIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            Proveedor proveedor = sistema.getProveedores().get(proveedorIndex);

            Servicio nuevoServicio = new Servicio(codigo, nombre, precio, tipo, proveedor);
            sistema.agregarServicio(nuevoServicio);
            System.out.println("Servicio creado exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine(); // Limpiar buffer
        }
    }

    private void listarServicios() {
        System.out.println("\n--- LISTA DE SERVICIOS ---");
        if (sistema.getServicios().isEmpty()) {
            System.out.println("No hay servicios registrados.");
            return;
        }

        for (int i = 0; i < sistema.getServicios().size(); i++) {
            Servicio s = sistema.getServicios().get(i);
            System.out.println((i + 1) + ". " + s.getNombre() +
                    " - Código: " + s.getCodigo() +
                    " - Precio: $" + s.getPrecio() +
                    " - Proveedor: " + s.getProveedor().getNombre());
        }
    }

}
