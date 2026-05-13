package billeteradigitalback.billeteradigitalback.Dto.request;

import billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CrearOperacionProgramadaDTO {

    @NotNull
    private Long billeteraOrigenId;

    @NotNull
    private Long billeteraDestinoId;

    @NotNull
    private TipoTransaccion tipoOperacion;

    @Min(1)
    private double monto;

    @Future
    private LocalDateTime fechaFutura;

    private int prioridad;

    public CrearOperacionProgramadaDTO() {}

    public Long getBilleteraOrigenId() {
        return billeteraOrigenId;
    }

    public void setBilleteraOrigenId(Long billeteraOrigenId) {
        this.billeteraOrigenId = billeteraOrigenId;
    }

    public Long getBilleteraDestinoId() {
        return billeteraDestinoId;
    }

    public void setBilleteraDestinoId(Long billeteraDestinoId) {
        this.billeteraDestinoId = billeteraDestinoId;
    }

    public TipoTransaccion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoTransaccion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaFutura() {
        return fechaFutura;
    }

    public void setFechaFutura(LocalDateTime fechaFutura) {
        this.fechaFutura = fechaFutura;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
}