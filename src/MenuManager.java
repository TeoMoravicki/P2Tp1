import java.util.*;
import model.*;
import enums.*;
import services.*;


public class MenuManager {
    private SistemaGestion sistema;
    private Scanner scanner;

    public MenuManager(SistemaGestion sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenuPrincipal() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n=== SISTEMA DE GESTIÓN EMPRESARIAL ===");
            System.out.println("1. Gestionar Departamentos");
            System.out.println("2. Gestionar Empleados");
            System.out.println("3. Gestionar Proveedores");
            System.out.println("4. Gestionar Clientes");
            System.out.println("5. Gestionar Productos");
            System.out.println("6. Gestionar Servicios");
            System.out.println("7. Gestionar Facturas");
            System.out.println("8. Mostrar todas las facturas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        gestionarDepartamentos();
                        break;
                    case 2:
                        gestionarEmpleados();
                        break;
                    case 3:
                        gestionarProveedores();
                        break;
                    case 4:
                        gestionarClientes();
                        break;
                    case 5:
                        gestionarProductos();
                        break;
                    case 6:
                        Servicio_service.gestionarServicios();
                        break;
                    case 7:
                        gestionarFacturas();
                        break;
                    case 8:
                        mostrarTodasLasFacturas();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }

    // --------------------------------- Departamento---------------------------------
    private void gestionarDepartamentos() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE DEPARTAMENTOS ---");
            System.out.println("1. Crear departamento");
            System.out.println("2. Listar departamentos");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        crearDepartamento();
                        break;
                    case 2:
                        listarDepartamentos();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch(InputMismatchException e){
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }


    private void crearDepartamento() {
        System.out.println("\n--- CREAR NUEVO DEPARTAMENTO ---");
        System.out.print("Nombre: ");
        try {
            String nombre = scanner.nextLine();

            System.out.print("Presupuesto: ");
            double presupuesto = scanner.nextDouble();
            scanner.nextLine();

            // Si hay empleados, permitir seleccionar un responsable
            Empleado responsable = null;
            if (!sistema.getEmpleados().isEmpty()) {
                System.out.println("Seleccione un responsable:");
                for (int i = 0; i < sistema.getEmpleados().size(); i++) {
                    System.out.println((i + 1) + ". " + sistema.getEmpleados().get(i).getNombre());
                }
                System.out.print("Seleccione el número del responsable (0 para ninguno): ");
                int seleccion = scanner.nextInt();
                scanner.nextLine();

                if (seleccion > 0 && seleccion <= sistema.getEmpleados().size()) {
                    responsable = sistema.getEmpleados().get(seleccion - 1);
                }
            }

            Departamento nuevoDepartamento = new Departamento(nombre, presupuesto, responsable);
            sistema.agregarDepartamento(nuevoDepartamento);
            System.out.println("Departamento creado exitosamente.");

        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine(); // Limpiar buffer
        }
    }

    private void listarDepartamentos() {
        System.out.println("\n--- LISTA DE DEPARTAMENTOS ---");
        if (sistema.getDepartamentos().isEmpty()) {
            System.out.println("No hay departamentos registrados.");
            return;
        }

        for (int i = 0; i < sistema.getDepartamentos().size(); i++) {
            Departamento d = sistema.getDepartamentos().get(i);
            System.out.println((i + 1) + ". " + d.getNombre() +
                    " - Presupuesto: $" + d.getPresupuesto() +
                    " - Responsable: " +
                    (d.getResponsable() != null ? d.getResponsable().getNombre() : "Sin asignar"));
        }
    }
    // --------------------------------- Empleado ---------------------------------
    private void gestionarEmpleados() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
            System.out.println("1. Crear empleado");
            System.out.println("2. Listar empleados");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearEmpleado();
                    break;
                case 2:
                    listarEmpleados();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearEmpleado() {
        System.out.println("\n--- CREAR NUEVO EMPLEADO ---");
        System.out.print("Nombre: ");
        try {
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

        // Seleccionar puesto
        System.out.println("Seleccione el puesto:");
        PuestoEmpleado[] puestos = PuestoEmpleado.values();
        for (int i = 0; i < puestos.length; i++) {
            System.out.println((i + 1) + ". " + puestos[i]);
        }
        System.out.print("Seleccione el número del puesto: ");
        int puestoIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        PuestoEmpleado puesto = puestos[puestoIndex];

        // Seleccionar departamento
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
            scanner.nextLine(); // Limpiar buffer
        }
    }

    private void listarEmpleados() {
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

    // --------------------------------- Proveedor ---------------------------------
    private void gestionarProveedores() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE PROVEEDORES ---");
            System.out.println("1. Crear proveedor");
            System.out.println("2. Listar proveedores");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearProveedor();
                    break;
                case 2:
                    listarProveedores();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearProveedor() {
        System.out.println("\n--- CREAR NUEVO PROVEEDOR ---");
        System.out.print("Nombre: ");
        try {
            String nombre = scanner.nextLine();

            System.out.print("Domicilio: ");
            String domicilio = scanner.nextLine();

            System.out.print("DNI: ");
            String dni = scanner.nextLine();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine();

            System.out.print("Razón Social: ");
            String razonSocial = scanner.nextLine();

            System.out.print("NIF: ");
            String nif = scanner.nextLine();

            Proveedor nuevoProveedor = new Proveedor(nombre, domicilio, dni, telefono, razonSocial, nif);
            sistema.agregarProveedor(nuevoProveedor);
            System.out.println("Proveedor creado exitosamente.");
           } catch(InputMismatchException e){
        System.out.println("Por favor, ingrese algo válido.");
        scanner.nextLine(); // Limpiar buffer
        }
    }

    private void listarProveedores() {
        System.out.println("\n--- LISTA DE PROVEEDORES ---");
        if (sistema.getProveedores().isEmpty()) {
            System.out.println("No hay proveedores registrados.");
            return;
        }

        for (int i = 0; i < sistema.getProveedores().size(); i++) {
            Proveedor p = sistema.getProveedores().get(i);
            System.out.println((i + 1) + ". " + p.getNombre() +
                    " - Razón Social: " + p.getRazonSocial() +
                    " - NIF: " + p.getNif());
        }
    }

    // --------------------------------- CLiente ---------------------------------
    private void gestionarClientes() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Crear cliente");
            System.out.println("2. Listar clientes");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearCliente();
                    break;
                case 2:
                    listarClientes();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearCliente() {
        System.out.println("\n--- CREAR NUEVO CLIENTE ---");
        System.out.print("Nombre: ");
        try {
        String nombre = scanner.nextLine();

        System.out.print("Domicilio: ");
        String domicilio = scanner.nextLine();

        System.out.print("DNI: ");
        String dni = scanner.nextLine();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();

        System.out.print("Límite de Crédito: ");
        double limiteCredito = scanner.nextDouble();
        scanner.nextLine();

        // Seleccionar categoría
        System.out.println("Seleccione la categoría:");
        CategoriaCliente[] categorias = CategoriaCliente.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + ". " + categorias[i]);
        }
        System.out.print("Seleccione el número de la categoría: ");
        int categoriaIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        CategoriaCliente categoria = categorias[categoriaIndex];

        Cliente nuevoCliente = new Cliente(nombre, domicilio, dni, telefono, limiteCredito, categoria);
        sistema.agregarCliente(nuevoCliente);
        System.out.println("Cliente creado exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine(); // Limpiar buffer
        }
        }

    private void listarClientes() {
        System.out.println("\n--- LISTA DE CLIENTES ---");
        if (sistema.getClientes().isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        for (int i = 0; i < sistema.getClientes().size(); i++) {
            Cliente c = sistema.getClientes().get(i);
            System.out.println((i + 1) + ". " + c.getNombre() +
                    " - DNI: " + c.getDni() +
                    " - Categoría: " + c.getCategoria() +
                    " - Límite de Crédito: $" + c.getLimiteCredito());
        }
    }


    // --------------------------------- Producto ---------------------------------
    private void gestionarProductos() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE PRODUCTOS ---");
            System.out.println("1. Crear producto");
            System.out.println("2. Listar productos");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearProducto();
                    break;
                case 2:
                    listarProductos();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearProducto() {
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

        Producto nuevoProducto = new Producto(codigo, nombre, precio, tipo, proveedor);
        sistema.agregarProducto(nuevoProducto);
        System.out.println("Producto creado exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine(); // Limpiar buffer
        }
        }

    private void listarProductos() {
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        if (sistema.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (int i = 0; i < sistema.getProductos().size(); i++) {
            Producto p = sistema.getProductos().get(i);
            System.out.println((i + 1) + ". " + p.getNombre() +
                    " - Código: " + p.getCodigo() +
                    " - Precio: $" + p.getPrecio() +
                    " - Proveedor: " + p.getProveedor().getNombre());
        }
    }
    // --------------------------------- Servicio ---------------------------------

    // --------------------------------- Factura (que hambre que tengo) ---------------------------------
    private void gestionarFacturas() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE FACTURAS ---");
            System.out.println("1. Crear factura");
            System.out.println("2. Listar facturas");
            System.out.println("3. Mostrar detalle de factura");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearFactura();
                    break;
                case 2:
                    listarFacturas();
                    break;
                case 3:
                    mostrarDetalleFactura();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearFactura() {
        System.out.println("\n--- CREAR NUEVA FACTURA ---");

        if (sistema.getClientes().isEmpty() || sistema.getEmpleados().isEmpty() ||
                (sistema.getProductos().isEmpty() && sistema.getServicios().isEmpty())) {
            System.out.println("No hay suficientes datos para crear una factura.");
            System.out.println("Necesita al menos un cliente, un empleado y un producto o servicio.");
            return;
        }
        try {
        System.out.print("Número de factura: ");
        String numero = scanner.nextLine();

        // Seleccionar cliente
        System.out.println("Seleccione el cliente:");
        for (int i = 0; i < sistema.getClientes().size(); i++) {
            System.out.println((i + 1) + ". " + sistema.getClientes().get(i).getNombre());
        }
        System.out.print("Seleccione el número del cliente: ");
        int clienteIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        Cliente cliente = sistema.getClientes().get(clienteIndex);

        // Seleccionar empleado
        System.out.println("Seleccione el empleado:");
        for (int i = 0; i < sistema.getEmpleados().size(); i++) {
            System.out.println((i + 1) + ". " + sistema.getEmpleados().get(i).getNombre());
        }
        System.out.print("Seleccione el número del empleado: ");
        int empleadoIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        Empleado empleado = sistema.getEmpleados().get(empleadoIndex);

        // Seleccionar forma de pago
        System.out.println("Seleccione la forma de pago:");
        FormaPago[] formasPago = FormaPago.values();
        for (int i = 0; i < formasPago.length; i++) {
            System.out.println((i + 1) + ". " + formasPago[i]);
        }
        System.out.print("Seleccione el número de la forma de pago: ");
        int pagoIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        FormaPago formaPago = formasPago[pagoIndex];

        // Crear factura
        Factura nuevaFactura = new Factura(numero, new Date(), cliente, empleado, formaPago);

        // Agregar productos/servicios
        int opcionItem = -1;
        while (opcionItem != 0) {
            System.out.println("\n¿Qué desea agregar?");
            System.out.println("1. Producto");
            System.out.println("2. Servicio");
            System.out.println("0. Finalizar");
            System.out.print("Seleccione una opción: ");

            opcionItem = scanner.nextInt();
            scanner.nextLine();

            if (opcionItem == 1 && !sistema.getProductos().isEmpty()) {
                System.out.println("Seleccione el producto:");
                for (int i = 0; i < sistema.getProductos().size(); i++) {
                    Producto p = sistema.getProductos().get(i);
                    System.out.println((i + 1) + ". " + p.getNombre() + " - $" + p.getPrecio());
                }
                System.out.print("Seleccione el número del producto: ");
                int productoIndex = scanner.nextInt() - 1;
                scanner.nextLine();
                nuevaFactura.agregarItem(sistema.getProductos().get(productoIndex));
                System.out.println("Producto agregado.");
            } else if (opcionItem == 2 && !sistema.getServicios().isEmpty()) {
                System.out.println("Seleccione el servicio:");
                for (int i = 0; i < sistema.getServicios().size(); i++) {
                    Servicio s = sistema.getServicios().get(i);
                    System.out.println((i + 1) + ". " + s.getNombre() + " - $" + s.getPrecio());
                }
                System.out.print("Seleccione el número del servicio: ");
                int servicioIndex = scanner.nextInt() - 1;
                scanner.nextLine();
                nuevaFactura.agregarItem(sistema.getServicios().get(servicioIndex));
                System.out.println("Servicio agregado.");
            } else if (opcionItem != 0) {
                System.out.println("Opción no válida o no hay elementos disponibles.");
            }
        }

        // Crear pago
        System.out.println("Total a pagar: $" + nuevaFactura.calcularTotal());
        System.out.println("Seleccione el estado del pago:");
        EstadoPago[] estadosPago = EstadoPago.values();
        for (int i = 0; i < estadosPago.length; i++) {
            System.out.println((i + 1) + ". " + estadosPago[i]);
        }
        System.out.print("Seleccione el número del estado: ");
        int estadoIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        EstadoPago estado = estadosPago[estadoIndex];

        Pago pago = new Pago(nuevaFactura.calcularTotal(), new Date(), formaPago, estado);
        nuevaFactura.setPago(pago);

        sistema.agregarFactura(nuevaFactura);
        System.out.println("Factura creada exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine(); // Limpiar buffer
        }
        }

    private void listarFacturas() {
        System.out.println("\n--- LISTA DE FACTURAS ---");
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }

        for (int i = 0; i < sistema.getFacturas().size(); i++) {
            Factura f = sistema.getFacturas().get(i);
            System.out.println((i + 1) + ". Factura #" + f.getNumero() +
                    " - Cliente: " + f.getCliente().getNombre() +
                    " - Total: $" + f.calcularTotal() +
                    " - Estado: " + f.getPago().getEstado());
        }
    }

    private void mostrarDetalleFactura() {
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
        System.out.print("Seleccione el número de la factura: ");
        int facturaIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (facturaIndex >= 0 && facturaIndex < sistema.getFacturas().size()) {
            sistema.getFacturas().get(facturaIndex).mostrarDetalle();
        } else {
            System.out.println("Número de factura no válido.");
        }
    }

    private void mostrarTodasLasFacturas() {
        System.out.println("\n=== TODAS LAS FACTURAS ===");
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }

        for (Factura factura : sistema.getFacturas()) {
            factura.mostrarDetalle();
        }
    }
}