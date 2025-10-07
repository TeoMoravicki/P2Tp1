package display;

import javax.swing.*;
import java.awt.*;

import display.dialog.FacturaDialog;
import model.*;
import display.table.FacturaTableModel;
import service.FacturaPDFService;

public class FacturaPanel extends JPanel {
    private SistemaGestion sistema;
    private JTable facturasTable;
    private FacturaTableModel tableModel;

    public FacturaPanel(SistemaGestion sistema) {
        this.sistema = sistema;
        initializePanel();
        cargarFacturas();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Facturas"));

        JButton btnAgregar = crearBoton("Agregar Factura", Color.decode("#2E8B57"));
        JButton btnEditar = crearBoton("Editar Factura", Color.decode("#1E90FF"));
        JButton btnEliminar = crearBoton("Eliminar Factura", Color.decode("#DC143C"));
        JButton btnActualizar = crearBoton("Actualizar", Color.decode("#696969"));
        JButton btnVerDetalle = crearBoton("Ver Detalle", Color.decode("#FF8C00"));

        btnAgregar.addActionListener(e -> agregarFactura());
        btnEditar.addActionListener(e -> editarFactura());
        btnEliminar.addActionListener(e -> eliminarFactura());
        btnActualizar.addActionListener(e -> cargarFacturas());
        btnVerDetalle.addActionListener(e -> verDetalleFactura());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);
        panel.add(btnVerDetalle);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Facturas"));

        tableModel = new FacturaTableModel();
        facturasTable = new JTable(tableModel);
        facturasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        facturasTable.setRowHeight(25);
        facturasTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(facturasTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Métodos de acción principales
    public void agregarFactura() {
        if (sistema.getClientes().isEmpty() || sistema.getEmpleados().isEmpty() ||
                (sistema.getProductos().isEmpty() && sistema.getServicios().isEmpty())) {
            mostrarError("No hay suficientes datos para crear una factura.\nSe necesitan:\n• Al menos un cliente\n• Al menos un empleado\n• Al menos un producto o servicio");
            return;
        }

        FacturaDialog dialog = new FacturaDialog(this, sistema, null);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarFacturas();
            mostrarExito("Factura creada exitosamente");
        }
    }

    public void editarFactura() {
        int selectedRow = facturasTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione una factura para editar");
            return;
        }

        String numero = (String) tableModel.getValueAt(selectedRow, 0);
        Factura factura = buscarFacturaPorNumero(numero);

        if (factura != null) {
            FacturaDialog dialog = new FacturaDialog(this, sistema, factura);
            dialog.setVisible(true);

            if (dialog.isGuardado()) {
                cargarFacturas();
                mostrarExito("Factura actualizada exitosamente");
            }
        }
    }

    public void verDetalleFactura() {
        int selectedRow = facturasTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione una factura para ver el detalle");
            return;
        }

        String numero = (String) tableModel.getValueAt(selectedRow, 0);
        Factura factura = buscarFacturaPorNumero(numero);

        if (factura != null) {
            mostrarDetalleFactura(factura);
        }
    }

    public void eliminarFactura() {
        int selectedRow = facturasTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione una factura para eliminar");
            return;
        }

        String numero = (String) tableModel.getValueAt(selectedRow, 0);
        String cliente = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la factura?\n\n" +
                        "Número: " + numero + "\n" +
                        "Cliente: " + cliente,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getFacturas().removeIf(factura -> factura.getNumero().equals(numero));
            cargarFacturas();
            mostrarExito("Factura eliminada exitosamente");
        }
    }

    // Métodos de datos
    public void cargarFacturas() {
        tableModel.setRowCount(0);

        for (Factura factura : sistema.getFacturas()) {
            String estado = factura.getPago() != null ? factura.getPago().getEstado().toString() : "SIN PAGO";

            Object[] rowData = {
                    factura.getNumero(),
                    factura.getFechaEmision(),
                    factura.getCliente().getNombre(),
                    factura.getEmpleado().getNombre(),
                    factura.getFormaPago().toString(),
                    String.format("$%,.2f", factura.calcularSubtotal()),
                    String.format("$%,.2f", factura.calcularImpuestos()),
                    String.format("%.1f%%", factura.getDescuento()),
                    String.format("%.1f%%", factura.getImpuestoExtra()),
                    String.format("$%,.2f", factura.calcularTotal()),
                    estado
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            setupTableColumns();
        }
    }

    private void setupTableColumns() {
        facturasTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        facturasTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        facturasTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        facturasTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        facturasTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        facturasTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        facturasTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        facturasTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        facturasTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        facturasTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        facturasTable.getColumnModel().getColumn(9).setPreferredWidth(90);
        facturasTable.getColumnModel().getColumn(10).setPreferredWidth(80);
    }

    // Métodos de utilidad
    private Factura buscarFacturaPorNumero(String numero) {
        return sistema.getFacturas().stream()
                .filter(factura -> factura.getNumero().equals(numero))
                .findFirst()
                .orElse(null);
    }

    private void mostrarDetalleFactura(Factura factura) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("=== DETALLE DE FACTURA ===\n\n");
        detalle.append("Número: ").append(factura.getNumero()).append("\n");
        detalle.append("Fecha: ").append(factura.getFechaEmision()).append("\n");
        detalle.append("Cliente: ").append(factura.getCliente().getNombre()).append("\n");
        detalle.append("Empleado: ").append(factura.getEmpleado().getNombre()).append("\n");
        detalle.append("Forma de Pago: ").append(factura.getFormaPago()).append("\n\n");

        detalle.append("=== ITEMS ===\n");
        for (ItemComercial item : factura.getItems()) {
            detalle.append("• ").append(item.getNombre())
                    .append(" - $").append(String.format("%,.2f", item.getPrecio()))
                    .append(" (").append(item.getTipo()).append(")\n");
        }

        detalle.append("\n=== DESGLOSE ===\n");
        detalle.append("Subtotal: $").append(String.format("%,.2f", factura.calcularSubtotal())).append("\n");
        detalle.append("IVA (21%): $").append(String.format("%,.2f", factura.calcularImpuestos())).append("\n");
        detalle.append("Impuesto Extra (").append(factura.getImpuestoExtra()).append("%): $")
                .append(String.format("%,.2f", factura.calcularImpuestoExtra())).append("\n");
        detalle.append("Descuento (").append(factura.getDescuento()).append("%): -$")
                .append(String.format("%,.2f", factura.calcularSubtotal() * (factura.getDescuento() / 100))).append("\n");
        detalle.append("TOTAL: $").append(String.format("%,.2f", factura.calcularTotal())).append("\n");

        if (factura.getPago() != null) {
            detalle.append("\n=== PAGO ===\n");
            detalle.append("Estado: ").append(factura.getPago().getEstado()).append("\n");
            detalle.append("Monto Pagado: $").append(String.format("%,.2f", factura.getPago().getMonto())).append("\n");
            detalle.append("Fecha Pago: ").append(factura.getPago().getFecha()).append("\n");
        }

        JTextArea textArea = new JTextArea(detalle.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JButton btnImprimir = crearBoton("Imprimir Factura", Color.decode("#2E8B57"));
        btnImprimir.addActionListener(e -> imprimirFactura(factura));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnImprimir);
        panel.add(panelBotones, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel,
                "Detalle de Factura #" + factura.getNumero(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void imprimirFactura(Factura factura) {
        try {
            FacturaPDFService.imprimirFactura(factura);
            mostrarExito(FacturaPDFService.getMensajeExito());
        } catch (Exception e) {
            mostrarError("Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos auxiliares (públicos para que FacturaDialog los use)
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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