package billeteradigitalback.billeteradigitalback.Dto.request;

import billeteradigitalback.billeteradigitalback.Enums.TipoBilletera;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BilleteraDTO {

    @NotBlank
    private String nombre;

    @NotNull
    private TipoBilletera tipoBilletera;

    @NotNull
    private Long usuarioId;

    @NotNull
    private BigDecimal limite;

    public BilleteraDTO() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoBilletera getTipoBilletera() {
        return tipoBilletera;
    }

    public void setTipoBilletera(TipoBilletera tipoBilletera) {
        this.tipoBilletera = tipoBilletera;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }
}