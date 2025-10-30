package gt.eg.ldapvalidatorservice.api;

import gt.eg.ldapvalidatorservice.core.SoapLdapClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/ldap")
public class LdapController {

    private final SoapLdapClient client;
    private static final Logger log = LoggerFactory.getLogger(LdapController.class);

    public LdapController(SoapLdapClient client) {
        this.client = client;
    }

    /**
     * POST /ldap/validate
     * Body JSON: { "username": "...", "password": "..." }
     * Respuestas:
     * 200 OK  -> body "1" (válido)
     * 401 Unauthorized -> body "0" (credenciales inválidas)
     * 500 Internal Server Error -> body "0" (error inesperado/parsing)
     */
    @PostMapping(value = "/validate", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> validate(@Valid @RequestBody ValidateRequest req) {
        log.info("[LDAP] Petición de validación recibida para usuario '{}'.", req.username());
        String ret = client.validate(req.username(), req.password());
        if ("1".equals(ret)) {
            log.info("[LDAP] Usuario '{}' válido.", req.username());
            return ResponseEntity.ok("1");
        } else if ("0".equals(ret)) {
            log.warn("[LDAP] Usuario '{}' credenciales inválidas.", req.username());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("0");
        } else {
            log.error("[LDAP] Usuario '{}' error inesperado en validación (ret={}).", req.username(), ret);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("0");
        }
    }

    /** Healthcheck simple: devuelve UP si el servicio está levantado. */
    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> health() {
        log.debug("Health check solicitado");
        return ResponseEntity.ok("Todo OK");
    }
}
