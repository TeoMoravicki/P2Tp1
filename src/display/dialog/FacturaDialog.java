package display.dialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

import display.FacturaPanel;
import model.*;
import enums.FormaPago;
import enums.EstadoPago;

public class FacturaDialog extends JDialog {
    private SistemaGestion sistema;
    private Factura factura;
    private boolean guardado = false;
    private FacturaPanel parentPanel;

    private JTextField txtNumero;
    private JComboBox<Cliente> cmbCliente;
    private JComboBox<Empleado> cmbEmpleado;
    private JComboBox<FormaPago> cmbFormaPago;
    private JComboBox<ItemComercial> cmbItems;
    private JSpinner spnDescuento;
    private JSpinner spnImpuestoExtra;
    private DefaultTableModel itemsTableModel;
    private JTable itemsTable;
    private JLabel lblSubtotal, lblIVA, lblImpuestoExtra, lblDescuento, lblTotal;

    public FacturaDialog(FacturaPanel parentPanel, SistemaGestion sistema, Factura facturaExistente) {
        super((Frame) SwingUtilities.getWindowAncestor(parentPanel),
                facturaExistente == null ? "Agregar Factura" : "Editar Factura",
                true);
        this.parentPanel = parentPanel;
        this.sistema = sistema;
        this.factura = facturaExistente;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(800, 700);
        setLocationRelativeTo(parentPanel);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createFormPanel(), BorderLayout.NORTH);
        mainPanel.add(createItemsPanel(), BorderLayout.CENTER);
        mainPanel.add(createResumenPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        configurarRenderersComboBox();

        if (factura != null) {
            cargarDatosFactura();
        }

        actualizarResumen();
    }

