package gt.eg.ldapvalidatorservice.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SoapLdapClient {

    private final RestTemplate restTemplate;
    private final String endpointUrl;
    private final boolean mockEnabled;

    private static final Logger log = LoggerFactory.getLogger(SoapLdapClient.class);

    public SoapLdapClient(RestTemplate restTemplate,
                          @Value("${ldapSoap.endpointUrl}") String endpointUrl,
                          @Value("${ldapSoap.mock.enabled:false}") boolean mockEnabled) {
        this.restTemplate = restTemplate;
        this.endpointUrl = endpointUrl;
        this.mockEnabled = mockEnabled;
    }

    /** Devuelve "1" o "0" (o null si hubo problema) */
    public String validate(String username, String password) {
        log.debug("Iniciando validación SOAP para usuario '{}', mockEnabled={}", username, mockEnabled);
        if (mockEnabled) {
            // Simulación: usuarios/contraseñas específicos devuelven éxito, el resto falla
            if (("jeremy.lopez".equals(username) || "carlos.archila".equals(username))
                    && "abc123".equals(password)) {
                log.info("Validación MOCK exitosa para usuario '{}': devuelve 1", username);
                return "1"; // éxito
            }
            log.info("Validación MOCK inválida para usuario '{}': devuelve 0", username);
            return "0"; // inválido
        }

        String envelope = buildEnvelope(username, password);
        log.trace("Envelope SOAP construido (longitud={} bytes)", envelope.length());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/soap+xml; charset=utf-8"));
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_XML, MediaType.TEXT_XML));

        String xml = restTemplate.postForObject(endpointUrl, new HttpEntity<>(envelope, headers), String.class);
        if (xml == null || xml.isBlank()) {
            log.warn("Respuesta SOAP vacía o nula para usuario '{}'", username);
            return null;
        }

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

            var xp = XPathFactory.newInstance().newXPath();
            String ret = (String) xp.evaluate("//*[local-name()='return']/text()", doc, XPathConstants.STRING);
            log.debug("Resultado extraído del XML para usuario '{}': '{}'", username, ret);
            return ret != null ? ret.trim() : null;
        } catch (Exception e) {
            log.error("Error parseando respuesta SOAP para usuario '{}': {}", username, e.getMessage(), e);
            return null;
        }
    }

    private String buildEnvelope(String user, String pass) {
        StringBuilder sb = new StringBuilder();
        sb.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" ")
          .append("xmlns:proc=\"http://procedimiento.claro.com/\">")
          .append("<soap:Header/>")
          .append("<soap:Body>")
          .append("<proc:usuario>")
          .append("<usuario>").append(escape(user)).append("</usuario>")
          .append("<password>").append(escape(pass)).append("</password>")
          .append("</proc:usuario>")
          .append("</soap:Body>")
          .append("</soap:Envelope>");
        return sb.toString();
    }

    private String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;")
                .replace(">","&gt;").replace("\"","&quot;")
                .replace("'","&apos;");
    }
}
