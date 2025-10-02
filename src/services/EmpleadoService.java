package services;

import enums.PuestoEmpleado;
import model.Departamento;
import model.Empleado;
import model.SistemaGestion;

import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EmpleadoService {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    public EmpleadoService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
            System.out.println("1. Crear empleado");
            System.out.println("2. Listar empleados");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> crear();
                    case 2 -> listar();
                    case 0 -> { }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    public void crear() {
        System.out.println("\n--- CREAR NUEVO EMPLEADO ---");
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Domicilio: ");
            String domicilio = scanner.nextLine();

            System.out.print("DNI: ");
            String dni = scanner.nextLine();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine();

            System.out.print("Salario: ");
            double salario = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Seleccione el puesto:");
            PuestoEmpleado[] puestos = PuestoEmpleado.values();
            for (int i = 0; i < puestos.length; i++) {
                System.out.println((i + 1) + ". " + puestos[i]);
            }
            System.out.print("Seleccione el número del puesto: ");
            int puestoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            PuestoEmpleado puesto = puestos[puestoIndex];

            Departamento departamento = null;
            if (!sistema.getDepartamentos().isEmpty()) {
                System.out.println("Seleccione el departamento:");
                for (int i = 0; i < sistema.getDepartamentos().size(); i++) {
                    System.out.println((i + 1) + ". " + sistema.getDepartamentos().get(i).getNombre());
                }
                System.out.print("Seleccione el número del departamento: ");
                int deptoIndex = scanner.nextInt() - 1;
                scanner.nextLine();
                departamento = sistema.getDepartamentos().get(deptoIndex);
            }

            Empleado nuevoEmpleado = new Empleado(nombre, domicilio, dni, telefono,
                    salario, puesto, new Date(), departamento);
            sistema.agregarEmpleado(nuevoEmpleado);
            System.out.println("Empleado creado exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine();
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE EMPLEADOS ---");
        if (sistema.getEmpleados().isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        for (int i = 0; i < sistema.getEmpleados().size(); i++) {
            Empleado e = sistema.getEmpleados().get(i);
            System.out.println((i + 1) + ". " + e.getNombre() +
                    " - DNI: " + e.getDni() +
                    " - Puesto: " + e.getPuesto() +
                    " - Departamento: " +
                    (e.getDepartamento() != null ? e.getDepartamento().getNombre() : "Sin asignar"));
        }
    }
}
