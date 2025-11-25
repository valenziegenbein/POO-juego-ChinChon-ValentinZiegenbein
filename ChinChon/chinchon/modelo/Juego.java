package chinchon.modelo;

import java.util.*;
import chinchon.observador.Observable;
import chinchon.observador.Observer;

/**
 * Clase Juego - representa el estado y lógica del juego Chin-Chon
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class Juego implements Observable {
    private List<Observer> observadores = new ArrayList<>();
    private Mazo mazo;
    private List<Jugador> jugadores;
    private Pozo pozo;
    private int jugadorActual;
    private int primeraVuelta; // Contador de vueltas (máximo = número de jugadores)
    private boolean manoCerrada;
    private Jugador jugadorQueCerro;
    private List<List<Carta>> gruposEnMesa; // Grupos ligados colocados en la mesa
    private boolean partidaTerminada;
    private Jugador ganador;
    
    public Juego(int numJugadores) {
        this(numJugadores, true, 2); // Por defecto 4 jugadores con 2 comodines
    }
    
    public Juego(int numJugadores, boolean conComodines, int numComodines) {
        jugadores = new ArrayList<>();
        for (int i = 1; i <= numJugadores; i++) {
            jugadores.add(new Jugador("Jugador " + i));
        }
        
        mazo = new Mazo(conComodines, numComodines);
        mazo.barajar();
        
        pozo = new Pozo();
        jugadorActual = 0;
        primeraVuelta = 0;
        manoCerrada = false;
        jugadorQueCerro = null;
        gruposEnMesa = new ArrayList<>();
        partidaTerminada = false;
        ganador = null;
        
        // Repartir 7 cartas a cada jugador
        repartirCartas();
        
        // Colocar la primera carta en el pozo
        if (!mazo.estaVacio()) {
            pozo.agregarCarta(mazo.repartirCarta());
        }
    }
    
    /**
     * Reparte 7 cartas a cada jugador
     */
    private void repartirCartas() {
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
            for (int i = 0; i < 7; i++) {
                Carta carta = mazo.repartirCarta();
                if (carta != null) {
                    jugador.getMano().agregarCarta(carta);
                }
            }
        }
    }
    
    /**
     * Obtiene el jugador actual
     */
    public Jugador getJugadorActual() {
        if (jugadorActual >= 0 && jugadorActual < jugadores.size()) {
            return jugadores.get(jugadorActual);
        }
        return null;
    }
    
    /**
     * Obtiene todos los jugadores
     */
    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
    }
    
    /**
     * Obtiene el pozo
     */
    public Pozo getPozo() {
        return pozo;
    }
    
    /**
     * Obtiene el mazo
     */
    public Mazo getMazo() {
        return mazo;
    }
    
    /**
     * Obtiene los grupos en la mesa
     */
    public List<List<Carta>> getGruposEnMesa() {
        return new ArrayList<>(gruposEnMesa);
    }
    
    /**
     * Verifica si la mano está cerrada
     */
    public boolean isManoCerrada() {
        return manoCerrada;
    }
    
    /**
     * Obtiene el jugador que cerró la mano
     */
    public Jugador getJugadorQueCerro() {
        return jugadorQueCerro;
    }
    
    /**
     * Verifica si es la primera vuelta
     */
    public boolean esPrimeraVuelta() {
        return primeraVuelta < jugadores.size();
    }
    
    /**
     * Verifica si la partida está terminada
     */
    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }
    
    /**
     * Obtiene el ganador
     */
    public Jugador getGanador() {
        return ganador;
    }
    
    /**
     * Toma una carta del mazo
     */
    public boolean tomarDelMazo() {
        if (manoCerrada || partidaTerminada) {
            return false;
        }
        
        Carta carta = mazo.repartirCarta();
        if (carta != null) {
            getJugadorActual().getMano().agregarCarta(carta);
            notificarObservadores();
            return true;
        }
        return false;
    }
    
    /**
     * Toma la carta superior del pozo
     */
    public boolean tomarDelPozo() {
        if (manoCerrada || partidaTerminada) {
            return false;
        }
        
        Carta carta = pozo.tomarCarta();
        if (carta != null) {
            getJugadorActual().getMano().agregarCarta(carta);
            notificarObservadores();
            return true;
        }
        return false;
    }
    
    /**
     * Descarta una carta al pozo
     */
    public boolean descartarCarta(int indiceCarta) {
        if (manoCerrada || partidaTerminada) {
            return false;
        }
        
        Jugador jugador = getJugadorActual();
        if (jugador == null) {
            return false;
        }
        
        Carta carta = jugador.getMano().retirarCarta(indiceCarta);
        if (carta != null) {
            pozo.agregarCarta(carta);
            
            // Verificar si se puede cerrar (después de descartar quedan 7 cartas)
            if (jugador.getMano().getNumeroCartas() == 7) {
                Mano.ResultadoCierre resultado = jugador.getMano().verificarCierre();
                if (resultado.puedeCerrar() && !esPrimeraVuelta()) {
                    cerrarMano(resultado);
                }
            }
            
            // Avanzar al siguiente jugador si no se cerró la mano
            if (!manoCerrada) {
                siguienteTurno();
            }
            
            notificarObservadores();
            return true;
        }
        return false;
    }
    
    /**
     * Cierra la mano
     */
    private void cerrarMano(Mano.ResultadoCierre resultado) {
        manoCerrada = true;
        jugadorQueCerro = getJugadorActual();
        
        // Colocar los grupos ligados en la mesa
        gruposEnMesa = jugadorQueCerro.getMano().encontrarGruposLigados();
        
        // Verificar si cerró con chinchón (escalera de 7 sin comodines)
        if (resultado.getTipo() == Mano.TipoCierre.ESCALERA_7 && resultado.getNumComodines() == 0) {
            // CHINCHÓN - gana la partida inmediatamente
            partidaTerminada = true;
            ganador = jugadorQueCerro;
        } else {
            // Calcular puntos y bonificaciones
            calcularPuntosYCerrarMano(resultado);
        }
        
        notificarObservadores();
    }
    
    /**
     * Calcula los puntos después de cerrar la mano
     */
    private void calcularPuntosYCerrarMano(Mano.ResultadoCierre resultado) {
        // Calcular puntos de todos los jugadores
        for (Jugador jugador : jugadores) {
            if (jugador == jugadorQueCerro) {
                // El que cierra puede tener bonus si cerró con 7 cartas ligadas
                int puntosNoLigadas = jugador.getMano().calcularPuntosNoLigadas(gruposEnMesa);
                if (puntosNoLigadas == 0 && resultado.getTipo() == Mano.TipoCierre.ESCALERA_7) {
                    // Cerró con escalera de 7, aplicar bonus
                    int bonus = 0;
                    if (resultado.getNumComodines() == 2) {
                        bonus = -25;
                    } else if (resultado.getNumComodines() == 1) {
                        bonus = -50;
                    }
                    jugador.restarPuntos(Math.abs(bonus)); // Restar puntos (aplicar bonus)
                } else if (puntosNoLigadas == 0 && resultado.getTipo() == Mano.TipoCierre.DOS_GRUPOS_3_4) {
                    // Dos grupos (3 y 4)
                    jugador.restarPuntos(10);
                } else {
                    jugador.agregarPuntos(puntosNoLigadas);
                }
            } else {
                // Otros jugadores pueden colocar cartas en los grupos de la mesa
                // Por ahora, calcular puntos de cartas no ligadas
                int puntosNoLigadas = jugador.getMano().calcularPuntosNoLigadas(gruposEnMesa);
                jugador.agregarPuntos(puntosNoLigadas);
            }
            
            // Verificar si superó 100 puntos
            if (jugador.getPuntos() > 100) {
                if (!jugador.estaReenganchado() && jugadoresActivos().size() > 2) {
                    // Puede reengancharse
                    // (la lógica de reenganche se manejará en el controlador)
                } else {
                    jugador.setEliminado(true);
                }
            }
        }
        
        // Verificar si todos los jugadores excepto uno fueron eliminados
        List<Jugador> activos = jugadoresActivos();
        if (activos.size() == 1) {
            partidaTerminada = true;
            ganador = activos.get(0);
        }
    }
    
    /**
     * Obtiene la lista de jugadores activos
     */
    public List<Jugador> jugadoresActivos() {
        List<Jugador> activos = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            if (!jugador.estaEliminado()) {
                activos.add(jugador);
            }
        }
        return activos;
    }
    
    /**
     * Avanza al siguiente turno
     */
    private void siguienteTurno() {
        if (manoCerrada) {
            return;
        }
        
        primeraVuelta++;
        jugadorActual = (jugadorActual + 1) % jugadores.size();
        
        // Si el jugador está eliminado, saltar su turno
        int intentos = 0;
        while (getJugadorActual().estaEliminado() && intentos < jugadores.size()) {
            jugadorActual = (jugadorActual + 1) % jugadores.size();
            intentos++;
        }
    }
    
    /**
     * Coloca grupos ligados en la mesa (después de que alguien cerró)
     */
    public boolean colocarGruposEnMesa() {
        if (!manoCerrada || jugadorQueCerro == null) {
            return false;
        }
        
        Jugador jugador = getJugadorActual();
        List<List<Carta>> gruposLigados = jugador.getMano().encontrarGruposLigados();
        
        // Si el que cerró tiene todas las cartas ligadas, no se pueden colocar más
        int puntosNoLigadasCerrador = jugadorQueCerro.getMano().calcularPuntosNoLigadas(gruposEnMesa);
        if (puntosNoLigadasCerrador == 0) {
            // No se pueden colocar cartas adicionales
            avanzarDespuesDeCierre();
            return false;
        }
        
        // Añadir los grupos nuevos a la mesa
        gruposEnMesa.addAll(gruposLigados);
        avanzarDespuesDeCierre();
        notificarObservadores();
        return true;
    }
    
    /**
     * Avanza al siguiente jugador después de cerrar la mano
     */
    private void avanzarDespuesDeCierre() {
        List<Jugador> activos = jugadoresActivos();
        if (activos.isEmpty()) {
            partidaTerminada = true;
            return;
        }
        
        // Buscar el índice del jugador actual en la lista de activos
        int indiceEnActivos = activos.indexOf(getJugadorActual());
        if (indiceEnActivos >= 0 && indiceEnActivos < activos.size() - 1) {
            // Hay más jugadores, pasar al siguiente
            Jugador siguiente = activos.get(indiceEnActivos + 1);
            jugadorActual = jugadores.indexOf(siguiente);
        } else {
            // Terminar la mano y empezar una nueva
            nuevaMano();
        }
    }
    
    /**
     * Inicia una nueva mano
     */
    private void nuevaMano() {
        manoCerrada = false;
        jugadorQueCerro = null;
        gruposEnMesa.clear();
        
        // Reiniciar turnos
        primeraVuelta = 0;
        jugadorActual = 0;
        
        // Limpiar mazo y pozo, crear nuevo mazo
        mazo = new Mazo(true, 2); // 2 comodines para 4 jugadores
        mazo.barajar();
        pozo = new Pozo();
        
        // Repartir nuevas cartas solo a jugadores activos
        for (Jugador jugador : jugadoresActivos()) {
            jugador.limpiarMano();
            for (int i = 0; i < 7; i++) {
                Carta carta = mazo.repartirCarta();
                if (carta != null) {
                    jugador.getMano().agregarCarta(carta);
                }
            }
        }
        
        // Colocar primera carta en el pozo
        if (!mazo.estaVacio()) {
            pozo.agregarCarta(mazo.repartirCarta());
        }
        
        notificarObservadores();
    }
    
    /**
     * Permite reenganchar a un jugador
     */
    public boolean reenganchar() {
        Jugador jugador = getJugadorActual();
        if (jugador.getPuntos() > 100 && !jugador.estaReenganchado() && 
            jugadoresActivos().size() > 2) {
            jugador.setReenganchado(true);
            // Obtener la máxima puntuación de los otros jugadores activos
            int maxPuntos = 0;
            for (Jugador otro : jugadores) {
                if (otro != jugador && !otro.estaEliminado()) {
                    maxPuntos = Math.max(maxPuntos, otro.getPuntos());
                }
            }
            jugador.restarPuntos(jugador.getPuntos() - maxPuntos);
            notificarObservadores();
            return true;
        }
        return false;
    }

    // Métodos de Observable
    @Override
    public void agregarObservador(Observer o) {
        observadores.add(o);
    }
    
    @Override
    public void quitarObservador(Observer o) {
        observadores.remove(o);
    }
    
    @Override
    public void notificarObservadores() {
        for (Observer o : observadores) {
            o.actualizar(this);
        }
    }
}

