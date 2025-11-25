package chinchon.observador;

/**
 * Interfaz Observer - permite que una clase observe cambios en objetos Observable
 * 
 * @author Valentin Ziegenbein
 * @version 1.0
 */
public interface Observer {
    void actualizar(Observable sujeto);
}

