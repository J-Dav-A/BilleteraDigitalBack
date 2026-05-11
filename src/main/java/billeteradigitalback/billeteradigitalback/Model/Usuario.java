package billeteradigitalback.billeteradigitalback.Model;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelUsuario nivelUsuario;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private boolean activo;

    // =========================
    // CONSTRUCTORES
    // =========================

    public Usuario() {
    }

    public Usuario(String nombre,
                   String correo,
                   String password,
                   NivelUsuario nivelUsuario,
                   LocalDateTime fechaRegistro,
                   boolean activo) {

        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
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

    public void setId(Long id) {
        this.id = id;
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
                ", fechaRegistro=" + fechaRegistro +
                ", activo=" + activo +
                '}';
    }
}
    //Metodo registrarse por primera ves (esta bien pero aqui no va)
    /*public static Usuario registrarse(String id, String nombre, String correo, String contrasenia, String numeroTelefono) {

        boolean existeUsuario = true;

        while (existeUsuario) {

            existeUsuario = false;

            for (Usuario usuario : listaUsuarios) {

                if (usuario.getCorreo().equals(correo)) {

                    System.out.println("Ese correo ya existe");
                    return null;
                }
            }
        }

        Usuario nuevo = new Usuario(
                id,
                nombre,
                correo,
                contrasenia,
                numeroTelefono,
                NivelUsuario.BRONCE
        );

        listaUsuarios.add(nuevo);
        System.out.println("Usuario registrado correctamente");
        return nuevo;
    }

    //Metodo para iniciar sesion (esta bien pero aqui no va)
    public static boolean iniciarSesion(String correo, String contrasenia) {

        boolean usuarioEncontrado = false;

        while (!usuarioEncontrado) {

            for (Usuario usuario : listaUsuarios) {

                if (usuario.getCorreo().equals(correo)
                        && usuario.getContrasenia().equals(contrasenia)) {

                    System.out.println("Inicio de sesión exitoso");
                    usuarioEncontrado = true;
                    break;
                }
            }

            if (!usuarioEncontrado) {
                System.out.println("Usuario o contraseña incorrectos");
                return false;
            }
        }

        return true;
    }
}*/
