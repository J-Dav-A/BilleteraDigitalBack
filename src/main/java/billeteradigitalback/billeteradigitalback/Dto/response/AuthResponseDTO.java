package billeteradigitalback.billeteradigitalback.Dto.response;

import billeteradigitalback.billeteradigitalback.Model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private Usuario usuario;
}