package billeteradigitalback.billeteradigitalback.Estructuras.TablasHash;

public class EntradaHash<K, V> {

    private K clave;
    private V valor;

    public EntradaHash(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public K getClave() {
        return clave;
    }

    public void setClave(K clave) {
        this.clave = clave;
    }

    public V getValor() {
        return valor;
    }

    public void setValor(V valor) {
        this.valor = valor;
    }
}