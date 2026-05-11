package billeteradigitalback.billeteradigitalback;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class BilleteraDigitalBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BilleteraDigitalBackApplication.class, args);
    }
}
