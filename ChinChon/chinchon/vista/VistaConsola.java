package chinchon.vista;

import java.util.List;
import chinchon.modelo.Juego;
import chinchon.modelo.Jugador;
import chinchon.modelo.Mano;
import chinchon.modelo.Carta;
import chinchon.controlador.ControladorChinchon;
import chinchon.observador.Observable;
import chinchon.observador.Observer;

/**
 * Clase VistaConsola - implementa la vista del juego en consola
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class VistaConsola implements Observer {
    
    private Juego modelo;
    private ControladorChinchon controlador;
    private boolean mostrarMensajes;
    
    public VistaConsola(Juego modelo, ControladorChinchon controlador) {
        this.modelo = modelo;
        this.controlador = controlador;
        modelo.agregarObservador(this);
        this.mostrarMensajes = true;
    }
    
    /**
     * Método llamado por el Modelo cada vez que cambia el estado
     */
    @Override
    public void actualizar(Observable sujeto) {
        if (mostrarMensajes) {
            mostrarEstadoDeJuego();
        }
    }
    
    /**
     * Muestra el estado actual del juego
     */
    public void mostrarEstadoDeJuego() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ESTADO DEL JUEGO");
        System.out.println("=".repeat(60));
        
        // Mostrar jugadores y puntos
        System.out.println("\nJUGADORES:");
        for (Jugador jugador : modelo.getJugadores()) {
            System.out.println("  " + jugador);
        }
        
        // Mostrar jugador actual
        Jugador jugadorActual = modelo.getJugadorActual();
        if (jugadorActual != null) {
            System.out.println("\n>>> TURNO DE: " + jugadorActual.getNombre() + " <<<");
        }
        
        // Mostrar carta del pozo
        if (modelo.getPozo() != null && !modelo.getPozo().estaVacio()) {
            System.out.println("\nCarta en el pozo: " + modelo.getPozo().verCartaSuperior());
        }
        
        // Mostrar mano del jugador actual
        if (jugadorActual != null) {
            System.out.println("\nTu mano:");
            Mano mano = jugadorActual.getMano();
            System.out.println("  " + mano);
            
            // Mostrar grupos ligados posibles
            List<List<Carta>> gruposLigados = mano.encontrarGruposLigados();
            if (!gruposLigados.isEmpty()) {
                System.out.println("\nGrupos ligados posibles:");
                for (int i = 0; i < gruposLigados.size(); i++) {
                    System.out.println("  Grupo " + (i + 1) + ": " + gruposLigados.get(i));
                }
            }
            
            // Mostrar si puede cerrar
            if (!modelo.esPrimeraVuelta()) {
                Mano.ResultadoCierre resultado = mano.verificarCierre();
                if (resultado.puedeCerrar()) {
                    System.out.println("\n*** ¡PUEDES CERRAR LA MANO! ***");
                    System.out.println("  Tipo: " + resultado.getTipo());
                }
            }
        }
        
        // Mostrar grupos en la mesa si la mano está cerrada
        if (modelo.isManoCerrada()) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("MANO CERRADA por: " + modelo.getJugadorQueCerro().getNombre());
            System.out.println("Grupos en la mesa:");
            List<List<Carta>> gruposEnMesa = modelo.getGruposEnMesa();
            for (int i = 0; i < gruposEnMesa.size(); i++) {
                System.out.println("  Grupo " + (i + 1) + ": " + gruposEnMesa.get(i));
            }
        }
        
        // Mostrar si la partida terminó
        if (modelo.isPartidaTerminada()) {
            System.out.println("\n" + "*".repeat(60));
            if (modelo.getGanador() != null) {
                System.out.println("¡¡¡ GANADOR: " + modelo.getGanador().getNombre() + " !!!");
            }
            System.out.println("*".repeat(60));
        }
        
        System.out.println("=".repeat(60));
    }
    
    /**
     * Inicia la interacción del usuario
     */
    public void iniciar() {
        System.out.println("¡BIENVENIDO AL CHIN-CHON!");
        System.out.println("=".repeat(60));
        
        while (!modelo.isPartidaTerminada()) {
            Jugador jugadorActual = modelo.getJugadorActual();
            if (jugadorActual == null || jugadorActual.estaEliminado()) {
                break;
            }
            
            mostrarEstadoDeJuego();
            
            if (modelo.isManoCerrada()) {
                // Procesar colocación de grupos después del cierre
                procesarDespuesDeCierre();
            } else {
                // Turno normal
                procesarTurnoNormal();
            }
        }
        
        mostrarEstadoDeJuego();
        System.out.println("\n¡Gracias por jugar!");
    }
    
    /**
     * Procesa un turno normal
     */
    private void procesarTurnoNormal() {
        System.out.println("\nOPCIONES:");
        System.out.println("  1. Tomar carta del mazo");
        System.out.println("  2. Tomar carta del pozo");
        System.out.print("\nElige una opción: ");
        
        int opcion = controlador.obtenerNumero();
        
        boolean cartaTomada = false;
        if (opcion == 1) {
            cartaTomada = controlador.tomarCarta(false);
        } else if (opcion == 2) {
            cartaTomada = controlador.tomarCarta(true);
        } else {
            System.out.println("Opción inválida.");
            return;
        }
        
        if (!cartaTomada) {
            System.out.println("No se pudo tomar la carta.");
            return;
        }
        
        // Mostrar estado actualizado
        mostrarEstadoDeJuego();
        
        // Descartar una carta
        System.out.println("\nDescartar carta (indica el índice): ");
        int indice = controlador.obtenerNumero();
        
        if (!controlador.descartarCarta(indice)) {
            System.out.println("No se pudo descartar la carta. Intenta de nuevo.");
            // Forzar nueva actualización
            mostrarEstadoDeJuego();
        }
    }
    
    /**
     * Procesa acciones después de cerrar la mano
     */
    private void procesarDespuesDeCierre() {
        Jugador jugadorActual = modelo.getJugadorActual();
        if (jugadorActual == null) {
            return;
        }
        
        System.out.println("\nLa mano está cerrada. Colocando tus grupos ligados...");
        modelo.colocarGruposEnMesa();
        
        // Esperar un momento para que el usuario vea los resultados
        System.out.println("\nPresiona Enter para continuar...");
        controlador.obtenerEntrada();
    }
    
    /**
     * Desactiva los mensajes automáticos (útil para control manual)
     */
    public void setMostrarMensajes(boolean mostrar) {
        this.mostrarMensajes = mostrar;
    }
}

