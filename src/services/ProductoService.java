package services;

import enums.TipoProductoServicio;
import model.Producto;
import model.Proveedor;
import model.SistemaGestion;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ProductoService {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    public ProductoService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }
    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE PRODUCTOS ---");
            System.out.println("1. Crear producto");
            System.out.println("2. Listar productos");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> crear();
                    case 2 -> listar();
                    case 0 -> { /* volver */ }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine(); // limpiar buffer
            }
        }
    }

    public void crear() {
        System.out.println("\n--- CREAR NUEVO PRODUCTO ---");

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
            scanner.nextLine(); // limpiar buffer

            System.out.println("Seleccione el tipo:");
            TipoProductoServicio[] tipos = TipoProductoServicio.values();
            for (int i = 0; i < tipos.length; i++) {
                System.out.println((i + 1) + ". " + tipos[i]);
            }
            System.out.print("Opción: ");
            int tipoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            TipoProductoServicio tipo = tipos[tipoIndex];

            System.out.println("Seleccione el proveedor:");
            for (int i = 0; i < sistema.getProveedores().size(); i++) {
                System.out.println((i + 1) + ". " + sistema.getProveedores().get(i).getNombre());
            }
            System.out.print("Opción: ");
            int proveedorIndex = scanner.nextInt() - 1; // pasa a índice 0-based
            scanner.nextLine();
            Proveedor proveedor = sistema.getProveedores().get(proveedorIndex);

            Producto nuevoProducto = new Producto(codigo, nombre, precio, tipo, proveedor);
            sistema.agregarProducto(nuevoProducto);
            System.out.println("Producto creado exitosamente.");
        } catch (InputMismatchException e) {
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine(); // limpiar buffer
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Opción fuera de rango.");
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        if (sistema.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (int i = 0; i < sistema.getProductos().size(); i++) {
            Producto p = sistema.getProductos().get(i);
            System.out.println((i + 1) + ". " + p.getNombre()
                    + " - Código: " + p.getCodigo()
                    + " - Precio: $" + p.getPrecio()
                    + " - Proveedor: " + p.getProveedor().getNombre());
        }
    }
}