    // Métodos de creación de UI (copiados de la clase original)
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));

        txtNumero = new JTextField();
        cmbCliente = new JComboBox<>();
        cmbEmpleado = new JComboBox<>();
        cmbFormaPago = new JComboBox<>(FormaPago.values());

        // Cargar combos
        for (Cliente cliente : sistema.getClientes()) {
            cmbCliente.addItem(cliente);
        }
        for (Empleado empleado : sistema.getEmpleados()) {
            cmbEmpleado.addItem(empleado);
        }

        panel.add(new JLabel("Número Factura:*"));
        panel.add(txtNumero);
        panel.add(new JLabel("Cliente:*"));
        panel.add(cmbCliente);
        panel.add(new JLabel("Empleado:*"));
        panel.add(cmbEmpleado);
        panel.add(new JLabel("Forma de Pago:*"));
        panel.add(cmbFormaPago);

        // Panel para descuento e impuesto extra
        JPanel impuestosPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        impuestosPanel.add(new JLabel("Descuento (%):"));
        spnDescuento = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5));
        impuestosPanel.add(spnDescuento);

        impuestosPanel.add(new JLabel("Impuesto Extra (%):"));
        spnImpuestoExtra = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5));
        impuestosPanel.add(spnImpuestoExtra);

        panel.add(new JLabel("Ajustes:"));
        panel.add(impuestosPanel);

        // Listeners para actualizar resumen cuando cambien los impuestos/descuentos
        spnDescuento.addChangeListener(e -> actualizarResumen());
        spnImpuestoExtra.addChangeListener(e -> actualizarResumen());

        return panel;
    }

    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Items de la Factura"));

        // Panel superior para agregar items
        JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cmbItems = new JComboBox<>();
        actualizarComboItems();

        JButton btnAgregarItem = crearBoton("Agregar Item", Color.decode("#2E8B57"));
        JButton btnQuitarItem = crearBoton("Quitar Item", Color.decode("#DC143C"));

        btnAgregarItem.addActionListener(e -> agregarItem());
        btnQuitarItem.addActionListener(e -> quitarItem());

        addItemPanel.add(new JLabel("Item:"));
        addItemPanel.add(cmbItems);
        addItemPanel.add(btnAgregarItem);
        addItemPanel.add(btnQuitarItem);

        // Tabla de items
        String[] columnNames = {"Tipo", "Código", "Nombre", "Precio"};
        itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemsTable = new JTable(itemsTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(itemsTable);

        panel.add(addItemPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createResumenPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Resumen de la Factura"));

        lblSubtotal = new JLabel("$0.00");
        lblIVA = new JLabel("$0.00");
        lblImpuestoExtra = new JLabel("$0.00");
        lblDescuento = new JLabel("$0.00");
        lblTotal = new JLabel("$0.00");

        // Estilo para los montos
        Font fontMonto = new Font("Arial", Font.BOLD, 12);
        lblSubtotal.setFont(fontMonto);
        lblIVA.setFont(fontMonto);
        lblImpuestoExtra.setFont(fontMonto);
        lblDescuento.setFont(fontMonto);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(Color.BLUE);

        panel.add(new JLabel("Subtotal:"));
        panel.add(lblSubtotal);
        panel.add(new JLabel("IVA (21%):"));
        panel.add(lblIVA);
        panel.add(new JLabel("Impuesto Extra:"));
        panel.add(lblImpuestoExtra);
        panel.add(new JLabel("Descuento:"));
        panel.add(lblDescuento);
        panel.add(new JLabel("TOTAL:"));
        panel.add(lblTotal);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnGuardar = crearBoton("Guardar Factura", Color.decode("#2E8B57"));
        JButton btnCancelar = crearBoton("Cancelar", Color.decode("#DC143C"));

        btnGuardar.addActionListener(e -> guardarFactura());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCancelar);

        return panel;
    }

    private void configurarRenderersComboBox() {
        // Configurar renderer para el combo de items
        cmbItems.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ItemComercial) {
                    ItemComercial item = (ItemComercial) value;
                    String tipo = item instanceof Producto ? "PRODUCTO" : "SERVICIO";
                    setText(String.format("%s - %s ($%,.2f)", tipo, item.getNombre(), item.getPrecio()));
                }
                return this;
            }
        });

        // Configurar renderer para el combo de clientes
        cmbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    Cliente cliente = (Cliente) value;
                    setText(String.format("%s - %s", cliente.getDni(), cliente.getNombre()));
                }
                return this;
            }
        });

        // Configurar renderer para el combo de empleados
        cmbEmpleado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Empleado) {
                    Empleado empleado = (Empleado) value;
                    setText(String.format("%s - %s (%s)", empleado.getDni(), empleado.getNombre(), empleado.getPuesto()));
                }
                return this;
            }
        });
    }

    // Resto de métodos (actualizarComboItems, agregarItem, quitarItem, actualizarResumen,
    // calcularSubtotal, cargarDatosFactura, guardarFactura) se mantienen igual
    // Solo cambian las referencias a parentPanel para los mensajes

    private void actualizarComboItems() {
        cmbItems.removeAllItems();

        for (Producto producto : sistema.getProductos()) {
            cmbItems.addItem(producto);
        }

        for (Servicio servicio : sistema.getServicios()) {
            cmbItems.addItem(servicio);
        }
    }

    private void agregarItem() {
        ItemComercial itemSeleccionado = (ItemComercial) cmbItems.getSelectedItem();
        if (itemSeleccionado != null) {
            Object[] rowData = {
                    itemSeleccionado instanceof Producto ? "PRODUCTO" : "SERVICIO",
                    itemSeleccionado.getCodigo(),
                    itemSeleccionado.getNombre(),
                    String.format("$%,.2f", itemSeleccionado.getPrecio())
            };
            itemsTableModel.addRow(rowData);
            actualizarResumen();
        }
    }

    private void quitarItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            itemsTableModel.removeRow(selectedRow);
            actualizarResumen();
        }
    }

    private void actualizarResumen() {
        double subtotal = calcularSubtotal();
        double iva = subtotal * 0.21;
        double descuentoPorcentaje = (Double) spnDescuento.getValue();
        double impuestoExtraPorcentaje = (Double) spnImpuestoExtra.getValue();

        double impuestoExtra = subtotal * (impuestoExtraPorcentaje / 100);
        double totalBruto = subtotal + iva + impuestoExtra;
        double descuento = totalBruto * (descuentoPorcentaje / 100);
        double total = totalBruto - descuento;

        lblSubtotal.setText(String.format("$%,.2f", subtotal));
        lblIVA.setText(String.format("$%,.2f", iva));
        lblImpuestoExtra.setText(String.format("$%,.2f", impuestoExtra));
        lblDescuento.setText(String.format("$%,.2f", descuento));
        lblTotal.setText(String.format("$%,.2f", total));
    }

    private double calcularSubtotal() {
        double subtotal = 0;
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            String precioStr = (String) itemsTableModel.getValueAt(i, 3);
            double precio = Double.parseDouble(precioStr.replace("$", "").replace(",", ""));
            subtotal += precio;
        }
        return subtotal;
    }

    private void cargarDatosFactura() {
        txtNumero.setText(factura.getNumero());
        txtNumero.setEditable(false);

        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).getDni().equals(factura.getCliente().getDni())) {
                cmbCliente.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cmbEmpleado.getItemCount(); i++) {
            if (cmbEmpleado.getItemAt(i).getDni().equals(factura.getEmpleado().getDni())) {
                cmbEmpleado.setSelectedIndex(i);
                break;
            }
        }

        cmbFormaPago.setSelectedItem(factura.getFormaPago());
        spnDescuento.setValue(factura.getDescuento());
        spnImpuestoExtra.setValue(factura.getImpuestoExtra());

        for (ItemComercial item : factura.getItems()) {
            Object[] rowData = {
                    item instanceof Producto ? "PRODUCTO" : "SERVICIO",
                    item.getCodigo(),
                    item.getNombre(),
                    String.format("$%,.2f", item.getPrecio())
            };
            itemsTableModel.addRow(rowData);
        }
    }

    private void guardarFactura() {
        if (txtNumero.getText().trim().isEmpty()) {
            parentPanel.mostrarError("El número de factura es obligatorio");
            return;
        }

        if (itemsTableModel.getRowCount() == 0) {
            parentPanel.mostrarError("Debe agregar al menos un item a la factura");
            return;
        }

        if (factura == null) {
            for (Factura f : sistema.getFacturas()) {
                if (f.getNumero().equals(txtNumero.getText().trim())) {
                    parentPanel.mostrarError("Ya existe una factura con el número: " + txtNumero.getText());
                    return;
                }
            }
        }

        try {
            Factura facturaGuardar;

            if (factura == null) {
                facturaGuardar = new Factura(
                        txtNumero.getText().trim(),
                        new Date(),
                        (Cliente) cmbCliente.getSelectedItem(),
                        (Empleado) cmbEmpleado.getSelectedItem(),
                        (FormaPago) cmbFormaPago.getSelectedItem()
                );
            } else {
                facturaGuardar = factura;
            }

            facturaGuardar.setDescuento((Double) spnDescuento.getValue());
            facturaGuardar.setImpuestoExtra((Double) spnImpuestoExtra.getValue());

            facturaGuardar.getItems().clear();
            for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                String codigo = (String) itemsTableModel.getValueAt(i, 1);
                ItemComercial item = null;

                for (Producto producto : sistema.getProductos()) {
                    if (producto.getCodigo().equals(codigo)) {
                        item = producto;
                        break;
                    }
                }
                if (item == null) {
                    for (Servicio servicio : sistema.getServicios()) {
                        if (servicio.getCodigo().equals(codigo)) {
                            item = servicio;
                            break;
                        }
                    }
                }

                if (item != null) {
                    facturaGuardar.agregarItem(item);
                }
            }

            if (facturaGuardar.getPago() == null) {
                Pago pago = new Pago(
                        facturaGuardar.calcularTotal(),
                        new Date(),
                        facturaGuardar.getFormaPago(),
                        EstadoPago.PENDIENTE
                );
                facturaGuardar.setPago(pago);
            } else {
                facturaGuardar.getPago().setMonto(facturaGuardar.calcularTotal());
            }

            if (factura == null) {
                sistema.agregarFactura(facturaGuardar);
            }

            guardado = true;
            dispose();

        } catch (Exception e) {
            parentPanel.mostrarError("Error al guardar la factura: " + e.getMessage());
        }
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

    public boolean isGuardado() {
        return guardado;
    }
}