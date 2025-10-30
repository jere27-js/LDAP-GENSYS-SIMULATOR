package gt.eg.ldapvalidatorservice.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class SoapLdapClientMockTest {

    @Test
    @DisplayName("Modo mock: credenciales correctas devuelven '1'")
    void mockModeValidCredentials() {
        RestTemplate rt = new RestTemplate();
        SoapLdapClient client = new SoapLdapClient(rt, "http://no-usado", true);
        String r = client.validate("jeremy.lopez", "abc123");
        assertEquals("1", r);
    }

    @Test
    @DisplayName("Modo mock: credenciales incorrectas devuelven '0'")
    void mockModeInvalidCredentials() {
        RestTemplate rt = new RestTemplate();
        SoapLdapClient client = new SoapLdapClient(rt, "http://no-usado", true);
        String r = client.validate("otro", "xyz");
        assertEquals("0", r);
    }
}
