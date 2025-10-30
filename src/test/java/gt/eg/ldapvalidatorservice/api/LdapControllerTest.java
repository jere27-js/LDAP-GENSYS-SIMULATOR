package gt.eg.ldapvalidatorservice.api;

import gt.eg.ldapvalidatorservice.core.SoapLdapClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LdapController.class)
@Import(LdapControllerTest.TestConfig.class)
class LdapControllerTest {

    static class TestConfig {
        @Bean
        SoapLdapClient soapLdapClient() {
            // Stub personalizado sin uso real de SOAP
            return new SoapLdapClient(new RestTemplate(), "http://dummy", false) {
                @Override
                public String validate(String username, String password) {
                    if ("validUser".equals(username) && "validPass".equals(password)) return "1";
                    if ("invalid".equals(username) && "invalid".equals(password)) return "0";
                    if ("error".equals(username) && "error".equals(password)) return null; // fuerza 500
                    return "0";
                }
            };
        }
    }

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Devuelve 200 y '1' cuando credenciales válidas")
    void returns200WhenValid() throws Exception {
        mvc.perform(post("/ldap/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"validUser\",\"password\":\"validPass\"}"))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Devuelve 401 y '0' cuando credenciales inválidas")
    void returns401WhenInvalid() throws Exception {
        mvc.perform(post("/ldap/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"invalid\",\"password\":\"invalid\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Devuelve 500 cuando validate retorna null (error inesperado)")
    void returns500WhenUnexpected() throws Exception {
        mvc.perform(post("/ldap/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"error\",\"password\":\"error\"}"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Health endpoint devuelve UP")
    void healthEndpoint() throws Exception {
        mvc.perform(get("/ldap/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("UP"));
    }

    @Test
    @DisplayName("Devuelve 400 cuando falta username (NotBlank)")
    void returns400WhenUsernameBlank() throws Exception {
        mvc.perform(post("/ldap/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"\",\"password\":\"algo\"}"))
            .andExpect(status().isBadRequest());
    }
}
