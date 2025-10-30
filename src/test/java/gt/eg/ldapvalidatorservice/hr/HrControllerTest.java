package gt.eg.ldapvalidatorservice.hr;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HrController.class)
@Import(HrControllerTest.TestConfig.class)
class HrControllerTest {

    static class TestConfig {
        @Bean
        EmployeeRepository employeeRepository() {
            return new EmployeeRepository(); // usa los 10 empleados predefinidos
        }
        @Bean
        HrController hrController(EmployeeRepository repo) {
            return new HrController(repo, "kUnSeIu*oMJC(Nx5L%7X)KyxO");
        }
    }

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Devuelve 200 y empleado cuando token y codigo son válidos")
    void returnsEmployeeWhenValid() throws Exception {
        mvc.perform(post("/ws_data_rh/DataRRHHEmp.asmx?op=RRHHData")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"authentication\":{\"token\":\"kUnSeIu*oMJC(Nx5L%7X)KyxO\"},\"data\":{\"codigo\":\"690007\"}}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codigoColaborador").value("690007"))
            .andExpect(jsonPath("$[0].nombreColaborador").value("LOPEZ JEREMY"));
    }

    @Test
    @DisplayName("Devuelve 401 cuando token inválido")
    void returns401WhenBadToken() throws Exception {
        mvc.perform(post("/ws_data_rh/DataRRHHEmp.asmx?op=RRHHData")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"authentication\":{\"token\":\"BAD\"},\"data\":{\"codigo\":\"690007\"}}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Devuelve 404 cuando código no existe")
    void returns404WhenNotFound() throws Exception {
        mvc.perform(post("/ws_data_rh/DataRRHHEmp.asmx?op=RRHHData")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"authentication\":{\"token\":\"kUnSeIu*oMJC(Nx5L%7X)KyxO\"},\"data\":{\"codigo\":\"999999\"}}"))
            .andExpect(status().isNotFound());
    }
}
