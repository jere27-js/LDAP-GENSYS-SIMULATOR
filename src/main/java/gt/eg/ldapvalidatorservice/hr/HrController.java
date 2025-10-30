package gt.eg.ldapvalidatorservice.hr;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/ws_data_rh")
public class HrController {

    private final EmployeeRepository repository;
    private final String expectedToken;
    private static final Logger log = LoggerFactory.getLogger(HrController.class);

    public HrController(EmployeeRepository repository,
                        @Value("${hr.api.token}") String expectedToken) {
        this.repository = repository;
        this.expectedToken = expectedToken;
    }

    /**
     * Endpoint: POST /ws_data_rh/DataRRHHEmp.asmx?op=RRHHData
     * Body JSON:
     * {
     *   "authentication": {"token": "..."},
     *   "data": {"codigo": "690007"}
     * }
     * Respuestas:
     * 200 -> lista con un empleado
     * 401 -> token inválido
     * 404 -> código no encontrado
     */
    @PostMapping(value = "/DataRRHHEmp.asmx", params = "op=RRHHData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Employee>> rrhhData(@Valid @RequestBody RRHHDataRequest request) {
        String token = request.authentication().token();
        String codigo = request.data().codigo();
        log.info("[RRHH] Solicitud para código '{}' con token recibido (longitud={}).", codigo, token.length());
        if (!expectedToken.equals(token)) {
            log.warn("[RRHH] Token inválido para código '{}'", codigo);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var emp = repository.findByCodigo(codigo);
        if (emp == null) {
            log.info("[RRHH] Código '{}' no encontrado.", codigo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.debug("[RRHH] Código '{}' encontrado: nombre='{}'", codigo, emp.nombreColaborador());
        return ResponseEntity.ok(List.of(emp));
    }
}
