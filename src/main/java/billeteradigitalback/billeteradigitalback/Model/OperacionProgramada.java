package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.EstadoOperacionProgramada;
import billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operaciones_programadas")
public class OperacionProgramada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaFutura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipoTransaccion;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private int prioridad;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private boolean recurrente;

    @Column(nullable = false)
    private int diasRecurrencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOperacionProgramada estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "billetera_origen_id")
    private Billetera billeteraOrigen;

    @ManyToOne
    @JoinColumn(name = "billetera_destino_id")
    private Billetera billeteraDestino;

    @OneToMany(mappedBy = "operacionProgramada")
    private List<Transaccion> transaccionesGeneradas = new ArrayList<>();;

    // Constructor vacío
    public OperacionProgramada() {
    }

    // Constructor completo
    public OperacionProgramada(LocalDateTime fechaFutura, TipoTransaccion tipoTransaccion, BigDecimal monto, int prioridad, String descripcion, boolean recurrente, int diasRecurrencia, EstadoOperacionProgramada estado, Usuario usuario, Billetera billeteraOrigen, Billetera billeteraDestino) {
        this.fechaFutura = fechaFutura;
        this.tipoTransaccion = tipoTransaccion;
        this.monto = monto;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.recurrente = recurrente;
        this.diasRecurrencia = diasRecurrencia;
        this.estado = estado;
        this.usuario = usuario;
        this.billeteraOrigen = billeteraOrigen;
        this.billeteraDestino = billeteraDestino;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaFutura() {
        return fechaFutura;
    }

    public void setFechaFutura(LocalDateTime fechaFutura) {
        this.fechaFutura = fechaFutura;
    }

    public TipoTransaccion getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(TipoTransaccion tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isRecurrente() {
        return recurrente;
    }

    public void setRecurrente(boolean recurrente) {
        this.recurrente = recurrente;
    }

    public int getDiasRecurrencia() {
        return diasRecurrencia;
    }

    public void setDiasRecurrencia(int diasRecurrencia) {
        this.diasRecurrencia = diasRecurrencia;
    }

    public EstadoOperacionProgramada getEstado() {
        return estado;
    }

    public void setEstado(EstadoOperacionProgramada estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Billetera getBilleteraOrigen() {
        return billeteraOrigen;
    }

    public void setBilleteraOrigen(Billetera billeteraOrigen) {
        this.billeteraOrigen = billeteraOrigen;
    }

    public Billetera getBilleteraDestino() {
        return billeteraDestino;
    }

    public void setBilleteraDestino(Billetera billeteraDestino) {
        this.billeteraDestino = billeteraDestino;
    }

    public List<Transaccion> getTransaccionesGeneradas() {
        return transaccionesGeneradas;
    }

    public void setTransaccionesGeneradas(List<Transaccion> transaccionesGeneradas) {
        this.transaccionesGeneradas = transaccionesGeneradas;
    }
}