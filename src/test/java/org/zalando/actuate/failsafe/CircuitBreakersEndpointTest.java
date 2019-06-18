package org.zalando.actuate.failsafe;

import net.jodah.failsafe.CircuitBreaker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.util.Map;

import static net.jodah.failsafe.CircuitBreaker.State.CLOSED;
import static net.jodah.failsafe.CircuitBreaker.State.HALF_OPEN;
import static net.jodah.failsafe.CircuitBreaker.State.OPEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.GET;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleApplication.class, webEnvironment = RANDOM_PORT)
public class CircuitBreakersEndpointTest {

    @Autowired
    private TestRestTemplate http;

    @PostConstruct
    public void configure() {
        this.http.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler());
    }

    @Before
    public void before() {

        write("test", CLOSED);
    }

    @Test
    public void shouldReadAll() {
        final Map<String, CircuitBreakerView> breakers = readAll().getCircuitBreakers();

        assertEquals(2, breakers.size());
        assertTrue(breakers.containsKey("test"));
        assertTrue(breakers.containsKey("delay"));

        final CircuitBreakerView view = breakers.get("test");
        assertEquals(CLOSED, view.getState());
    }

    @Test
    public void shouldReadOne() {
        final CircuitBreakerView view = readOne("test");
        assertNotNull(view);
        assertEquals(CLOSED, view.getState());
    }

    @Test(expected = HttpClientErrorException.class)
    public void shouldReadUnknown() {
        readOne("unknown");
    }

    @Test
    public void shouldOpen() {
        verifyTransitionTo(OPEN);
    }

    @Test
    public void shouldHalfOpen() {
        verifyTransitionTo(HALF_OPEN);
    }

    @Test
    public void shouldClose() {
        write("test", OPEN); // so we have an actual state change, since closed is the default

        verifyTransitionTo(CLOSED);
    }

    @Test
    public void shouldWriteUnknown() {
        assertNull(write("unknown", OPEN));
    }

    private void verifyTransitionTo(final CircuitBreaker.State state) {
        final CircuitBreakerView written = write("test", state);
        final CircuitBreakerView read = readOne("test");

        assertEquals(read, written);
        assertEquals(read.getState(), state);
    }

    private Container readAll() {
        return http.exchange("/actuator/circuitbreakers", GET, EMPTY, Container.class).getBody();
    }

    private CircuitBreakerView readOne(final String name) {
        return http.getForObject("/actuator/circuitbreakers/{name}",
                CircuitBreakerView.class, name);
    }

    private CircuitBreakerView write(final String test, final CircuitBreaker.State state) {
        return http.postForObject("/actuator/circuitbreakers/{name}",
                new CircuitBreakerView(state), CircuitBreakerView.class, test);
    }

}

