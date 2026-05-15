package billeteradigitalback.billeteradigitalback.Estructuras.Colas.ColaPrioridad;

public class ColaPrioridad<T> {

    private NodoPrioridad<T> frente;
    private int tamaño;

    public ColaPrioridad() {
        this.frente = null;
        this.tamaño = 0;
    }

    // Insertar según prioridad
    public void encolar(T dato, int prioridad) {

        NodoPrioridad<T> nuevo = new NodoPrioridad<>(dato, prioridad);

        // Si está vacía
        if (frente == null) {

            frente = nuevo;

        }
        // Si tiene mayor prioridad que el primero
        else if (prioridad < frente.getPrioridad()) {

            nuevo.setSiguiente(frente);
            frente = nuevo;

        }
        else {

            NodoPrioridad<T> actual = frente;

            while (actual.getSiguiente() != null &&
                    actual.getSiguiente().getPrioridad() <= prioridad) {

                actual = actual.getSiguiente();
            }

            nuevo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevo);
        }

        tamaño++;
    }

    // Eliminar el primero
    public T desencolar() {

        if (estaVacia()) {
            return null;
        }

        T dato = frente.getDato();

        frente = frente.getSiguiente();

        tamaño--;

        return dato;
    }

    // Ver primero
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

        NodoPrioridad<T> actual = frente;

        while (actual != null) {

            System.out.println(
                    "Dato: " + actual.getDato() +
                            " | Prioridad: " + actual.getPrioridad()
            );

            actual = actual.getSiguiente();
        }
    }
}