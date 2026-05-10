package billeteradigitalback.billeteradigitalback.Model;

import java.time.LocalDateTime;

public class OperacionProgramada {
    private String id;
    private LocalDateTime fechaFutura;
    //¿Tipo de operacion o tipo de transaccion??
    private double monto;
    private int prioridad;
    // ¿estado de operacion o estado de transaccion?
    //     private Billetera billeteraOrigen; ¿Deberia ir o no??
    //    private Billetera billeteraDestino; ??
    // ¿Usuario es necesario aqui o bastaria con saber la cuenta destino?
}
