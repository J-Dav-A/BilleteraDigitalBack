package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.NivelRiesgo;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_riesgo")
public class AuditoriaRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // PK

    @Column(nullable = false)
    private String descripcionEvento;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRiesgo nivelRiesgo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "transaccion_id", nullable = false)
    private Transaccion transaccion;

    public AuditoriaRiesgo() {}

    public AuditoriaRiesgo(String descripcionEvento, LocalDateTime fecha, NivelRiesgo nivelRiesgo, Usuario usuario, Transaccion transaccion) {
        this.descripcionEvento = descripcionEvento;
        this.fecha = fecha;
        this.nivelRiesgo = nivelRiesgo;
        this.usuario = usuario;
        this.transaccion = transaccion;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcionEvento() {
        return descripcionEvento;
    }

    public void setDescripcionEvento(String descripcionEvento) {
        this.descripcionEvento = descripcionEvento;
    }

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }
}