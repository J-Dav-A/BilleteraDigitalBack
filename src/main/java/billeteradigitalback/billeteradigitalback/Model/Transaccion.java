package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.EstadoTransaccion;
import billeteradigitalback.billeteradigitalback.Enums.NivelRiesgo;
import billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipoTransaccion;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTransaccion estado;

    @Column(nullable = false)
    private int puntosGenerados;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRiesgo nivelRiesgo;

    @ManyToOne
    @JoinColumn(name = "billetera_origen_id")
    private Billetera billeteraOrigen;

    @ManyToOne
    @JoinColumn(name = "billetera_destino_id")
    private Billetera billeteraDestino;

    @ManyToOne
    @JoinColumn(name = "operacion_programada_id")
    private OperacionProgramada operacionProgramada;

    @OneToOne(mappedBy = "transaccion", cascade = CascadeType.ALL)
    private AuditoriaRiesgo auditoriaRiesgo;

    // Constructor vacío
    public Transaccion() {
    }

    // Constructor completo
    public Transaccion(LocalDateTime fecha, TipoTransaccion tipoTransaccion, BigDecimal valor, EstadoTransaccion estado, int puntosGenerados, NivelRiesgo nivelRiesgo, Billetera billeteraOrigen, Billetera billeteraDestino, OperacionProgramada operacionProgramada, AuditoriaRiesgo auditoriaRiesgo) {
        this.fecha = fecha;
        this.tipoTransaccion = tipoTransaccion;
        this.valor = valor;
        this.estado = estado;
        this.puntosGenerados = puntosGenerados;
        this.nivelRiesgo = nivelRiesgo;
        this.billeteraOrigen = billeteraOrigen;
        this.billeteraDestino = billeteraDestino;
        this.operacionProgramada = operacionProgramada;
        this.auditoriaRiesgo = auditoriaRiesgo;
    }

    public Long getPid() {
        return pid;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoTransaccion getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(TipoTransaccion tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransaccion estado) {
        this.estado = estado;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
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

    public OperacionProgramada getOperacionProgramada() {
        return operacionProgramada;
    }

    public void setOperacionProgramada(OperacionProgramada operacionProgramada) {
        this.operacionProgramada = operacionProgramada;
    }

    public AuditoriaRiesgo getAuditoriaRiesgo() {
        return auditoriaRiesgo;
    }

    public void setAuditoriaRiesgo(AuditoriaRiesgo auditoriaRiesgo) {
        this.auditoriaRiesgo = auditoriaRiesgo;
    }
}
