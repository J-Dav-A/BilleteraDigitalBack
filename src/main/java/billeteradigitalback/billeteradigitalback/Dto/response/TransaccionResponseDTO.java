package billeteradigitalback.billeteradigitalback.Dto.response;

import billeteradigitalback.billeteradigitalback.Enums.EstadoTransaccion;
import billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion;
import billeteradigitalback.billeteradigitalback.Model.Transaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionResponseDTO {

    private Long id;
    private String tipo;
    private BigDecimal monto;
    private Integer puntosGenerados;

    private Long billeteraOrigenId;
    private Long billeteraDestinoId;

    public TransaccionResponseDTO() {
    }

    public TransaccionResponseDTO(Transaccion tx) {

        this.id = tx.getPid();

        this.tipo = tx.getTipoTransaccion() != null
                ? tx.getTipoTransaccion().name()
                : null;

        this.monto = tx.getValor();

        this.puntosGenerados = tx.getPuntosGenerados();

        this.billeteraOrigenId = tx.getBilleteraOrigen() != null
                ? tx.getBilleteraOrigen().getId()
                : null;

        this.billeteraDestinoId = tx.getBilleteraDestino() != null
                ? tx.getBilleteraDestino().getId()
                : null;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public Integer getPuntosGenerados() {
        return puntosGenerados;
    }

    public Long getBilleteraOrigenId() {
        return billeteraOrigenId;
    }

    public Long getBilleteraDestinoId() {
        return billeteraDestinoId;
    }
}