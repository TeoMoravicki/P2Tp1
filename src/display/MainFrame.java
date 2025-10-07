package display;

import javax.swing.*;
import java.awt.*;
import model.SistemaGestion;

public class MainFrame extends JFrame {
    private SistemaGestion sistema;
    private JTabbedPane tabbedPane;

    public MainFrame(SistemaGestion sistema) {
        this.sistema = sistema;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sistema de Gestión Empresarial - GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Crear el panel con pestañas
        tabbedPane = new JTabbedPane();

        // Agregar las pestañas para cada módulo
        tabbedPane.addTab("🏠 Clientes", new ClientePanel(sistema));
        tabbedPane.addTab("👥 Empleados", new EmpleadoPanel(sistema));
        tabbedPane.addTab("🏢 Departamentos", new DepartamentoPanel(sistema));
        tabbedPane.addTab("📦 Proveedores", new ProveedorPanel(sistema));
        tabbedPane.addTab("📋 Productos", new ProductoPanel(sistema));
        tabbedPane.addTab("🔧 Servicios", new ServicioPanel(sistema));
        tabbedPane.addTab("🧾 Facturas", new FacturaPanel(sistema));

        add(tabbedPane);
    }
}