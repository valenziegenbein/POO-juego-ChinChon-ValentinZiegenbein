package chinchon.controlador;

import java.util.Scanner;
import chinchon.modelo.Juego;
import chinchon.modelo.Jugador;
import chinchon.modelo.Mano;

/**
 * Clase ControladorChinchon - maneja la interacción entre la vista y el modelo
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class ControladorChinchon
{
    private Juego juego;
    private Scanner scanner;
    
    public ControladorChinchon(Juego juego) {
        this.juego = juego;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Procesa la acción de tomar carta del mazo o pozo
     */
    public boolean tomarCarta(boolean delPozo) {
        if (delPozo) {
            return juego.tomarDelPozo();
        } else {
            return juego.tomarDelMazo();
        }
    }
    
    /**
     * Procesa la acción de descartar una carta
     */
    public boolean descartarCarta(int indice) {
        return juego.descartarCarta(indice);
    }
    
    /**
     * Procesa el intento de cerrar la mano
     */
    public boolean intentarCerrarMano() {
        Jugador jugador = juego.getJugadorActual();
        if (jugador == null || juego.esPrimeraVuelta()) {
            return false;
        }
        
        Mano.ResultadoCierre resultado = jugador.getMano().verificarCierre();
        if (resultado.puedeCerrar()) {
            // Simular descartar una carta primero (necesario para cerrar)
            // En realidad, el cierre se hace después de descartar
            return true;
        }
        return false;
    }
    
    /**
     * Procesa la acción de reenganchar
     */
    public boolean reenganchar() {
        return juego.reenganchar();
    }
    
    /**
     * Obtiene una entrada del usuario
     */
    public String obtenerEntrada() {
        return scanner.nextLine();
    }
    
    /**
     * Obtiene un número del usuario
     */
    public int obtenerNumero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Cierra el scanner
     */
    public void cerrar() {
        scanner.close();
    }
}

