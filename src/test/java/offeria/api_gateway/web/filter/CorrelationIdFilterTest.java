package offeria.api_gateway.web.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void shouldGenerateCorrelationIdWhenHeaderIsMissing() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/rfqs").build()
        );

        AtomicReference<String> forwardedCorrelationId = new AtomicReference<>();

        GatewayFilterChain chain = filteredExchange -> {
            forwardedCorrelationId.set(
                    filteredExchange.getRequest()
                            .getHeaders()
                            .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER)
            );
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(forwardedCorrelationId.get()).isNotBlank();
        assertThat(exchange.getResponse().getHeaders()
                .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER))
                .isEqualTo(forwardedCorrelationId.get());
    }

    @Test
    void shouldPreserveExistingCorrelationId() {
        String correlationId = "offeria-test-correlation-id";

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/rfqs")
                        .header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId)
                        .build()
        );

        AtomicReference<String> forwardedCorrelationId = new AtomicReference<>();

        GatewayFilterChain chain = filteredExchange -> {
            forwardedCorrelationId.set(
                    filteredExchange.getRequest()
                            .getHeaders()
                            .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER)
            );
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(forwardedCorrelationId.get()).isEqualTo(correlationId);
        assertThat(exchange.getResponse().getHeaders()
                .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER))
                .isEqualTo(correlationId);
    }
}
