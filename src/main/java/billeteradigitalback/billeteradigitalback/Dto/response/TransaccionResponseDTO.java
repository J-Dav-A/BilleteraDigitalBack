package billeteradigitalback.billeteradigitalback.Dto.response;

import billeteradigitalback.billeteradigitalback.Enums.EstadoTransaccion;
import billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion;

import java.time.LocalDateTime;

public class TransaccionResponseDTO {

    private Long id;

    private LocalDateTime fecha;

    private TipoTransaccion tipoTransaccion;

    private double valor;

    private EstadoTransaccion estado;

    private int puntosGenerados;

    public TransaccionResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoTransaccion getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(TipoTransaccion tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransaccion estado) {
        this.estado = estado;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }
}