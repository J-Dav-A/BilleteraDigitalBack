package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por correo — para login y validación de duplicados
    Optional<Usuario> findByCorreo(String correo);

    // Verificar si ya existe ese correo (para el registro)
    boolean existsByCorreo(String correo);

    // Buscar todos los usuarios activos
    List<Usuario> findByActivoTrue();

    // Buscar usuarios por nivel — útil para reportes
    List<Usuario> findByNivelUsuario(NivelUsuario nivel);

    // Usuarios con puntos en un rango — complementa el árbol BST en memoria
    List<Usuario> findByPuntosBetween(int min, int max);

    // Top N usuarios con más puntos — para reportes y analytics
    @Query("SELECT u FROM Usuario u ORDER BY u.puntos DESC")
    List<Usuario> findAllOrderByPuntosDesc();
}
