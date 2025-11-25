package chinchon.modelo;

import java.util.*;

/**
 * Clase Mazo - representa el mazo de cartas de la baraja española
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class Mazo
{
    private List<Carta> cartas;
    
    /**
     * Constructor que crea un mazo español estándar (40 cartas)
     */
    public Mazo() {
        this(false, 0); // Sin comodines por defecto
    }
    
    /**
     * Constructor que permite crear mazo con comodines
     * @param conComodines si incluir comodines
     * @param numComodines número de comodines a añadir
     */
    public Mazo(boolean conComodines, int numComodines) {
        cartas = new ArrayList<>();
        
        // Crear cartas normales (1-7, 10-12) para cada palo
        Palo[] palos = Palo.values();
        for (Palo palo : palos) {
            // Números del 1 al 7
            for (int i = 1; i <= 7; i++) {
                cartas.add(new Carta(palo, i));
            }
            // Figuras (10 = Sota, 11 = Caballo, 12 = Rey)
            for (int i = 10; i <= 12; i++) {
                cartas.add(new Carta(palo, i));
            }
        }
        
        // Añadir comodines si se solicitan
        if (conComodines) {
            for (int i = 1; i <= numComodines; i++) {
                cartas.add(new Carta(i)); // Comodines numerados
            }
        }
    }
    
    /**
     * Baraja el mazo
     */
    public void barajar() {
        Collections.shuffle(cartas);
    }
    
    /**
     * Reparte una carta del mazo
     */
    public Carta repartirCarta() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.remove(cartas.size() - 1);
    }
    
    /**
     * Obtiene el número de cartas restantes
     */
    public int getNumeroCartas() {
        return cartas.size();
    }
    
    /**
     * Verifica si el mazo está vacío
     */
    public boolean estaVacio() {
        return cartas.isEmpty();
    }
    
    /**
     * Obtiene todas las cartas (para debug)
     */
    public List<Carta> getCartas() {
        return new ArrayList<>(cartas); // Retorna copia
    }
}

