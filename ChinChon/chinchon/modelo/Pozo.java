package chinchon.modelo;

import java.util.Stack;

/**
 * Clase Pozo - representa el pozo donde se descartan las cartas
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class Pozo
{
    private Stack<Carta> cartas;
    
    public Pozo() {
        cartas = new Stack<>();
    }
    
    /**
     * Añade una carta al pozo
     */
    public void agregarCarta(Carta carta) {
        cartas.push(carta);
    }
    
    /**
     * Toma la carta superior del pozo
     */
    public Carta tomarCarta() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.pop();
    }
    
    /**
     * Observa la carta superior sin tomarla
     */
    public Carta verCartaSuperior() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.peek();
    }
    
    /**
     * Verifica si el pozo está vacío
     */
    public boolean estaVacio() {
        return cartas.isEmpty();
    }
    
    /**
     * Obtiene el número de cartas en el pozo
     */
    public int getNumeroCartas() {
        return cartas.size();
    }
}

