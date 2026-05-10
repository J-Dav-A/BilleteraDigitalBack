package billeteradigitalback.billeteradigitalback.Model;

import java.time.LocalDateTime;

public class Transaccion {
    private String PID;
    private LocalDateTime fecha;
    private Enum TipoTransaccion;
    private int valor;
    private Billetera billeteraOrigen;
    private Billetera billeteraDestino;
    private boolean estado;
    private int puntosGenerados;
    //Nivel de riesgo si va en esta clase??

    //falta definir el constructor

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Enum getTipoTransaccion() {
        return TipoTransaccion;
    }

    public void setTipoTransaccion(Enum tipoTransaccion) {
        TipoTransaccion = tipoTransaccion;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public Billetera getBilleteraOrigen() {
        return billeteraOrigen;
    }

    public void setBilleteraOrigen(Billetera billeteraOrigen) {
        this.billeteraOrigen = billeteraOrigen;
    }

    public Billetera getBilleteraDestino() {
        return billeteraDestino;
    }

    public void setBilleteraDestino(Billetera billeteraDestino) {
        this.billeteraDestino = billeteraDestino;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }
}
