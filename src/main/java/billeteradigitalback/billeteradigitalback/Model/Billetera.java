package billeteradigitalback.billeteradigitalback.Model;

import java.util.ArrayList;

public class Billetera {
    private String id;
    private String nombre;
    private Enum TipoBilletera;
    private double saldo;
    private Usuario usuario;
    private boolean activa;
    private boolean topeTransacciones;
    ArrayList<Transaccion> transacciones;

    public Billetera(String id, String nombre, Enum tipoBilletera, Usuario usuario, boolean activa, boolean topeTransacciones, ArrayList<Transaccion> transacciones) {
        this.saldo = 0;
        this.id = id;
        this.nombre = nombre;
        TipoBilletera = tipoBilletera;
        this.usuario = usuario;
        this.activa = activa;
        this.topeTransacciones = topeTransacciones;
        this.transacciones = transacciones;
    }

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

    public Enum getTipoBilletera() {
        return TipoBilletera;
    }

    public void setTipoBilletera(Enum tipoBilletera) {
        TipoBilletera = tipoBilletera;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
