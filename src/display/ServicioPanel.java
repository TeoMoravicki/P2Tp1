package display;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.SistemaGestion;
import model.Servicio;
import model.Proveedor;
import enums.TipoProductoServicio;

public class ServicioPanel extends JPanel {
    private SistemaGestion sistema;
    private JTable serviciosTable;
    private DefaultTableModel tableModel;

    public ServicioPanel(SistemaGestion sistema) {
        this.sistema = sistema;
        initializePanel();
        cargarServicios();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Servicios"));

        JButton btnAgregar = crearBoton("Agregar Servicio", Color.decode("#2E8B57"));
        JButton btnEditar = crearBoton("Editar Servicio", Color.decode("#1E90FF"));
        JButton btnEliminar = crearBoton("Eliminar Servicio", Color.decode("#DC143C"));
        JButton btnActualizar = crearBoton("Actualizar", Color.decode("#696969"));

        btnAgregar.addActionListener(e -> agregarServicio());
        btnEditar.addActionListener(e -> editarServicio());
        btnEliminar.addActionListener(e -> eliminarServicio());
        btnActualizar.addActionListener(e -> cargarServicios());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Servicios"));

        String[] columnNames = {"Código", "Nombre", "Precio", "Tipo", "Proveedor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviciosTable = new JTable(tableModel);
        serviciosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serviciosTable.setRowHeight(30);
        serviciosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(serviciosTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Reemplazar agregarServicio()
    private void agregarServicio() {
        if (sistema.getProveedores().isEmpty()) {
            mostrarError("No hay proveedores registrados. Debe crear un proveedor primero.");
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        display.dialog.ServicioDialog dialog = new display.dialog.ServicioDialog(owner, sistema, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarServicios();
            mostrarExito("Servicio creado exitosamente");
        }
    }


    private boolean validarYGuardarServicio(String codigo, String nombre, String precio,
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

            Servicio nuevoServicio = new Servicio(codigo.trim(),
                    nombre.trim(),
                    precioVal,
                    tipo,
                    proveedor);
            sistema.agregarServicio(nuevoServicio);
            cargarServicios();
            mostrarExito("Servicio creado exitosamente");
            return true;

        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número válido");
            return false;
        }
    }

    private void editarServicio() {
        int selectedRow = serviciosTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un servicio para editar");
            return;
        }

        String codigo = (String) tableModel.getValueAt(selectedRow, 0);
        Servicio servicio = buscarServicioPorCodigo(codigo);

        if (servicio != null) {
            mostrarDialogoEdicion(servicio);
        }
    }

    private void eliminarServicio() {
        int selectedRow = serviciosTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un servicio para eliminar");
            return;
        }

        String codigo = (String) tableModel.getValueAt(selectedRow, 0);
        String nombre = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el servicio?\n\n" +
                        "Código: " + codigo + "\n" +
                        "Nombre: " + nombre,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getServicios().removeIf(servicio -> servicio.getCodigo().equals(codigo));
            cargarServicios();
            mostrarExito("Servicio eliminado exitosamente");
        }
    }

    private void cargarServicios() {
        tableModel.setRowCount(0);

        for (Servicio servicio : sistema.getServicios()) {
            Object[] rowData = {
                    servicio.getCodigo(),
                    servicio.getNombre(),
                    String.format("$%,.2f", servicio.getPrecio()),
                    servicio.getTipo().toString(),
                    servicio.getProveedor().getNombre()
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            serviciosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            serviciosTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            serviciosTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            serviciosTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            serviciosTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            serviciosTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        }
    }

    private Servicio buscarServicioPorCodigo(String codigo) {
        for (Servicio servicio : sistema.getServicios()) {
            if (servicio.getCodigo().equals(codigo)) {
                return servicio;
            }
        }
        return null;
    }

    // Reemplazar mostrarDialogoEdicion(Servicio servicio)
    private void mostrarDialogoEdicion(Servicio servicio) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        display.dialog.ServicioDialog dialog = new display.dialog.ServicioDialog(owner, sistema, servicio);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarServicios();
            mostrarExito("Servicio actualizado exitosamente");
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