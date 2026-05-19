package billeteradigitalback.billeteradigitalback.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final String SECRET = "fintech_secret";

    // =========================================
    // GENERAR TOKEN
    // =========================================
    public String generarToken(String correo) {

        return JWT.create()
                .withSubject(correo)
                .withIssuedAt(new Date())
                .withExpiresAt(
                        new Date(System.currentTimeMillis() + 86400000)
                )
                .sign(Algorithm.HMAC256(SECRET));
    }

    // =========================================
    // EXTRAER CORREO
    // =========================================
    public String extraerCorreo(String token) {

        DecodedJWT jwt = JWT.require(
                        Algorithm.HMAC256(SECRET)
                )
                .build()
                .verify(token);

        return jwt.getSubject();
    }

    // =========================================
    // VALIDAR TOKEN
    // =========================================
    public boolean tokenValido(String token) {

        try {

            JWT.require(
                            Algorithm.HMAC256(SECRET)
                    )
                    .build()
                    .verify(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}