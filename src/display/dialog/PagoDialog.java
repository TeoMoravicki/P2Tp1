package display.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import model.SistemaGestion;
import model.Factura;
import model.Pago;
import enums.EstadoPago;
import enums.FormaPago;

public class PagoDialog extends JDialog {
    private SistemaGestion sistema;
    private Factura factura;
    private boolean guardado = false;

    private JLabel lblNumeroFactura;
    private JLabel lblCliente;
    private JLabel lblTotalFactura;
    private JLabel lblMontoYaPagado;
    private JLabel lblSaldoPendiente;
    private JTextField txtMontoAdicional;
    private JComboBox<EstadoPago> cmbEstadoPago;
    private JComboBox<FormaPago> cmbFormaPago;
    private JSpinner spnFechaPago;

    public PagoDialog(Frame owner, SistemaGestion sistema, Factura factura) {
        super(owner, "Gestionar Pago - Factura #" + factura.getNumero(), true);
        this.sistema = sistema;
        this.factura = factura;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(500, 450);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createInfoPanel(), BorderLayout.NORTH);
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        cargarDatosFactura();
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Información de la Factura"));

        lblNumeroFactura = new JLabel();
        lblCliente = new JLabel();
        lblTotalFactura = new JLabel();
        lblMontoYaPagado = new JLabel();
        lblSaldoPendiente = new JLabel();

        // Estilo para los labels de información
        Font fontInfo = new Font("Arial", Font.BOLD, 12);
        lblNumeroFactura.setFont(fontInfo);
        lblCliente.setFont(fontInfo);
        lblTotalFactura.setFont(fontInfo);
        lblMontoYaPagado.setFont(fontInfo);
        lblSaldoPendiente.setFont(fontInfo);

        panel.add(new JLabel("Número Factura:"));
        panel.add(lblNumeroFactura);
        panel.add(new JLabel("Cliente:"));
        panel.add(lblCliente);
        panel.add(new JLabel("Total Factura:"));
        panel.add(lblTotalFactura);
        panel.add(new JLabel("Monto Ya Pagado:"));
        panel.add(lblMontoYaPagado);
        panel.add(new JLabel("Saldo Pendiente:"));
        panel.add(lblSaldoPendiente);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo Pago"));

