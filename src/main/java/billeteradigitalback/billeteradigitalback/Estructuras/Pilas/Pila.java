package billeteradigitalback.billeteradigitalback.Estructuras.Pilas;

public class Pila<T> {

    private NodoPila<T> cima;
    private int tamaño;

    public Pila() {
        this.cima = null;
        this.tamaño = 0;
    }

    // PUSH -> insertar elemento
    public void push(T dato) {

        NodoPila<T> nuevo = new NodoPila<>(dato);

        nuevo.setSiguiente(cima);

        cima = nuevo;

        tamaño++;
    }

    // POP -> eliminar y retornar el elemento superior
    public T pop() {

        if (estaVacia()) {
            return null;
        }

        T dato = cima.getDato();

        cima = cima.getSiguiente();

        tamaño--;

        return dato;
    }

    // PEEK -> ver elemento superior sin eliminar
    public T peek() {

        if (estaVacia()) {
            return null;
        }

        return cima.getDato();
    }

    // Saber si está vacía
    public boolean estaVacia() {
        return cima == null;
    }

    // Tamaño de la pila
    public int tamaño() {
        return tamaño;
    }

    // Mostrar pila
    public void mostrar() {

        NodoPila<T> actual = cima;

        while (actual != null) {

            System.out.println(actual.getDato());

            actual = actual.getSiguiente();
        }
    }
}