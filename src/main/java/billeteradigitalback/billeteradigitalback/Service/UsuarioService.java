package billeteradigitalback.billeteradigitalback.Service;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import billeteradigitalback.billeteradigitalback.Enums.TipoAlerta;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class UsuarioService {

    // =========================================================
    // DEPENDENCIAS
    // =========================================================

    private final UsuarioRepository usuarioRepository;
    private final AlertaService alertaService;

    // @Lazy en AlertaService evita dependencia circular
    // (AlertaService también usa UsuarioService)
    public UsuarioService(UsuarioRepository usuarioRepository,
                          @Lazy AlertaService alertaService) {
        this.usuarioRepository = usuarioRepository;
        this.alertaService = alertaService;
    }

    // =========================================================
    // ESTRUCTURAS DE DATOS EN MEMORIA
    // =========================================================

    /**
     * TABLA HASH — HashMap<Long, Usuario>
     * Búsqueda O(1) por ID. Evita ir a la BD en cada operación.
     * Se actualiza en cada create/update/delete.
     */
    private final Map<Long, Usuario> cacheUsuarios = new HashMap<>();

    /**
     * ÁRBOL BST — TreeMap<Integer, List<Usuario>>
     * Clave   = puntosAcumulados (se mantiene ordenado automáticamente)
     * Valor   = lista de usuarios con ese puntaje
     *
     * Permite:
     *  - Búsqueda por rango de puntos en O(log n) con subMap()
     *  - Ranking ordenado recorriendo el árbol
     *  - Clasificación por nivel de forma eficiente
     */
    private final TreeMap<Integer, List<Usuario>> arbolPorPuntos = new TreeMap<>();

    // =========================================================
    // INICIALIZACIÓN AL ARRANCAR LA APP
    // =========================================================

    /**
     * @PostConstruct: se ejecuta una sola vez cuando Spring
     * termina de construir el bean. Carga todos los usuarios
     * de la BD en las estructuras en memoria.
     */
    @PostConstruct
    public void inicializarEstructuras() {
        List<Usuario> todos = usuarioRepository.findAll();
        for (Usuario u : todos) {
            cacheUsuarios.put(u.getId(), u);
            insertarEnArbol(u);
        }
        System.out.println("[UsuarioService] " + todos.size()
                + " usuarios cargados en cache y árbol.");
    }

    // =========================================================
    // CRUD
    // =========================================================

    public Usuario registrarUsuario(Usuario usuario) {
        // Validar correo único
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el correo: " + usuario.getCorreo());
        }

        // Valores por defecto
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        usuario.setNivelUsuario(NivelUsuario.BRONCE);
        usuario.setPuntos(0);

        // 1. Guardar en BD
        Usuario guardado = usuarioRepository.save(usuario);

        // 2. Insertar en tabla hash — O(1)
        cacheUsuarios.put(guardado.getId(), guardado);

        // 3. Insertar en árbol con 0 puntos — O(log n)
        insertarEnArbol(guardado);

        // 4. Alerta de bienvenida
        alertaService.crearAlerta(
                guardado,
                "¡Bienvenido a la plataforma, " + guardado.getNombre() + "!",
                TipoAlerta.INFO);

        return guardado;
    }

    /**
     * Búsqueda O(1) usando tabla hash.
     * Si no está en cache va a BD como respaldo.
     */
    public Usuario buscarPorId(Long id) {
        Usuario enCache = cacheUsuarios.get(id);
        if (enCache != null) return enCache;

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Usuario no encontrado con id: " + id));
    }

    public Usuario buscarPorCorreo(String correo) {
        // Corregido: findByCorreo retorna Optional, hay que manejarlo
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new NoSuchElementException(
                        "Usuario no encontrado con correo: " + correo));
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario actualizarUsuario(Long id, String nuevoNombre) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(nuevoNombre);

        Usuario guardado = usuarioRepository.save(usuario);

        // Refrescar en cache
        cacheUsuarios.put(id, guardado);

        return guardado;
    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);

        // Limpiar estructuras en memoria
        cacheUsuarios.remove(id);
        eliminarDelArbol(usuario);

        usuarioRepository.delete(usuario);
    }

    // =========================================================
    // PUNTOS Y NIVELES
    // =========================================================

    /**
     * Suma puntos al usuario.
     * Se llama desde TransaccionService después de cada operación.
     *
     * Pasos:
     * 1. Sacar del árbol (con los puntos viejos)
     * 2. Sumar puntos
     * 3. Recalcular nivel
     * 4. Guardar en BD
     * 5. Reinsertar en árbol (con puntos nuevos)
     * 6. Refrescar cache
     */
    public void agregarPuntos(Long id, int puntos) {
        Usuario usuario = buscarPorId(id);

        // Sacar del árbol ANTES de cambiar los puntos
        eliminarDelArbol(usuario);

        NivelUsuario nivelAnterior = usuario.getNivelUsuario();

        usuario.setPuntos(usuario.getPuntos() + puntos);
        NivelUsuario nivelNuevo = calcularNivel(usuario.getPuntos());
        usuario.setNivelUsuario(nivelNuevo);

        Usuario guardado = usuarioRepository.save(usuario);

        // Reinsertar en árbol con los puntos nuevos
        insertarEnArbol(guardado);

        // Refrescar cache
        cacheUsuarios.put(id, guardado);

        // Notificar si subió de nivel
        if (nivelNuevo != nivelAnterior) {
            alertaService.crearAlerta(
                    guardado,
                    "¡Felicitaciones! Subiste al nivel " + nivelNuevo.name(),
                    TipoAlerta.ASCENSO_NIVEL);
        }
    }

    /**
     * Resta puntos — al revertir transacción o canjear beneficio.
     * Mismo patrón que agregarPuntos.
     */
    public void canjearPuntos(Long id, int puntos) {
        Usuario usuario = buscarPorId(id);

        eliminarDelArbol(usuario);

        // No permitir puntos negativos
        int puntosNuevos = Math.max(0, usuario.getPuntos() - puntos);
        usuario.setPuntos(puntosNuevos);
        usuario.setNivelUsuario(calcularNivel(puntosNuevos));

        Usuario guardado = usuarioRepository.save(usuario);
        insertarEnArbol(guardado);
        cacheUsuarios.put(id, guardado);
    }

    // =========================================================
    // CONSULTAS CON EL ÁRBOL BST
    // =========================================================

    /**
     * Usuarios con puntos entre min y max.
     * TreeMap.subMap() es O(log n) — mucho más eficiente que filtrar una lista.
     */
    public List<Usuario> buscarPorRangoDePuntos(int min, int max) {
        List<Usuario> resultado = new ArrayList<>();
        arbolPorPuntos
                .subMap(min, true, max, true)
                .values()
                .forEach(resultado::addAll);
        return resultado;
    }

    /**
     * Todos los usuarios ordenados de mayor a menor puntaje.
     * descendingMap() invierte el árbol — O(n) para recorrerlo todo.
     */
    public List<Usuario> rankingPorPuntos() {
        List<Usuario> ranking = new ArrayList<>();
        arbolPorPuntos.descendingMap()
                .values()
                .forEach(ranking::addAll);
        return ranking;
    }

    /** Top N usuarios con más puntos */
    public List<Usuario> topUsuarios(int n) {
        return rankingPorPuntos().stream().limit(n).toList();
    }

    /** Usuarios filtrados por nivel usando rangos del árbol */
    public List<Usuario> buscarPorNivel(NivelUsuario nivel) {
        return switch (nivel) {
            case BRONCE  -> buscarPorRangoDePuntos(0, 500);
            case PLATA   -> buscarPorRangoDePuntos(501, 1000);
            case ORO     -> buscarPorRangoDePuntos(1001, 5000);
            case PLATINO -> buscarPorRangoDePuntos(5001, Integer.MAX_VALUE);
        };
    }

    // =========================================================
    // HELPERS PRIVADOS
    // =========================================================

    private void insertarEnArbol(Usuario u) {
        arbolPorPuntos
                .computeIfAbsent(u.getPuntos(), k -> new ArrayList<>())
                .add(u);
    }

    private void eliminarDelArbol(Usuario u) {
        List<Usuario> lista = arbolPorPuntos.get(u.getPuntos());
        if (lista != null) {
            lista.removeIf(x -> x.getId().equals(u.getId()));
            if (lista.isEmpty()) arbolPorPuntos.remove(u.getPuntos());
        }
    }

    /**
     * Regla de negocio de niveles.
     * CORREGIDO: usa if/else if — sin esto un usuario con 300 puntos
     * entraría a todos los if y terminaría en PLATINO.
     */
    private NivelUsuario calcularNivel(int puntos) {
        if (puntos <= 500)       return NivelUsuario.BRONCE;
        else if (puntos <= 1000) return NivelUsuario.PLATA;
        else if (puntos <= 5000) return NivelUsuario.ORO;
        else                     return NivelUsuario.PLATINO;
    }
}