package billeteradigitalback.billeteradigitalback.Dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RetiroDTO {

    @NotNull
    private Long billeteraId;

    @NotNull
    @DecimalMin("1.0")
    private BigDecimal valor;

    public RetiroDTO() {}

    public Long getBilleteraId() {
        return billeteraId;
    }

    public void setBilleteraId(Long billeteraId) {
        this.billeteraId = billeteraId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}