package billeteradigitalback.billeteradigitalback.Estructuras.Arboles;

public class ArbolBST<T> {

    private NodoArbol<T> raiz;

    public ArbolBST() {
        this.raiz = null;
    }

    // INSERTAR
    public void insertar(int clave, T dato) {

        raiz = insertarRecursivo(raiz, clave, dato);
    }

    private NodoArbol<T> insertarRecursivo(
            NodoArbol<T> actual,
            int clave,
            T dato
    ) {

        if (actual == null) {

            return new NodoArbol<>(clave, dato);
        }

        // Menor -> izquierda
        if (clave < actual.getClave()) {

            actual.setIzquierdo(
                    insertarRecursivo(
                            actual.getIzquierdo(),
                            clave,
                            dato
                    )
            );
        }

        // Mayor -> derecha
        else if (clave > actual.getClave()) {

            actual.setDerecho(
                    insertarRecursivo(
                            actual.getDerecho(),
                            clave,
                            dato
                    )
            );
        }

        return actual;
    }

    // BUSCAR
    public T buscar(int clave) {

        NodoArbol<T> nodo = buscarRecursivo(raiz, clave);

        return nodo != null ? nodo.getDato() : null;
    }

    private NodoArbol<T> buscarRecursivo(
            NodoArbol<T> actual,
            int clave
    ) {

        // No encontrado
        if (actual == null) {
            return null;
        }

        // Encontrado
        if (clave == actual.getClave()) {
            return actual;
        }

        // Buscar izquierda
        if (clave < actual.getClave()) {

            return buscarRecursivo(
                    actual.getIzquierdo(),
                    clave
            );
        }

        // Buscar derecha
        return buscarRecursivo(
                actual.getDerecho(),
                clave
        );
    }

    // RECORRIDO INORDER
    public void inorder() {

        inorderRecursivo(raiz);
    }

    private void inorderRecursivo(NodoArbol<T> actual) {

        if (actual != null) {

            inorderRecursivo(actual.getIzquierdo());

            System.out.println(
                    "Clave: " + actual.getClave() +
                            " | Dato: " + actual.getDato()
            );

            inorderRecursivo(actual.getDerecho());
        }
    }

    // RECORRIDO PREORDER
    public void preorder() {

        preorderRecursivo(raiz);
    }

    private void preorderRecursivo(NodoArbol<T> actual) {

        if (actual != null) {

            System.out.println(
                    "Clave: " + actual.getClave() +
                            " | Dato: " + actual.getDato()
            );

            preorderRecursivo(actual.getIzquierdo());
            preorderRecursivo(actual.getDerecho());
        }
    }

    // RECORRIDO POSTORDER
    public void postorder() {

        postorderRecursivo(raiz);
    }

    private void postorderRecursivo(NodoArbol<T> actual) {

        if (actual != null) {

            postorderRecursivo(actual.getIzquierdo());
            postorderRecursivo(actual.getDerecho());

            System.out.println(
                    "Clave: " + actual.getClave() +
                            " | Dato: " + actual.getDato()
            );
        }
    }
}