        txtMontoAdicional = new JTextField();
        cmbEstadoPago = new JComboBox<>(EstadoPago.values());
        cmbFormaPago = new JComboBox<>(FormaPago.values());
        spnFechaPago = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));

        // Configurar el formato de fecha en el spinner
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnFechaPago, "dd/MM/yyyy");
        spnFechaPago.setEditor(dateEditor);

        // Listener para actualizar estado automáticamente según el monto adicional
        txtMontoAdicional.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoAutomatico(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoAutomatico(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoAutomatico(); }
        });

        panel.add(new JLabel("Monto Adicional:*"));
        panel.add(txtMontoAdicional);
        panel.add(new JLabel("Nuevo Estado:*"));
        panel.add(cmbEstadoPago);
        panel.add(new JLabel("Forma de Pago:*"));
        panel.add(cmbFormaPago);
        panel.add(new JLabel("Fecha Pago:*"));
        panel.add(spnFechaPago);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnGuardar = crearBoton(" Guardar Pago", Color.decode("#2E8B57"));
        JButton btnCancelar = crearBoton(" Cancelar", Color.decode("#DC143C"));
        JButton btnPagarSaldo = crearBoton(" Pagar Saldo Restante", Color.decode("#4CAF50"));
        JButton btnLimpiarPago = crearBoton(" Limpiar Pago", Color.decode("#FF9800"));

        btnGuardar.addActionListener(e -> guardarPago());
        btnCancelar.addActionListener(e -> dispose());
        btnPagarSaldo.addActionListener(e -> pagarSaldoRestante());
        btnLimpiarPago.addActionListener(e -> limpiarPago());

        panel.add(btnPagarSaldo);
        panel.add(btnLimpiarPago);
        panel.add(btnGuardar);
        panel.add(btnCancelar);

        return panel;
    }

    private void cargarDatosFactura() {
        double total = factura.calcularTotal();
        double montoYaPagado = 0;

        // Cargar información de la factura
        lblNumeroFactura.setText(factura.getNumero());
        lblCliente.setText(factura.getCliente().getNombre());
        lblTotalFactura.setText(String.format("$%,.2f", total));

        // Cargar datos del pago existente si existe
        if (factura.getPago() != null) {
            Pago pago = factura.getPago();
            montoYaPagado = pago.getMonto();
            cmbEstadoPago.setSelectedItem(pago.getEstado());
            cmbFormaPago.setSelectedItem(pago.getFormaPago());
            spnFechaPago.setValue(pago.getFecha());
        } else {
            // Si no hay pago, establecer valores por defecto
            cmbEstadoPago.setSelectedItem(EstadoPago.PENDIENTE);
            cmbFormaPago.setSelectedItem(factura.getFormaPago());
            // No establecer montoYaPagado - ya es 0 por defecto
        }

        double saldoPendiente = total - montoYaPagado;
        lblMontoYaPagado.setText(String.format("$%,.2f", montoYaPagado));
        lblSaldoPendiente.setText(String.format("$%,.2f", saldoPendiente));

        // Establecer monto adicional por defecto como el saldo pendiente
        txtMontoAdicional.setText(String.format("%.2f", saldoPendiente));

        // Resaltar según el estado
        actualizarColoresEstado(montoYaPagado, total, saldoPendiente);
    }

    private void actualizarColoresEstado(double montoYaPagado, double total, double saldoPendiente) {
        if (montoYaPagado >= total) {
            lblMontoYaPagado.setForeground(Color.blue);
            lblSaldoPendiente.setForeground(Color.MAGENTA);
        } else if (montoYaPagado > 0) {
            lblMontoYaPagado.setForeground(Color.ORANGE);
            lblSaldoPendiente.setForeground(Color.RED);
        } else {
            lblMontoYaPagado.setForeground(Color.RED);
            lblSaldoPendiente.setForeground(Color.RED);
        }
    }

    private void pagarSaldoRestante() {
        try {
            double saldoPendiente = Double.parseDouble(lblSaldoPendiente.getText().replace("$", "").replace(",", ""));
            txtMontoAdicional.setText(String.format("%.2f", saldoPendiente));
            cmbEstadoPago.setSelectedItem(EstadoPago.PAGADO);
        } catch (NumberFormatException e) {
            mostrarError("Error al calcular el saldo pendiente");
        }
    }

    private void limpiarPago() {
        txtMontoAdicional.setText("0.00");
        cmbEstadoPago.setSelectedItem(EstadoPago.PENDIENTE);
    }

    private void actualizarEstadoAutomatico() {
        try {
            double montoAdicional = Double.parseDouble(txtMontoAdicional.getText().trim());
            double montoYaPagado = factura.getPago() != null ? factura.getPago().getMonto() : 0;
            double montoTotal = montoYaPagado + montoAdicional;
            double totalFactura = factura.calcularTotal();

            if (montoAdicional <= 0) {
                cmbEstadoPago.setSelectedItem(EstadoPago.PENDIENTE);
            } else if (montoTotal >= totalFactura) {
                cmbEstadoPago.setSelectedItem(EstadoPago.PAGADO);
            } else {
                cmbEstadoPago.setSelectedItem(EstadoPago.PARCIAL);
            }
        } catch (NumberFormatException e) {
            // Ignorar errores de formato mientras se escribe
        }
    }

    private void guardarPago() {
        if (validarFormulario()) {
            try {
                double montoAdicional = Double.parseDouble(txtMontoAdicional.getText().trim());
                double montoYaPagado = factura.getPago() != null ? factura.getPago().getMonto() : 0;
                double montoTotal = montoYaPagado + montoAdicional;

                EstadoPago estado = (EstadoPago) cmbEstadoPago.getSelectedItem();
                FormaPago formaPago = (FormaPago) cmbFormaPago.getSelectedItem();
                Date fechaPago = (Date) spnFechaPago.getValue();

                // Validar consistencia del estado con el monto total
                double totalFactura = factura.calcularTotal();
                if (estado == EstadoPago.PAGADO && montoTotal < totalFactura) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "El monto total pagado ($" + String.format("%,.2f", montoTotal) + ") " +
                                    "es menor al total de la factura ($" + String.format("%,.2f", totalFactura) + ").\n" +
                                    "¿Desea cambiar el estado a PARCIAL?",
                            "Estado Inconsistente",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        estado = EstadoPago.PARCIAL;
                        cmbEstadoPago.setSelectedItem(estado);
                    }
                }

                Pago pago = factura.getPago();

                if (pago == null) {
                    // Crear NUEVO pago - primera vez que se paga
                    pago = new Pago(montoTotal, fechaPago, formaPago, estado);
                    factura.setPago(pago);
                } else {
                    // Actualizar pago existente con el monto acumulado
                    pago.setMonto(montoTotal);
                    pago.setEstado(estado);
                    pago.setFormaPago(formaPago);
                    pago.setFecha(fechaPago);
                }

                guardado = true;
                dispose();

            } catch (NumberFormatException e) {
                mostrarError("El monto adicional debe ser un número válido");
            }
        }
    }
    private boolean validarFormulario() {
        // Validar monto adicional
        if (txtMontoAdicional.getText().trim().isEmpty()) {
            mostrarError("El monto adicional es obligatorio");
            return false;
        }

        try {
            double montoAdicional = Double.parseDouble(txtMontoAdicional.getText().trim());
            double montoYaPagado = factura.getPago() != null ? factura.getPago().getMonto() : 0;
            double totalFactura = factura.calcularTotal();

            if (montoAdicional < 0) {
                mostrarError("El monto adicional no puede ser negativo");
                return false;
            }

            if (montoAdicional == 0 && montoYaPagado == 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "El monto adicional es $0.00 y no hay pagos previos.\n" +
                                "¿Desea establecer el estado como PENDIENTE?",
                        "Confirmar Estado Pendiente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return false;
                }
            }

            // Verificar que no se exceda el pago total (aunque permitimos override)
            double montoTotal = montoYaPagado + montoAdicional;
            if (montoTotal > totalFactura) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "El monto total pagado ($" + String.format("%,.2f", montoTotal) + ") " +
                                "es mayor al total de la factura ($" + String.format("%,.2f", totalFactura) + ").\n" +
                                "¿Desea continuar?",
                        "Confirmar Pago Excedente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return false;
                }
            }

        } catch (NumberFormatException e) {
            mostrarError("El monto adicional debe ser un número válido");
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
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