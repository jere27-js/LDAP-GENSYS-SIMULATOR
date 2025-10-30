# ldap-validator-service

Servicio REST (Spring Boot) que recibe `username` y `password`, invoca (o simula) un servicio SOAP de validación y devuelve **texto plano** `1` (válido) o `0` (no válido). Incluye además un servicio de datos de RRHH en memoria y logging estructurado con correlación.

## Endpoints

### 1. Validación LDAP (mock/real)
`POST /ldap/validate`

Body JSON:
```json
{
  "username": "validUser",
  "password": "validPass"
}
```
Respuestas:
- `200 OK` body: `1` (credenciales válidas)
- `401 Unauthorized` body: `0` (credenciales inválidas)
- `500 Internal Server Error` body: `0` (error inesperado / parsing / timeout)

Modo mock activado si `ldapSoap.mock.enabled=true` en `application.yml`. En modo mock se responde `1` solo para `jeremy.lopez` / `abc123` y `0` para el resto.

Ejemplo curl:
```bash
curl -X POST http://localhost:8081/ldap/validate \
  -H "Content-Type: application/json" \
  -d '{"username":"jeremy.lopez","password":"abc123"}'
```

### 2. Health Check
`GET /ldap/health`

Respuesta: `200 OK` body: `UP`

### 3. Datos RRHH
`POST /ws_data_rh/DataRRHHEmp.asmx?op=RRHHData`

Body JSON:
```json
{
  "authentication": { "token": "kUnSeIu*oMJC(Nx5L%7X)KyxO" },
  "data": { "codigo": "690007" }
}
```
Respuestas:
- `200 OK` body: `[ { ... empleado ... } ]`
- `401 Unauthorized` (token inválido)
- `404 Not Found` (código no existe)

Ejemplo curl:
```bash
curl -X POST "http://localhost:8081/ws_data_rh/DataRRHHEmp.asmx?op=RRHHData" \
  -H "Content-Type: application/json" \
  -d '{"authentication":{"token":"kUnSeIu*oMJC(Nx5L%7X)KyxO"},"data":{"codigo":"690007"}}'
```

## Respuesta ejemplo RRHH
```json
[
  {
    "codigoColaborador": "690007",
    "nombreColaborador": "LOPEZ JEREMY",
    "puestoColaborador": "DESARROLLADOR",
    "departamentoColaborador": "DEP DESARROLLO",
    "subGerencia": "SUBGERENCIA DESARROLLO",
    "gerencia1": "GERENCIA TI",
    "gerencia2": "GERENCIA OPERACIONES TI",
    "direccion": "DIRECCION PAIS",
    "compania": "18-OPERADORA LABORAL, S.A.",
    "noTelAutoconsumo": "500005 66666666",
    "correoElectronico": "empleado7@claro.com.gt",
    "dpi": "00000000000007",
    "sede": "EDIFICIO ZONA 6",
    "codigoJefeColaborador": "7805",
    "nombreJefeColaborador": "VELASQUE INNER EDUARDO",
    "puestoJefeColaborador": "SUPERVISOR ESTACIONES BASE",
    "gerenciaJefeColaborador": "GERENCIA TECNICA PAIS",
    "correoElectronicoJefe": "jefe1@claro.com.gt",
    "numeroTelJefe": "582205672",
    "devuelve": null
  }
]
```

## Logging y Correlación
Se añadió un filtro (`RequestLoggingFilter`) que genera o reutiliza la cabecera `X-Correlation-Id` y emite dos líneas por petición:
- Entrada: `--> [correlationId] MÉTODO URI`
- Salida: `<-- [correlationId] MÉTODO URI status=... time=...ms`

Puedes enviar tu propio `X-Correlation-Id` en el request y se propagará en la respuesta.

### Niveles configurados
En `application.yml`:
```yaml
logging:
  level:
    root: INFO
    gt.eg.ldapvalidatorservice.core.SoapLdapClient: DEBUG
    gt.eg.ldapvalidatorservice.hr: DEBUG
    gt.eg.ldapvalidatorservice.api: INFO
```
Ajusta a `INFO` si los logs de debug son muy verbosos.

### Buenas prácticas
- No elevar `DEBUG` en producción salvo investigación puntual.
- No loggear contraseñas: solo se imprime el nombre de usuario.
- Correlation ID facilita rastrear flujos entre servicios.

## Configuración principal (`application.yml`)
```yaml
ldapSoap:
  endpointUrl: "https://..."   # SOAP real (en mock no se usa la red)
  connectTimeoutMs: 5000
  readTimeoutMs: 8000
  mock:
    enabled: true  # <--- cambiar a false para usar el endpoint real
hr:
  api:
    token: "kUnSeIu*oMJC(Nx5L%7X)KyxO"  # token esperado por RRHH
ssl:
  devTrustAll: true  # confiar todos los certificados solo en DEV
```

## Ejecutar
```bash
mvn spring-boot:run
```

## Docker (opcional)
```bash
mvn -DskipTests package
docker build -t ldap-validator-service:0.1 .
docker run --rm -p 8081:8081 ldap-validator-service:0.1
```

## Docker Compose
Se incluye `docker-compose.yml` para simplificar el levantamiento del servicio.

### Pasos (Windows CMD)
```cmd
mvn -DskipTests package
docker compose up --build -d
```

Ver logs:
```cmd
docker compose logs -f
```
Detener y eliminar contenedor/red:
```cmd
docker compose down
```

### Archivo docker-compose.yml (resumen)
```yaml
services:
  ldap-validator-service:
    build: .
    ports:
      - "8081:8081"
    environment:
      - JAVA_OPTS=-Xms256m -Xmx512m
      - SPRING_PROFILES_ACTIVE=default
    restart: unless-stopped
```

Nota: El healthcheck no está activo porque la imagen base no trae `curl`. Si deseas agregarlo:
1. Modifica el Dockerfile para instalar `curl` (ejemplo: `RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*`).
2. Descomenta el bloque healthcheck del `docker-compose.yml`.

## Próximos pasos sugeridos
- Endpoint `GET /ws_data_rh/employees` para listar todos los empleados.
- Formato de logs JSON (Logback encoder) para integrar con ELK / Splunk.
- Métricas con Micrometer (`/actuator/prometheus`).
- Manejo centralizado de errores (ControllerAdvice) para respuestas uniformes.

---
Cualquier mejora que necesites (logs JSON, métricas, seguridad), pídela y se añade.
