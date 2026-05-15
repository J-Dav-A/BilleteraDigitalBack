package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int puntos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelUsuario nivelUsuario;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private boolean activo;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Set<Billetera> billeteras = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Alerta> alertas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<CanjeoBeneficio> canjeos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<AuditoriaRiesgo> auditorias = new ArrayList<>();

    // =========================
    // CONSTRUCTORES
    // =========================

    public Usuario() {
    }

    public Usuario(String nombre, String correo, String password, int puntos, NivelUsuario nivelUsuario, LocalDateTime fechaRegistro, boolean activo) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.puntos = puntos;
        this.nivelUsuario = nivelUsuario;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public NivelUsuario getNivelUsuario() {
        return nivelUsuario;
    }

    public void setNivelUsuario(NivelUsuario nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<Billetera> getBilleteras() {
        return billeteras;
    }

    public void setBilleteras(Set<Billetera> billeteras) {
        this.billeteras = billeteras;
    }

    public List<Alerta> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<Alerta> alertas) {
        this.alertas = alertas;
    }

    public List<CanjeoBeneficio> getCanjeos() {
        return canjeos;
    }

    public void setCanjeos(List<CanjeoBeneficio> canjeos) {
        this.canjeos = canjeos;
    }

    public List<AuditoriaRiesgo> getAuditorias() {
        return auditorias;
    }

    public void setAuditorias(List<AuditoriaRiesgo> auditorias) {
        this.auditorias = auditorias;
    }


    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", nivelUsuario=" + nivelUsuario +
                ", Puntos=" + puntos +
                ", fechaRegistro=" + fechaRegistro +
                ", activo=" + activo +
                '}';
    }
}