package billeteradigitalback.billeteradigitalback.Estructuras.Grafos;

import billeteradigitalback.billeteradigitalback.Model.Usuario;

import java.util.*;

public class GrafoTransferencias {

    // Usuario origen -> lista de usuarios destino
    private Map<Usuario, List<Usuario>> grafo;

    public GrafoTransferencias() {
        this.grafo = new HashMap<>();
    }

    // Agregar usuario al grafo
    public void agregarUsuario(Usuario usuario) {

        if (!grafo.containsKey(usuario)) {
            grafo.put(usuario, new ArrayList<>());
        }
    }

    // Crear transferencia entre usuarios
    public void agregarTransferencia(Usuario origen, Usuario destino) {

        agregarUsuario(origen);
        agregarUsuario(destino);

        grafo.get(origen).add(destino);
    }

    // Obtener usuarios conectados
    public List<Usuario> obtenerDestinos(Usuario usuario) {

        return grafo.getOrDefault(usuario, new ArrayList<>());
    }

    // Mostrar grafo completo
    public void mostrarGrafo() {

        for (Usuario usuario : grafo.keySet()) {

            System.out.print(usuario.getNombre() + " -> ");

            List<Usuario> destinos = grafo.get(usuario);

            for (Usuario destino : destinos) {
                System.out.print(destino.getNombre() + " ");
            }

            System.out.println();
        }
    }

    // Verificar si existe conexión
    public boolean existeConexion(Usuario origen, Usuario destino) {

        List<Usuario> destinos = grafo.get(origen);

        if (destinos == null) {
            return false;
        }

        return destinos.contains(destino);
    }

    // Cantidad de transferencias realizadas
    public int cantidadTransferencias(Usuario usuario) {

        List<Usuario> destinos = grafo.get(usuario);

        if (destinos == null) {
            return 0;
        }

        return destinos.size();
    }

    // DFS para buscar rutas
    public void recorridoDFS(Usuario inicio) {

        Set<Usuario> visitados = new HashSet<>();

        dfs(inicio, visitados);
    }

    private void dfs(Usuario usuario, Set<Usuario> visitados) {

        if (usuario == null || visitados.contains(usuario)) {
            return;
        }

        visitados.add(usuario);

        System.out.println(usuario.getNombre());

        List<Usuario> vecinos = grafo.get(usuario);

        if (vecinos != null) {

            for (Usuario vecino : vecinos) {
                dfs(vecino, visitados);
            }
        }
    }

    // BFS para analizar conexiones
    public void recorridoBFS(Usuario inicio) {

        Set<Usuario> visitados = new HashSet<>();
        Queue<Usuario> cola = new LinkedList<>();

        cola.add(inicio);
        visitados.add(inicio);

        while (!cola.isEmpty()) {

            Usuario actual = cola.poll();

            System.out.println(actual.getNombre());

            List<Usuario> vecinos = grafo.get(actual);

            if (vecinos != null) {

                for (Usuario vecino : vecinos) {

                    if (!visitados.contains(vecino)) {

                        visitados.add(vecino);
                        cola.add(vecino);
                    }
                }
            }
        }
    }
}