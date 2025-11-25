package chinchon.modelo;

/**
 * Clase Jugador - representa un jugador del juego
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public class Jugador
{
    private String nombre;
    private int puntos;
    private Mano mano;
    private boolean reenganchado;
    private boolean eliminado;
    
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.puntos = 0;
        this.mano = new Mano();
        this.reenganchado = false;
        this.eliminado = false;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getPuntos() {
        return puntos;
    }
    
    public void agregarPuntos(int puntos) {
        this.puntos += puntos;
    }
    
    public void restarPuntos(int puntos) {
        this.puntos -= puntos;
        if (this.puntos < 0) {
            this.puntos = 0;
        }
    }
    
    public Mano getMano() {
        return mano;
    }
    
    public boolean estaReenganchado() {
        return reenganchado;
    }
    
    public void setReenganchado(boolean reenganchado) {
        this.reenganchado = reenganchado;
    }
    
    public boolean estaEliminado() {
        return eliminado;
    }
    
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
    
    public void limpiarMano() {
        mano.limpiar();
    }
    
    @Override
    public String toString() {
        return nombre + " - Puntos: " + puntos + 
               (reenganchado ? " (REENGANCHADO)" : "") +
               (eliminado ? " (ELIMINADO)" : "");
    }
}

