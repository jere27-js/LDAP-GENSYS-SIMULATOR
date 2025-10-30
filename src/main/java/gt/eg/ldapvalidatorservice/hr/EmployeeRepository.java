package gt.eg.ldapvalidatorservice.hr;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmployeeRepository {
    private final Map<String, Employee> employees = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(EmployeeRepository.class);

    public EmployeeRepository() {
        // Cargar 10 empleados genéricos (códigos 690001..690010)
        put(new Employee("690001","PEREZ MANUEL","TECNICO EN TRANSMISION I","SUPERVISION TRANSMISION ROOSEVELTH","SUBGERENCIA DE TRANSMISION","GERENCIA TECNICA PAIS","GERENCIA DE OPERACIÓN PLANTA INTERNA","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","585658 24206029","empleado1@claro.com.gt","00000000000001","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690002","LOPEZ ANA","ING SISTEMAS","DEP SISTEMAS","SUBGERENCIA SISTEMAS","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500000 11111111","empleado2@claro.com.gt","00000000000002","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690003","GARCIA LUIS","ANALISTA","DEP ANALISIS","SUBGERENCIA ANALISIS","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500001 22222222","empleado3@claro.com.gt","00000000000003","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690004","FLORES MARIA","COORD PROYECTOS","DEP PROYECTOS","SUBGERENCIA PROYECTOS","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500002 33333333","empleado4@claro.com.gt","00000000000004","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690005","MENDOZA CARLOS","TECNICO SOPORTE","DEP SOPORTE","SUBGERENCIA SOPORTE","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500003 44444444","empleado5@claro.com.gt","00000000000005","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690006","RODRIGUEZ SARA","DBA","DEP BASES","SUBGERENCIA BASES","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500004 55555555","empleado6@claro.com.gt","00000000000006","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690007","LOPEZ JEREMY","DESARROLLADOR","DEP DESARROLLO","SUBGERENCIA DESARROLLO","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500005 66666666","empleado7@claro.com.gt","00000000000007","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690008","HERNANDEZ JOSE","QA TESTER","DEP QA","SUBGERENCIA QA","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500006 77777777","empleado8@claro.com.gt","00000000000008","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690009","RAMIREZ JULIO","SCRUM MASTER","DEP AGIL","SUBGERENCIA AGIL","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500007 88888888","empleado9@claro.com.gt","00000000000009","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        put(new Employee("690010","CASTILLO ANDREA","UX DESIGNER","DEP UX","SUBGERENCIA UX","GERENCIA TI","GERENCIA OPERACIONES TI","DIRECCION PAIS","18-OPERADORA LABORAL, S.A.","500008 99999999","empleado10@claro.com.gt","00000000000010","EDIFICIO ZONA 6","7805","VELASQUE INNER EDUARDO","SUPERVISOR ESTACIONES BASE","GERENCIA TECNICA PAIS","jefe1@claro.com.gt","582205672",null));
        log.info("[RRHH] Repositorio inicializado con {} empleados", employees.size());
    }

    private void put(Employee e) { employees.put(e.codigoColaborador(), e); }

    public Employee findByCodigo(String codigo) {
        var emp = employees.get(codigo);
        if (emp == null) {
            log.debug("[RRHH] findByCodigo('{}') -> null", codigo);
        } else {
            log.debug("[RRHH] findByCodigo('{}') -> '{}'", codigo, emp.nombreColaborador());
        }
        return emp;
    }

    public List<Employee> findAll() {
        log.debug("[RRHH] findAll() devuelve {} empleados", employees.size());
        return List.copyOf(employees.values());
    }
}
