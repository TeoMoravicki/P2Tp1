package display;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.SistemaGestion;
import model.Producto;
import model.Proveedor;
import enums.TipoProductoServicio;

public class ProductoPanel extends JPanel {
    private SistemaGestion sistema;
    private JTable productosTable;
    private DefaultTableModel tableModel;

    public ProductoPanel(SistemaGestion sistema) {
        this.sistema = sistema;
        initializePanel();
        cargarProductos();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Productos"));

        JButton btnAgregar = crearBoton("Agregar Producto", Color.decode("#2E8B57"));
        JButton btnEditar = crearBoton("Editar Producto", Color.decode("#1E90FF"));
        JButton btnEliminar = crearBoton("Eliminar Producto", Color.decode("#DC143C"));
        JButton btnActualizar = crearBoton("Actualizar", Color.decode("#696969"));

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnActualizar.addActionListener(e -> cargarProductos());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));

        String[] columnNames = {"Código", "Nombre", "Precio", "Tipo", "Proveedor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productosTable = new JTable(tableModel);
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.setRowHeight(30);
        productosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(productosTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Reemplaza agregarProducto()
    private void agregarProducto() {
        if (sistema.getProveedores().isEmpty()) {
            mostrarError("No hay proveedores registrados. Debe crear un proveedor primero.");
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        display.dialog.ProductoDialog dialog = new display.dialog.ProductoDialog(owner, sistema, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarProductos();
            mostrarExito("Producto creado exitosamente");
        }
    }

    private boolean validarYGuardarProducto(String codigo, String nombre, String precio,
                                            TipoProductoServicio tipo, Proveedor proveedor) {
        if (codigo == null || codigo.trim().isEmpty()) {
            mostrarError("El código es obligatorio");
            return false;
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        try {
            double precioVal = Double.parseDouble(precio);
            if (precioVal < 0) {
                mostrarError("El precio no puede ser negativo");
                return false;
            }

            Producto nuevoProducto = new Producto(codigo.trim(),
                    nombre.trim(),
                    precioVal,
                    tipo,
                    proveedor);
            sistema.agregarProducto(nuevoProducto);
            cargarProductos();
            mostrarExito("Producto creado exitosamente");
            return true;

        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número válido");
            return false;
        }
    }

    private void editarProducto() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un producto para editar");
            return;
        }

        String codigo = (String) tableModel.getValueAt(selectedRow, 0);
        Producto producto = buscarProductoPorCodigo(codigo);

        if (producto != null) {
            mostrarDialogoEdicion(producto);
        }
    }

    private void eliminarProducto() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un producto para eliminar");
            return;
        }

        String codigo = (String) tableModel.getValueAt(selectedRow, 0);
        String nombre = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto?\n\n" +
                        "Código: " + codigo + "\n" +
                        "Nombre: " + nombre,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getProductos().removeIf(producto -> producto.getCodigo().equals(codigo));
            cargarProductos();
            mostrarExito("Producto eliminado exitosamente");
        }
    }

    private void cargarProductos() {
        tableModel.setRowCount(0);

        for (Producto producto : sistema.getProductos()) {
            Object[] rowData = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    String.format("$%,.2f", producto.getPrecio()),
                    producto.getTipo().toString(),
                    producto.getProveedor().getNombre()
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            productosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            productosTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            productosTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            productosTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            productosTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            productosTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        }
    }

    private Producto buscarProductoPorCodigo(String codigo) {
        for (Producto producto : sistema.getProductos()) {
            if (producto.getCodigo().equals(codigo)) {
                return producto;
            }
        }
        return null;
    }

    // Reemplaza mostrarDialogoEdicion(producto)
    private void mostrarDialogoEdicion(Producto producto) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        display.dialog.ProductoDialog dialog = new display.dialog.ProductoDialog(owner, sistema, producto);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            // ya editamos el mismo objeto (in-place) por lo que solo refrescamos la tabla
            cargarProductos();
            mostrarExito("Producto actualizado exitosamente");
        }
    }


    // Métodos auxiliares (iguales que en ClientePanel)
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setFont(new Font("Arial", Font.BOLD, 12));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }
}