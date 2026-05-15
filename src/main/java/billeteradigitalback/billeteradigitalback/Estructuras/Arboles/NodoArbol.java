package billeteradigitalback.billeteradigitalback.Estructuras.Arboles;

public class NodoArbol<T> {

    private int clave;
    private T dato;

    private NodoArbol<T> izquierdo;
    private NodoArbol<T> derecho;

    public NodoArbol(int clave, T dato) {
        this.clave = clave;
        this.dato = dato;
    }

    public int getClave() {
        return clave;
    }

    public void setClave(int clave) {
        this.clave = clave;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public NodoArbol<T> getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoArbol<T> izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoArbol<T> getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoArbol<T> derecho) {
        this.derecho = derecho;
    }
}