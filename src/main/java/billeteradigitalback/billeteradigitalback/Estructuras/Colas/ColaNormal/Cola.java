package billeteradigitalback.billeteradigitalback.Estructuras.Colas.ColaNormal;

public class Cola<T> {

    private NodoCola<T> frente;
    private NodoCola<T> fin;
    private int tamaño;

    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamaño = 0;
    }

    // ENCOLAR -> agregar al final
    public void encolar(T dato) {

        NodoCola<T> nuevo = new NodoCola<>(dato);

        // Si está vacía
        if (estaVacia()) {

            frente = nuevo;
            fin = nuevo;

        } else {

            fin.setSiguiente(nuevo);
            fin = nuevo;
        }

        tamaño++;
    }

    // DESENCOLAR -> eliminar del frente
    public T desencolar() {

        if (estaVacia()) {
            return null;
        }

        T dato = frente.getDato();

        frente = frente.getSiguiente();

        // Si quedó vacía
        if (frente == null) {
            fin = null;
        }

        tamaño--;

        return dato;
    }

    // Ver el frente
    public T frente() {

        if (estaVacia()) {
            return null;
        }

        return frente.getDato();
    }

    // Saber si está vacía
    public boolean estaVacia() {
        return frente == null;
    }

    // Tamaño
    public int tamaño() {
        return tamaño;
    }

    // Mostrar cola
    public void mostrar() {

        NodoCola<T> actual = frente;

        while (actual != null) {

            System.out.println(actual.getDato());

            actual = actual.getSiguiente();
        }
    }
}