package billeteradigitalback.billeteradigitalback.Dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TransferenciaDTO {

    @NotNull
    private Long billeteraOrigenId;

    @NotNull
    private Long billeteraDestinoId;

    @Min(1)
    private double valor;

    public TransferenciaDTO() {}

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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}