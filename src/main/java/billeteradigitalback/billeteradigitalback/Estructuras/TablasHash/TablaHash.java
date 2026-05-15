package billeteradigitalback.billeteradigitalback.Estructuras.TablasHash;

import java.util.LinkedList;

public class TablaHash<K, V> {

    private LinkedList<EntradaHash<K, V>>[] tabla;
    private int capacidad;

    @SuppressWarnings("unchecked")
    public TablaHash(int capacidad) {

        this.capacidad = capacidad;

        tabla = new LinkedList[capacidad];

        for (int i = 0; i < capacidad; i++) {
            tabla[i] = new LinkedList<>();
        }
    }

    // Función hash
    private int obtenerIndice(K clave) {

        return Math.abs(clave.hashCode()) % capacidad;
    }

    // Insertar
    public void insertar(K clave, V valor) {

        int indice = obtenerIndice(clave);

        LinkedList<EntradaHash<K, V>> lista = tabla[indice];

        // Si la clave ya existe -> actualizar
        for (EntradaHash<K, V> entrada : lista) {

            if (entrada.getClave().equals(clave)) {

                entrada.setValor(valor);
                return;
            }
        }

        // Si no existe -> insertar
        lista.add(new EntradaHash<>(clave, valor));
    }

    // Buscar
    public V buscar(K clave) {

        int indice = obtenerIndice(clave);

        LinkedList<EntradaHash<K, V>> lista = tabla[indice];

        for (EntradaHash<K, V> entrada : lista) {

            if (entrada.getClave().equals(clave)) {

                return entrada.getValor();
            }
        }

        return null;
    }

    // Eliminar
    public void eliminar(K clave) {

        int indice = obtenerIndice(clave);

        LinkedList<EntradaHash<K, V>> lista = tabla[indice];

        lista.removeIf(entrada ->
                entrada.getClave().equals(clave));
    }

    // Mostrar tabla
    public void mostrar() {

        for (int i = 0; i < capacidad; i++) {

            System.out.println("Indice " + i + ":");

            for (EntradaHash<K, V> entrada : tabla[i]) {

                System.out.println(
                        "Clave: " + entrada.getClave() +
                                " | Valor: " + entrada.getValor()
                );
            }
        }
    }
}