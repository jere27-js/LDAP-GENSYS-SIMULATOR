package gt.eg.ldapvalidatorservice.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class HttpClientConfig {

    @Value("${ldapSoap.connectTimeoutMs}")
    private int connectTimeoutMs;

    @Value("${ldapSoap.readTimeoutMs}")
    private int readTimeoutMs;

    @Value("${ssl.devTrustAll:true}")
    private boolean devTrustAll;

    @Value("${ssl.truststore.enabled:false}")
    private boolean truststoreEnabled;

    @Value("${ssl.truststore.path:}")
    private String truststorePath;

    @Value("${ssl.truststore.password:changeit}")
    private String truststorePassword;

    private static final Logger log = LoggerFactory.getLogger(HttpClientConfig.class);

    @Bean
    public RestTemplate restTemplate() throws Exception {
        log.info("Creando RestTemplate con connectTimeoutMs={} readTimeoutMs={} devTrustAll={} truststoreEnabled={}", connectTimeoutMs, readTimeoutMs, devTrustAll, truststoreEnabled);
        SSLContext sslContext;
        if (devTrustAll) {
            TrustStrategy trustAll = (chain, authType) -> true;
            sslContext = SSLContexts.custom().loadTrustMaterial(trustAll).build();
            log.warn("SSL devTrustAll activado: se confía en todos los certificados");
        } else if (truststoreEnabled) {
            KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
            log.info("Cargando truststore desde '{}'", truststorePath);
            try (FileInputStream fis = new FileInputStream(truststorePath)) {
                ts.load(fis, truststorePassword.toCharArray());
            }
            sslContext = SSLContexts.custom().loadTrustMaterial(ts, null).build();
        } else {
            sslContext = SSLContexts.custom().build();
            log.debug("Usando SSLContext por defecto sin truststore personalizado");
        }

        RequestConfig rc = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .build();
        log.debug("RequestConfig creado connectTimeout={}ms responseTimeout={}ms", connectTimeoutMs, readTimeoutMs);

        var sslsfBuilder = SSLConnectionSocketFactoryBuilder.create().setSslContext(sslContext);
        if (devTrustAll) {
            // Desactiva verificación de hostname solo en modo devTrustAll
            sslsfBuilder.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }

        var cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslsfBuilder.build())
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(rc)
                .build();
        log.info("RestTemplate creado exitosamente");
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}
