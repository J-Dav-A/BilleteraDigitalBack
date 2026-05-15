package billeteradigitalback.billeteradigitalback.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "canjeos_beneficio")
public class CanjeoBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaCanje;
    private int puntosUsados;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "beneficio_id", nullable = false)
    private Beneficio beneficio;

    public CanjeoBeneficio() {}

    public CanjeoBeneficio(LocalDateTime fechaCanje, int puntosUsados, Usuario usuario, Beneficio beneficio) {
        this.fechaCanje = fechaCanje;
        this.puntosUsados = puntosUsados;
        this.usuario = usuario;
        this.beneficio = beneficio;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaCanje() {
        return fechaCanje;
    }

    public void setFechaCanje(LocalDateTime fechaCanje) {
        this.fechaCanje = fechaCanje;
    }

    public int getPuntosUsados() {
        return puntosUsados;
    }

    public void setPuntosUsados(int puntosUsados) {
        this.puntosUsados = puntosUsados;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Beneficio getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(Beneficio beneficio) {
        this.beneficio = beneficio;
    }
}