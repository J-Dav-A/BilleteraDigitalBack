package billeteradigitalback.billeteradigitalback;

import java.util.ArrayList;

public class Usuario {
    private String id;
    private String nombre;
    private String correo; //Condicional?
    private String contrasenia;
    private String numeroTelefono;
    private int puntos;
    private int nivelUsuario;
    //rol usuario?
    //activo?
    //para que la fecha de registro?
    ArrayList<Billetera> billeteras;

    // Lista donde se guardan todos los usuarios registrados
    static ArrayList<Usuario> listaUsuarios= new ArrayList<>();

    // Constructor
    public Usuario(String id, String nombre, String correo, String contrasenia, String numeroTelefono) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.numeroTelefono = numeroTelefono;
        this.puntos = 0;
        this.nivelUsuario = 1;
        this.billeteras = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getNivelUsuario() {
        return nivelUsuario;
    }

    public void setNivelUsuario(int nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public ArrayList<Billetera> getBilleteras() {
        return billeteras;
    }

    public void setBilleteras(ArrayList<Billetera> billeteras) {
        this.billeteras = billeteras;
    }

    public static ArrayList<Usuario> getUsuariosRegistrados() {
        return listaUsuarios;
    }

    public static void setUsuariosRegistrados(ArrayList<Usuario> usuariosRegistrados) {
        Usuario.listaUsuarios = usuariosRegistrados;
    }

    //Metodo registrarse por primera ves
    public static Usuario registrarse(String id, String nombre, String correo, String contrasenia, String numeroTelefono) {

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
                numeroTelefono
        );

        listaUsuarios.add(nuevo);
        System.out.println("Usuario registrado correctamente");
        return nuevo;
    }

    //Metodo para iniciar sesion
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
}
