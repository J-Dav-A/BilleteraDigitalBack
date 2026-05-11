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

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private BigDecimal limiteTransaccion;

    //Corregir porque esta estructura toca corregirla
    @OneToMany(mappedBy = "billeteraOrigen")
    private List<Transaccion> transacciones = new ArrayList<>();

    // Constructor vacío
    public Billetera() {
    }

    // Constructor completo
    public Billetera(String nombre,
                     TipoBilletera tipoBilletera,
                     Usuario usuario,
                     boolean activa,
                     BigDecimal limiteTransaccion) {

        this.nombre = nombre;
        this.tipoBilletera = tipoBilletera;
        this.usuario = usuario;
        this.activa = activa;
        this.limiteTransaccion = limiteTransaccion;
        this.saldo = BigDecimal.ZERO;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }
}