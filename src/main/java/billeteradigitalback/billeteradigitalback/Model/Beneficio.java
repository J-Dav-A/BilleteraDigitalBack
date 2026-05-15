package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "beneficios")
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false)
    private int puntosNecesarios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelUsuario nivelRequerido;

    @Column(nullable = false)
    private boolean activo;

    @OneToMany(mappedBy = "beneficio")
    private List<CanjeoBeneficio> canjeos = new ArrayList<>();

    // Constructor vacío
    public Beneficio() {}

    // Constructor completo

    public Beneficio(String descripcion, int puntosNecesarios, NivelUsuario nivelRequerido, boolean activo) {
        this.descripcion = descripcion;
        this.puntosNecesarios = puntosNecesarios;
        this.nivelRequerido = nivelRequerido;
        this.activo = activo;
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
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

    public List<CanjeoBeneficio> getCanjeos() {
        return canjeos;
    }

    public void setCanjeos(List<CanjeoBeneficio> canjeos) {
        this.canjeos = canjeos;
    }
}