package display.dialog;

import javax.swing.*;
import java.awt.*;
import model.SistemaGestion;
import model.Cliente;
import enums.CategoriaCliente;

public class ClienteDialog extends JDialog {
    private SistemaGestion sistema;
    private Cliente cliente;
    private boolean guardado = false;

    private JTextField txtNombre;
    private JTextField txtDomicilio;
    private JTextField txtDNI;
    private JTextField txtTelefono;
    private JTextField txtLimiteCredito;
    private JComboBox<CategoriaCliente> cmbCategoria;
    private JTextField txtEmail = new JTextField(20);


    public ClienteDialog(Frame owner, SistemaGestion sistema, Cliente clienteExistente) {
        super(owner, clienteExistente == null ? "Agregar Cliente" : "Editar Cliente", true);
        this.sistema = sistema;
        this.cliente = clienteExistente;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(450, 450);
        setLocationRelativeTo(getParent());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtNombre = new JTextField(20);
        txtDomicilio = new JTextField(20);
        txtDNI = new JTextField(20);
        txtTelefono = new JTextField(20);
        txtLimiteCredito = new JTextField(20);
        cmbCategoria = new JComboBox<>(CategoriaCliente.values());

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDomicilio, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("DNI:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDNI, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Límite Crédito:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtLimiteCredito, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Categoría:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbCategoria, gbc);

        gbc.gridx = 0; gbc.gridy = 6;  // Ajusta el número según tu layout
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnGuardar = crearBoton("Guardar", Color.decode("#2E8B57"));
        JButton btnCancelar = crearBoton("Cancelar", Color.decode("#DC143C"));

        btnGuardar.addActionListener(e -> guardarCliente());
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (cliente != null) {
            cargarDatosCliente();
        }
    }

    private void cargarDatosCliente() {
        txtNombre.setText(cliente.getNombre());
        txtDomicilio.setText(cliente.getDomicilio());
        txtDNI.setText(cliente.getDni());
        txtDNI.setEditable(false); // No se puede cambiar el DNI en edición
        txtTelefono.setText(cliente.getTelefono());
        txtLimiteCredito.setText(String.valueOf(cliente.getLimiteCredito()));
        cmbCategoria.setSelectedItem(cliente.getCategoria());
    }

    private void guardarCliente() {
        if (validarFormulario()) {
            try {
                if (cliente == null) {
                   Cliente clienteGuardar = new Cliente(
                            txtNombre.getText().trim(),
                            txtDomicilio.getText().trim(),
                            txtDNI.getText().trim(),
                            txtTelefono.getText().trim(),
                            Double.parseDouble(txtLimiteCredito.getText().trim()),
                            (CategoriaCliente) cmbCategoria.getSelectedItem(),
                            txtEmail.getText().trim()  // Nuevo campo
                    );
                    sistema.agregarCliente(clienteGuardar);
                } else {
                    // Edición in-place usando setters
                    cliente.setNombre(txtNombre.getText().trim());
                    cliente.setDomicilio(txtDomicilio.getText().trim());
                    // DNI no se modifica
                    cliente.setTelefono(txtTelefono.getText().trim());
                    cliente.setLimiteCredito(Double.parseDouble(txtLimiteCredito.getText().trim()));
                    cliente.setCategoria((CategoriaCliente) cmbCategoria.getSelectedItem());
                    cliente.setEmail(txtEmail.getText().trim());
                }

                guardado = true;
                dispose();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "El límite de crédito debe ser un número válido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        if (txtDNI.getText().trim().isEmpty()) {
            mostrarError("El DNI es obligatorio");
            return false;
        }

        // Verificar DNI único solo para nuevos clientes
        if (cliente == null) {
            for (Cliente c : sistema.getClientes()) {
                if (c.getDni().equals(txtDNI.getText().trim())) {
                    mostrarError("Ya existe un cliente con el DNI: " + txtDNI.getText());
                    return false;
                }
            }
        }

        try {
            double limite = Double.parseDouble(txtLimiteCredito.getText().trim());
            if (limite < 0) {
                mostrarError("El límite de crédito no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El límite de crédito debe ser un número válido");
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

    public boolean isGuardado() {
        return guardado;
    }
}
