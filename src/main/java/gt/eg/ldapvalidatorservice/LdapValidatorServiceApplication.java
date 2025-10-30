package gt.eg.ldapvalidatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class LdapValidatorServiceApplication {
    private static final Logger log = LoggerFactory.getLogger(LdapValidatorServiceApplication.class);
    public static void main(String[] args) {
        log.info("Iniciando LdapValidatorServiceApplication...");
        SpringApplication.run(LdapValidatorServiceApplication.class, args);
        log.info("Aplicación iniciada correctamente.");
    }
}
