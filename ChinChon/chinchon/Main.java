package chinchon;

import chinchon.modelo.Juego;
import chinchon.controlador.ControladorChinchon;
import chinchon.vista.VistaConsola;

/**
 * Clase Main - punto de entrada del juego Chin-Chon
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Crear el juego con 4 jugadores (baraja de 40 cartas + 2 comodines)
        Juego juego = new Juego(4, true, 2);
        
        // Crear el controlador
        ControladorChinchon controlador = new ControladorChinchon(juego);
        
        // Crear la vista
        VistaConsola vista = new VistaConsola(juego, controlador);
        
        // Iniciar el juego
        vista.iniciar();
        
        // Cerrar el controlador
        controlador.cerrar();
    }
}
