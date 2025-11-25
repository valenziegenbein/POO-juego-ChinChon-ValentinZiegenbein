package chinchon.modelo;

import java.util.*;

/**
 * Clase Mano - representa la mano de cartas de un jugador
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class Mano
{
    private List<Carta> cartas;
    
    public Mano() {
        cartas = new ArrayList<>();
    }
    
    /**
     * Añade una carta a la mano
     */
    public void agregarCarta(Carta carta) {
        cartas.add(carta);
        ordenar();
    }
    
    /**
     * Retira una carta de la mano
     */
    public boolean retirarCarta(Carta carta) {
        return cartas.remove(carta);
    }
    
    /**
     * Retira una carta por índice
     */
    public Carta retirarCarta(int indice) {
        if (indice >= 0 && indice < cartas.size()) {
            return cartas.remove(indice);
        }
        return null;
    }
    
    /**
     * Obtiene una carta por índice sin retirarla
     */
    public Carta obtenerCarta(int indice) {
        if (indice >= 0 && indice < cartas.size()) {
            return cartas.get(indice);
        }
        return null;
    }
    
    /**
     * Obtiene todas las cartas
     */
    public List<Carta> getCartas() {
        return new ArrayList<>(cartas);
    }
    
    /**
     * Obtiene el número de cartas en la mano
     */
    public int getNumeroCartas() {
        return cartas.size();
    }
    
    /**
     * Ordena las cartas por palo y número
     */
    public void ordenar() {
        Collections.sort(cartas);
    }
    
    /**
     * Verifica si la mano está vacía
     */
    public boolean estaVacia() {
        return cartas.isEmpty();
    }
    
    /**
     * Limpia todas las cartas
     */
    public void limpiar() {
        cartas.clear();
    }
    
    /**
     * Calcula los puntos de las cartas no ligadas
     */
    public int calcularPuntosNoLigadas(List<List<Carta>> gruposLigados) {
        List<Carta> cartasLigadas = new ArrayList<>();
        for (List<Carta> grupo : gruposLigados) {
            cartasLigadas.addAll(grupo);
        }
        
        int puntos = 0;
        for (Carta carta : cartas) {
            if (!cartasLigadas.contains(carta)) {
                puntos += carta.getValorPuntos();
            }
        }
        return puntos;
    }
    
    /**
     * Encuentra todos los grupos de cartas ligadas posibles
     * Retorna una lista de grupos, donde cada grupo es una lista de cartas ligadas
     */
    public List<List<Carta>> encontrarGruposLigados() {
        List<List<Carta>> grupos = new ArrayList<>();
        List<Carta> usadas = new ArrayList<>();
        
        // Buscar grupos del mismo número (al menos 3)
        Map<Integer, List<Carta>> porNumero = new HashMap<>();
        for (Carta carta : cartas) {
            if (!carta.esComodin()) {
                porNumero.putIfAbsent(carta.getNumero(), new ArrayList<>());
                porNumero.get(carta.getNumero()).add(carta);
            }
        }
        
        // Añadir comodines disponibles
        List<Carta> comodines = new ArrayList<>();
        for (Carta carta : cartas) {
            if (carta.esComodin()) {
                comodines.add(carta);
            }
        }
        
        // Formar grupos con el mismo número
        for (Map.Entry<Integer, List<Carta>> entry : porNumero.entrySet()) {
            List<Carta> grupo = new ArrayList<>(entry.getValue());
            if (grupo.size() >= 2 && grupo.size() + comodines.size() >= 3) {
                // Añadir comodines si es necesario (máximo 1 comodín por cada 2 cartas reales)
                int comodinesNecesarios = Math.max(0, 3 - grupo.size());
                comodinesNecesarios = Math.min(comodinesNecesarios, comodines.size());
                for (int i = 0; i < comodinesNecesarios && i < comodines.size(); i++) {
                    if (!usadas.contains(comodines.get(i))) {
                        grupo.add(comodines.get(i));
                        usadas.add(comodines.get(i));
                    }
                }
                if (grupo.size() >= 3) {
                    grupos.add(grupo);
                }
            }
        }
        
        // Buscar escaleras del mismo palo
        Map<Palo, List<Carta>> porPalo = new HashMap<>();
        for (Carta carta : cartas) {
            if (!carta.esComodin()) {
                porPalo.putIfAbsent(carta.getPalo(), new ArrayList<>());
                porPalo.get(carta.getPalo()).add(carta);
            }
        }
        
        // Para cada palo, buscar escaleras
        for (Map.Entry<Palo, List<Carta>> entry : porPalo.entrySet()) {
            List<Carta> cartasDelPalo = entry.getValue();
            Collections.sort(cartasDelPalo, Comparator.comparingInt(Carta::getNumero));
            
            // Buscar escaleras consecutivas
            List<List<Carta>> escalerasDelPalo = buscarEscaleras(cartasDelPalo, comodines, usadas);
            grupos.addAll(escalerasDelPalo);
        }
        
        return grupos;
    }
    
    /**
     * Busca escaleras en una lista ordenada de cartas del mismo palo
     */
    private List<List<Carta>> buscarEscaleras(List<Carta> cartasDelPalo, List<Carta> comodines, List<Carta> usadas) {
        List<List<Carta>> escaleras = new ArrayList<>();
        
        // Buscar secuencias consecutivas de al menos 3 cartas
        for (int i = 0; i < cartasDelPalo.size(); i++) {
            List<Carta> escalera = new ArrayList<>();
            escalera.add(cartasDelPalo.get(i));
            int siguienteEsperado = cartasDelPalo.get(i).getNumero() + 1;
            
            for (int j = i + 1; j < cartasDelPalo.size(); j++) {
                Carta actual = cartasDelPalo.get(j);
                if (actual.getNumero() == siguienteEsperado) {
                    escalera.add(actual);
                    siguienteEsperado++;
                } else if (actual.getNumero() > siguienteEsperado) {
                    // Hay un hueco, intentar usar comodín
                    boolean comodinUsado = false;
                    for (Carta comodin : comodines) {
                        if (!usadas.contains(comodin)) {
                            escalera.add(comodin);
                            usadas.add(comodin);
                            comodinUsado = true;
                            siguienteEsperado++;
                            j--; // Revisar la misma carta otra vez
                            break;
                        }
                    }
                    if (!comodinUsado) {
                        break;
                    }
                }
            }
            
            // Usar comodines adicionales si es necesario para alcanzar 3 cartas
            while (escalera.size() < 3) {
                boolean comodinAñadido = false;
                for (Carta comodin : comodines) {
                    if (!usadas.contains(comodin)) {
                        escalera.add(comodin);
                        usadas.add(comodin);
                        comodinAñadido = true;
                        break;
                    }
                }
                if (!comodinAñadido) {
                    break;
                }
            }
            
            if (escalera.size() >= 3) {
                escaleras.add(escalera);
            }
        }
        
        return escaleras;
    }
    
    /**
     * Verifica Si se puede cerrar la mano
     * Retorna un objeto con información sobre si se puede cerrar y por qué
     */
    public ResultadoCierre verificarCierre() {
        // Primero verificar que hay exactamente 7 cartas
        if (cartas.size() != 7) {
            return new ResultadoCierre(false, null);
        }
        
        List<List<Carta>> gruposLigados = encontrarGruposLigados();
        
        // Verificar escalera de 7 cartas
        for (List<Carta> grupo : gruposLigados) {
            if (grupo.size() == 7 && esEscalera(grupo)) {
                int numComodines = contarComodines(grupo);
                // Verificar que todas las cartas están en el grupo
                if (contieneTodasLasCartas(grupo)) {
                    return new ResultadoCierre(true, TipoCierre.ESCALERA_7, numComodines);
                }
            }
        }
        
        // Verificar dos grupos: uno de 3 y otro de 4
        if (gruposLigados.size() >= 2) {
            for (List<Carta> grupo1 : gruposLigados) {
                for (List<Carta> grupo2 : gruposLigados) {
                    if (grupo1 != grupo2 && 
                        ((grupo1.size() == 3 && grupo2.size() == 4) ||
                         (grupo1.size() == 4 && grupo2.size() == 3))) {
                        // Verificar que las cartas de ambos grupos cubren todas las 7 cartas
                        List<Carta> cartasCombinadas = new ArrayList<>();
                        cartasCombinadas.addAll(grupo1);
                        cartasCombinadas.addAll(grupo2);
                        if (contieneTodasLasCartas(cartasCombinadas)) {
                            return new ResultadoCierre(true, TipoCierre.DOS_GRUPOS_3_4);
                        }
                    }
                }
            }
        }
        
        // Verificar dos grupos de 3 (o escalera de 6) + carta < 5
        if (gruposLigados.size() >= 2) {
            for (List<Carta> grupo1 : gruposLigados) {
                for (List<Carta> grupo2 : gruposLigados) {
                    if (grupo1 != grupo2 && grupo1.size() >= 3 && grupo2.size() >= 3) {
                        List<Carta> cartasLigadas = new ArrayList<>();
                        cartasLigadas.addAll(grupo1);
                        cartasLigadas.addAll(grupo2);
                        
                        // Buscar una carta suelta menor que 5
                        for (Carta carta : cartas) {
                            if (!cartasLigadas.contains(carta) && !carta.esComodin() && carta.getNumero() < 5) {
                                // Verificar que las cartas ligadas + la carta suelta = 7 cartas
                                cartasLigadas.add(carta);
                                if (contieneTodasLasCartas(cartasLigadas)) {
                                    return new ResultadoCierre(true, TipoCierre.DOS_GRUPOS_3_MAS_CARTA_BAJA);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return new ResultadoCierre(false, null);
    }
    
    /**
     * Verifica si una lista de cartas contiene todas las cartas de la mano
     */
    private boolean contieneTodasLasCartas(List<Carta> listaCartas) {
        if (listaCartas.size() != cartas.size()) {
            return false;
        }
        for (Carta carta : cartas) {
            if (!listaCartas.contains(carta)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean esEscalera(List<Carta> grupo) {
        // Verificar si todas las cartas son del mismo palo o tienen comodines
        Palo paloBase = null;
        for (Carta carta : grupo) {
            if (!carta.esComodin()) {
                if (paloBase == null) {
                    paloBase = carta.getPalo();
                } else if (carta.getPalo() != paloBase) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private int contarComodines(List<Carta> grupo) {
        int count = 0;
        for (Carta carta : grupo) {
            if (carta.esComodin()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Clase auxiliar para resultado de verificación de cierre
     * responde a 3 preguntas:
     * ¿Puedo cerrar? (Sí/No - boolean).
     * ¿Qué tipo de juego hice? (TipoCierre).
     * ¿cuántos comodines usé? (int - importante para restar puntos extra como -25 o -50)
     */
    public static class ResultadoCierre {
        private boolean puedeCerrar;
        private TipoCierre tipo;
        private int numComodines;
        
        public ResultadoCierre(boolean puedeCerrar, TipoCierre tipo) {
            this.puedeCerrar = puedeCerrar;
            this.tipo = tipo;
            this.numComodines = 0;
        }
        
        public ResultadoCierre(boolean puedeCerrar, TipoCierre tipo, int numComodines) {
            this.puedeCerrar = puedeCerrar;
            this.tipo = tipo;
            this.numComodines = numComodines;
        }
        
        public boolean puedeCerrar() {
            return puedeCerrar;
        }
        
        public TipoCierre getTipo() {
            return tipo;
        }
        
        public int getNumComodines() {
            return numComodines;
        }
    }
    
    /* Tipos de cierre */
    public enum TipoCierre {
        ESCALERA_7,
        DOS_GRUPOS_3_4,
        DOS_GRUPOS_3_MAS_CARTA_BAJA
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cartas.size(); i++) {
            sb.append(i).append(": ").append(cartas.get(i));
            if (i < cartas.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}

