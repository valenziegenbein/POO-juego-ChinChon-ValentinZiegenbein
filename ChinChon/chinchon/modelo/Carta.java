package chinchon.modelo;

/**
 * Clase Carta - representa una carta de la baraja española
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class Carta implements Comparable<Carta>
{
    private Palo palo;
    private int numero; // 1-7 para números, 10-12 para figuras (sota, caballo, rey)
    
    // Constantes para identificar comodines
    public static final int COMODIN_NUMERO = 0;
    
    /**
     * Constructor para una carta normal
     */
    public Carta(Palo palo, int numero) {
        this.palo = palo;
        this.numero = numero;
    }
    
    /**
     * Constructor para comodín (sin palo)
     */
    public Carta(int numero) {
        this.palo = null;
        this.numero = numero;
    }
    
    public Palo getPalo() {
        return palo;
    }
    
    public int getNumero() {
        return numero;
    }
    
    public boolean esComodin() {
        return palo == null;
    }
    
    /**
     * Obtiene el valor de la carta para calcular puntos
     */
    public int getValorPuntos() {
        if (esComodin()) {
            return 20; // Los comodines valen 20 puntos
        }
        if (numero >= 10) {
            return 10; // Figuras valen 10 puntos
        }
        return numero; // Números valen su valor
    }
    
    /**
     * Compara dos cartas por palo y número
     */
    @Override
    public int compareTo(Carta otra) {
        if (this.esComodin() && otra.esComodin()) {
            return Integer.compare(this.numero, otra.numero);
        }
        if (this.esComodin()) {
            return 1;
        }
        if (otra.esComodin()) {
            return -1;
        }
        int comparacionPalo = this.palo.compareTo(otra.palo);
        if (comparacionPalo != 0) {
            return comparacionPalo;
        }
        return Integer.compare(this.numero, otra.numero);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Carta carta = (Carta) obj;
        if (esComodin() && carta.esComodin()) {
            return numero == carta.numero;
        }
        return numero == carta.numero && palo == carta.palo;
    }
    
    @Override
    public String toString() {
        if (esComodin()) {
            return "COMODIN";
        }
        String numeroStr;
        switch (numero) {
            case 1: numeroStr = "AS"; break;
            case 10: numeroStr = "SOTA"; break;
            case 11: numeroStr = "CABALLO"; break;
            case 12: numeroStr = "REY"; break;
            default: numeroStr = String.valueOf(numero);
        }
        return numeroStr + "_" + palo.toString();
    }
}

