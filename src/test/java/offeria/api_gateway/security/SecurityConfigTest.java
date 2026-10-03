package offeria.api_gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "jwt.secret=0123456789012345678901234567890123456789012345678901234567890123"
        }
)
class SecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRejectProtectedEndpointWithoutAuthentication() {
        webTestClient.get()
                .uri("/api/v1/rfqs")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldAllowHealthEndpointWithoutAuthentication() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus()
                .isOk();
    }
}
