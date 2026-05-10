package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;

public class Beneficio {
    private String id;
    private String descripcion;
    private int puntosNecesarios;
    private NivelUsuario nivelRequerido;
    private boolean activo;

    //falta constructor

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntosNecesarios() {
        return puntosNecesarios;
    }

    public void setPuntosNecesarios(int puntosNecesarios) {
        this.puntosNecesarios = puntosNecesarios;
    }

    public NivelUsuario getNivelRequerido() {
        return nivelRequerido;
    }

    public void setNivelRequerido(NivelUsuario nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
