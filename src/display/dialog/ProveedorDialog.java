package display.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import model.Proveedor;
import model.SistemaGestion;

public class ProveedorDialog extends JDialog {
    private SistemaGestion sistema;
    private Proveedor proveedor;
    private boolean guardado = false;

    private JTextField txtNombre;
    private JTextField txtDomicilio;
    private JTextField txtDNI;
    private JTextField txtTelefono;
    private JTextField txtRazonSocial;
    private JTextField txtNIF;

    // owner puede ser null; uso Frame owner para modalidad correcta
    public ProveedorDialog(Frame owner, SistemaGestion sistema, Proveedor proveedorExistente) {
        super(owner, proveedorExistente == null ? "Agregar Proveedor" : "Editar Proveedor", true);
        this.sistema = sistema;
        this.proveedor = proveedorExistente;
        initDialog();
    }

    private void initDialog() {
        setLayout(new BorderLayout());
        setSize(450, 380);
        setLocationRelativeTo(getOwner());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtNombre = new JTextField(20);
        txtDomicilio = new JTextField(20);
        txtDNI = new JTextField(20);
        txtTelefono = new JTextField(20);
        txtRazonSocial = new JTextField(20);
        txtNIF = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nombre:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDomicilio, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("DNI:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDNI, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Razón Social:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtRazonSocial, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("NIF:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNIF, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = crearBoton("💾 Guardar", Color.decode("#2E8B57"));
        JButton btnCancelar = crearBoton("❌ Cancelar", Color.decode("#DC143C"));

        btnGuardar.addActionListener(e -> {
            if (validarFormulario()) {
                guardarProveedor();
            }
        });

        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (proveedor != null) {
            cargarProveedor();
        }
    }

    private void cargarProveedor() {
        txtNombre.setText(proveedor.getNombre());
        txtDomicilio.setText(proveedor.getDomicilio());
        txtDNI.setText(proveedor.getDni());
        txtDNI.setEditable(false); // no permitir cambiar DNI al editar
        txtTelefono.setText(proveedor.getTelefono());
        txtRazonSocial.setText(proveedor.getRazonSocial());
        txtNIF.setText(proveedor.getNif());
    }

    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String dni = txtDNI.getText().trim();
        String razon = txtRazonSocial.getText().trim();
        String nif = txtNIF.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }
        if (dni.isEmpty()) {
            mostrarError("El DNI es obligatorio");
            return false;
        }
        if (razon.isEmpty()) {
            mostrarError("La razón social es obligatoria");
            return false;
        }
        if (nif.isEmpty()) {
            mostrarError("El NIF es obligatorio");
            return false;
        }

        // Verificar DNI único (solo si estamos creando o si cambiara, pero en edición bloqueamos DNI)
        if (proveedor == null) {
            for (Proveedor p : sistema.getProveedores()) {
                if (Objects.equals(p.getDni(), dni)) {
                    mostrarError("Ya existe un proveedor con el DNI: " + dni);
                    return false;
                }
            }
        }

        return true;
    }

    private void guardarProveedor() {
        String nombre = txtNombre.getText().trim();
        String domicilio = txtDomicilio.getText().trim();
        String dni = txtDNI.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String razon = txtRazonSocial.getText().trim();
        String nif = txtNIF.getText().trim();

        Proveedor nuevo = new Proveedor(nombre, domicilio, dni, telefono, razon, nif);

        if (proveedor == null) {
            sistema.agregarProveedor(nuevo);
        } else {
            // reemplazar existente (buscar por DNI original)
            for (int i = 0; i < sistema.getProveedores().size(); i++) {
                Proveedor p = sistema.getProveedores().get(i);
                if (Objects.equals(p.getDni(), proveedor.getDni())) {
                    sistema.getProveedores().set(i, nuevo);
                    break;
                }
            }
        }

        guardado = true;
        dispose();
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { boton.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { boton.setBackground(color); }
        });
        return boton;
    }

    public boolean isGuardado() { return guardado; }
}
