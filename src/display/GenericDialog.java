package display;

import javax.swing.*;
import java.awt.*;
import model.SistemaGestion;

public abstract class GenericDialog extends JDialog {
    protected SistemaGestion sistema;
    protected boolean guardado = false;

    public GenericDialog(Frame owner, SistemaGestion sistema, String titulo) {
        super(owner, titulo, true);
        this.sistema = sistema;
        initializeDialog();
    }

    protected abstract void initializeDialog();
    protected abstract boolean validarFormulario();
    protected abstract void guardar();

    public boolean isGuardado() {
        return guardado;
    }

    // Métodos comunes para crear componentes de formulario
    protected JPanel crearCampoFormulario(String label, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }
}