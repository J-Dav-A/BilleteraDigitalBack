package billeteradigitalback.billeteradigitalback.Estructuras.Listas;

public class ListaEnlazada<T> {

    private NodoLista<T> cabeza;
    private int tamaño;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    // Agregar al final
    public void agregar(T dato) {

        NodoLista<T> nuevo = new NodoLista<>(dato);

        if (cabeza == null) {
            cabeza = nuevo;
        } else {

            NodoLista<T> actual = cabeza;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }

        tamaño++;
    }

    // Mostrar lista
    public void mostrar() {

        NodoLista<T> actual = cabeza;

        while (actual != null) {
            System.out.println(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    // Buscar elemento
    public boolean contiene(T dato) {

        NodoLista<T> actual = cabeza;

        while (actual != null) {

            if (actual.getDato().equals(dato)) {
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    // Eliminar elemento
    public void eliminar(T dato) {

        if (cabeza == null) {
            return;
        }

        // Si el primero es el que se elimina
        if (cabeza.getDato().equals(dato)) {
            cabeza = cabeza.getSiguiente();
            tamaño--;
            return;
        }

        NodoLista<T> actual = cabeza;

        while (actual.getSiguiente() != null &&
                !actual.getSiguiente().getDato().equals(dato)) {

            actual = actual.getSiguiente();
        }

        if (actual.getSiguiente() != null) {
            actual.setSiguiente(actual.getSiguiente().getSiguiente());
            tamaño--;
        }
    }

    // Obtener tamaño
    public int tamaño() {
        return tamaño;
    }

    // Saber si está vacía
    public boolean estaVacia() {
        return cabeza == null;
    }

    // Obtener cabeza
    public NodoLista<T> getCabeza() {
        return cabeza;
    }
}