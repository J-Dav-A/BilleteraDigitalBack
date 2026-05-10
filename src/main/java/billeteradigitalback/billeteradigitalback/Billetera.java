package billeteradigitalback.billeteradigitalback;

import java.util.ArrayList;

public class Billetera {
    private String id;
    private String nombre;
    //tipo billetera
    private double saldo;
    //es necesaria la clave?
    //Usuario?
    private boolean activa;
    //fecha de creacion?
    private boolean topeTransacciones;
    ArrayList<Transaccion> transacciones;

    //falta contructor


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public boolean isTopeTransacciones() {
        return topeTransacciones;
    }

    public void setTopeTransacciones(boolean topeTransacciones) {
        this.topeTransacciones = topeTransacciones;
    }

    public ArrayList<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(ArrayList<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }
}
