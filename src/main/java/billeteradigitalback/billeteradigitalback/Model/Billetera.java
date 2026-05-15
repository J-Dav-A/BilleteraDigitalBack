package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.TipoBilletera;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billeteras")
public class Billetera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBilletera tipoBilletera;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private BigDecimal limiteTransaccion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "billeteraOrigen")
    private List<Transaccion> transaccionesOrigen = new ArrayList<>();

    @OneToMany(mappedBy = "billeteraDestino")
    private List<Transaccion> transaccionesDestino = new ArrayList<>();

    @OneToMany(mappedBy = "billeteraOrigen")
    private List<OperacionProgramada> operacionesOrigen = new ArrayList<>();

    @OneToMany(mappedBy = "billeteraDestino")
    private List<OperacionProgramada> operacionesDestino = new ArrayList<>();

    // Constructor vacío
    public Billetera() {}

    // Constructor completo

    public Billetera(String nombre, TipoBilletera tipoBilletera, BigDecimal saldo, boolean activa, BigDecimal limiteTransaccion, Usuario usuario) {
        this.nombre = nombre;
        this.tipoBilletera = tipoBilletera;
        this.saldo = saldo;
        this.activa = activa;
        this.limiteTransaccion = limiteTransaccion;
        this.usuario = usuario;
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public BigDecimal getLimiteTransaccion() {
        return limiteTransaccion;
    }

    public void setLimiteTransaccion(BigDecimal limiteTransaccion) {
        this.limiteTransaccion = limiteTransaccion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Transaccion> getTransaccionesOrigen() {
        return transaccionesOrigen;
    }

    public void setTransaccionesOrigen(List<Transaccion> transaccionesOrigen) {
        this.transaccionesOrigen = transaccionesOrigen;
    }

    public List<Transaccion> getTransaccionesDestino() {
        return transaccionesDestino;
    }

    public void setTransaccionesDestino(List<Transaccion> transaccionesDestino) {
        this.transaccionesDestino = transaccionesDestino;
    }

    public List<OperacionProgramada> getOperacionesOrigen() {
        return operacionesOrigen;
    }

    public void setOperacionesOrigen(List<OperacionProgramada> operacionesOrigen) {
        this.operacionesOrigen = operacionesOrigen;
    }

    public List<OperacionProgramada> getOperacionesDestino() {
        return operacionesDestino;
    }

    public void setOperacionesDestino(List<OperacionProgramada> operacionesDestino) {
        this.operacionesDestino = operacionesDestino;
    }
}