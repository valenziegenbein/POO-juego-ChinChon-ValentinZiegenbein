package chinchon.observador;

/**
 * Interfaz Observable - permite que una clase notifique cambios a sus observadores
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public interface Observable {
    void agregarObservador(Observer o);
    void quitarObservador(Observer o);
    void notificarObservadores();
}

