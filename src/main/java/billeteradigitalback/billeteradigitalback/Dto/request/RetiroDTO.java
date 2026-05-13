package billeteradigitalback.billeteradigitalback.Dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RetiroDTO {

    @NotNull
    private Long billeteraId;

    @Min(1)
    private double valor;

    public RetiroDTO() {}

    public Long getBilleteraId() {
        return billeteraId;
    }

    public void setBilleteraId(Long billeteraId) {
        this.billeteraId = billeteraId;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}