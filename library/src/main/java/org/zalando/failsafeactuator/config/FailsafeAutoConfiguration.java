package org.zalando.failsafeactuator.config;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.zalando.failsafeactuator.endpoint.FailsafeEndpoint;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

/** Autoconfiguration for the FailsafeEndpoint. */
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeAutoConfiguration {

  private CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    circuitBreakerRegistry = new CircuitBreakerRegistry();
    return circuitBreakerRegistry;
  }

  @Bean
  @DependsOn("circuitBreakerRegistry")
  public FailsafeEndpoint createEndpoint() {
    return new FailsafeEndpoint(circuitBreakerRegistry);
  }

  /** Condition to check that the Failsafe endpoint is enabled */
  static class FailsafeCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(
        final ConditionContext context, final AnnotatedTypeMetadata metadata) {
      final boolean endpointsEnabled = isEnabled(context, "endpoints.", true);
      final ConditionMessage.Builder message = ConditionMessage.forCondition("Failsafe");
      if (isEnabled(context, "endpoints.failsafe.", endpointsEnabled)) {
        return ConditionOutcome.match(message.because("enabled"));
      }
      return ConditionOutcome.noMatch(message.because("not enabled"));
    }

    private boolean isEnabled(
        final ConditionContext context, final String prefix, final boolean defaultValue) {
      final RelaxedPropertyResolver resolver =
          new RelaxedPropertyResolver(context.getEnvironment(), prefix);
      return resolver.getProperty("enabled", Boolean.class, defaultValue);
    }
  }
}
