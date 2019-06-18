package org.zalando.actuate.failsafe;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.GET;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NoCircuitBreakersApplication.class, webEnvironment = RANDOM_PORT)
public class EmptyCircuitBreakersEndpointTest {

    @Autowired
    private TestRestTemplate http;

    @Test
    public void shouldReadAll() {
        final Map<String, CircuitBreakerView> breakers = readAll().getCircuitBreakers();

        assertEquals(0, breakers.size());
    }

    private Container readAll() {
        return http.exchange("/actuator/circuitbreakers", GET, EMPTY, Container.class).getBody();
    }

}

