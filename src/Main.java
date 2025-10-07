import display.MainFrame;
import model.SistemaGestion;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configurar el look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear sistema de gestión
        SistemaGestion sistema = new SistemaGestion();

        // Ejecutar la GUI en el EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(sistema);
            mainFrame.setVisible(true);
        });
    }
}