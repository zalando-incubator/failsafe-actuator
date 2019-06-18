package org.zalando.actuate.failsafe;

import net.jodah.failsafe.CircuitBreaker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

@Endpoint(id = "circuitbreakers")
public class CircuitBreakersEndpoint {

    private final Map<String, CircuitBreaker> breakers;

    public CircuitBreakersEndpoint(final Map<String, CircuitBreaker> breakers) {
        this.breakers = breakers;
    }

    @ReadOperation
    public Container circuitBreakers() {
        final Map<String, CircuitBreakerView> circuitBreakers = breakers.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> toView(entry.getValue())));

        return new Container(circuitBreakers);
    }

    @ReadOperation
    @Nullable
    public CircuitBreakerView circuitBreaker(@Selector final String name) {
        final CircuitBreaker breaker = breakers.get(name);

        if (breaker == null) {
            return null;
        }

        return toView(breaker);
    }

    @WriteOperation
    @Nullable
    public CircuitBreakerView transitionTo(@Selector final String name, final CircuitBreaker.State state) {
        final CircuitBreaker breaker = breakers.get(name);

        if (breaker == null) {
            return null;
        }

        transitioner(state).accept(breaker);

        return toView(breaker);
    }

    private CircuitBreakerView toView(final CircuitBreaker breaker) {
        return new CircuitBreakerView(breaker.getState());
    }

    private Consumer<CircuitBreaker> transitioner(final CircuitBreaker.State state) {
        switch (state) {
            case CLOSED:
                return CircuitBreaker::close;
            case OPEN:
                return CircuitBreaker::open;
            case HALF_OPEN:
                return CircuitBreaker::halfOpen;
            default:
                throw new UnsupportedOperationException("Unknown state: " + state);
        }
    }

}
