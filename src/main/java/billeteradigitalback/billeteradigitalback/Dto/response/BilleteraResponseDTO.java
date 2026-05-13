package billeteradigitalback.billeteradigitalback.Dto.response;

import billeteradigitalback.billeteradigitalback.Enums.TipoBilletera;

public class BilleteraResponseDTO {

    private Long id;

    private String nombre;

    private TipoBilletera tipoBilletera;

    private double saldo;

    private boolean activa;

    public BilleteraResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}