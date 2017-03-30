/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.service;

import net.jodah.failsafe.CircuitBreaker;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

/**
 * Registry which holds a reference to registered circuit breakers.
 *
 * @author mpickhan on 29.06.16.
 */
public class CircuitBreakerRegistry {

  private final Map<String, CircuitBreaker> concurrentBreakerMap = new ConcurrentHashMap<String, CircuitBreaker>();

  /**
   * Will put the {@link CircuitBreaker} into the registry. There is no check which avoids overwriting of identifiers. Therefore be sure that your identifiers
   * are unique, or you want to overwrite the current {@link CircuitBreaker} which is registered with this identifier.
   *
   * @param breaker Which should be added
   * @param name Which is used to identify the CircuitBreaker
   */
  void registerCircuitBreaker(final CircuitBreaker breaker, final String name) {
    Assert.hasText(name, "Name for circuitbreaker needs to be set");
    Assert.notNull(breaker, "Circuitbreaker to add, can't be null");

    final CircuitBreaker replaced = concurrentBreakerMap.put(name, breaker);
    Assert.isNull(replaced, "There was an Circuit-Breaker registered already with name : " + name);
  }

  /**
   * Checks if a {@link CircuitBreaker} with the given name was already registered.
   *
   * @param name That should be checked
   * @return <code>true</code> if a CircuitBreaker was already registered, <code>false</code> otherwise
   */
  boolean contains(final String name) {
    Assert.hasText(name, "Name for circuitbreaker needs to be set");
    return concurrentBreakerMap.containsKey(name);
  }

  /**
   * Returns the {@link CircuitBreaker} for the given name or <code>null</code> if no breaker with
   * that name was registered before.
   *
   * @param name of the CircuitBreaker to get
   * @return the found CircuitBreaker or <code>null</code>
   */
  CircuitBreaker get(final String name) {
    Assert.hasText(name, "Name for circuitbreaker needs to be set");
    return concurrentBreakerMap.get(name);
  }

  /**
   * Returns the {@link Map} with registered circuit breakers.
   *
   * @return
   */
  public Map<String, CircuitBreaker> getConcurrentBreakerMap() {
    return this.concurrentBreakerMap;
  }

  @PreDestroy
  public void destroy() throws Exception {
    concurrentBreakerMap.clear();
  }
}
