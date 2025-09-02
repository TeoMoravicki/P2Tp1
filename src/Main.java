
public class Main {
    public static void main(String[] args) {
        SistemaGestion sistema = new SistemaGestion();
        MenuManager menu = new MenuManager(sistema);
        menu.mostrarMenuPrincipal();
    }
}