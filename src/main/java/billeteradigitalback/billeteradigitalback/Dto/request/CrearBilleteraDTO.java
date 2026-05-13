package billeteradigitalback.billeteradigitalback.Dto.request;

import billeteradigitalback.billeteradigitalback.Enums.TipoBilletera;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearBilleteraDTO {

    @NotBlank
    private String nombre;

    @NotNull
    private TipoBilletera tipoBilletera;

    @NotNull
    private Long usuarioId;

    public CrearBilleteraDTO() {}

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
}