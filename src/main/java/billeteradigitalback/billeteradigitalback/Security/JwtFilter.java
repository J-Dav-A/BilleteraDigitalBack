package billeteradigitalback.billeteradigitalback.Security;

import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain

    ) throws ServletException, IOException {

        // =========================================
        // OBTENER HEADER
        // =========================================

        final String authHeader =
                request.getHeader("Authorization");

        // =========================================
        // VALIDAR SI EXISTE TOKEN
        // =========================================

        if (
                authHeader == null ||
                        !authHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(request, response);
            return;
        }

        // =========================================
        // EXTRAER TOKEN
        // =========================================

        String token =
                authHeader.substring(7);

        // =========================================
        // VALIDAR TOKEN
        // =========================================

        if (!jwtService.tokenValido(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        // =========================================
        // EXTRAER CORREO
        // =========================================

        String correo =
                jwtService.extraerCorreo(token);
        System.out.println("TOKEN RECIBIDO: " + token);
        System.out.println("CORREO EXTRAIDO: " + correo);
        // =========================================
        // BUSCAR USUARIO
        // =========================================

        Usuario usuario =
                usuarioRepository
                        .findByCorreo(correo)
                        .orElse(null);

        if (usuario == null) {

            filterChain.doFilter(request, response);
            return;
        }

        // =========================================
        // CREAR AUTENTICACIÓN
        // =========================================

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        Collections.emptyList()
                );

        authToken.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        // =========================================
        // GUARDAR USUARIO AUTENTICADO
        // =========================================

        SecurityContextHolder
                .getContext()
                .setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}