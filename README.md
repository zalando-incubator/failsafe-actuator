[![Codacy Badge](https://api.codacy.com/project/badge/Grade/97639870e76546cab6fd2597c583c0b1)](https://www.codacy.com/app/MALPI/failsafe-actuator?utm_source=github.com&utm_medium=referral&utm_content=zalando-incubator/failsafe-actuator&utm_campaign=badger)
[![Build Status](https://travis-ci.org/zalando/failsafe-actuator.svg?branch=master)](https://travis-ci.org/zalando/failsafe-actuator)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/failsafe-actuator.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/failsafe-actuator)

# Failsafe Actuator

**Failsafe Actuator** is a Java library that provides a simple monitoring interface for [Spring Boot](https://projects.spring.io/spring-boot/) 
applications that use the [Failsafe](https://github.com/jhalterman/failsafe) library. 
Using Failsafe Actuator will readily expose the state of your [Circuit Breakers](http://martinfowler.com/bliki/CircuitBreaker.html) (closed, open, half-open) 
to your Spring Actuator endpoint without additional effort. 

## Core Technical Concepts/Inspiration

Failsafe Actuator supports Spring's dependency injection to make it easier to use *Failsafe*. 
It allows you to monitor the state of your Circuit Breakers so that, whenever a third party that your app relies upon 
suddenly becomes unavailable, you can discover it immediately and take action. This is essential for applications used in production.

## Development Status/Project Roadmap
This library is currently under development and used in production at [Zalando](https://jobs.zalando.com/tech/). 

Find more details about our development plans in the [Issues Tracker](https://github.com/zalando-incubator/failsafe-actuator/issues). 

We're always [looking for contributors](https://github.com/zalando-incubator/failsafe-actuator/blob/master/CONTRIBUTIONS.md), 
so if you find an interesting "Help Wanted" issue then please drop us a line in the related issue to claim it and begin working.

Unless you explicitly state otherwise in advance, any non trivial contribution intentionally submitted for inclusion in this project by you to the steward of this repository (Zalando SE, Berlin) shall be under the terms and conditions of the MIT License, without any additional copyright information, terms or conditions.

## Getting Started

### Dependencies/Requirements
* Java 8
* [Spring Boot 2](http://projects.spring.io/spring-boot/) 
* [Failsafe](https://github.com/jhalterman/failsafe)

### Running/Using

To use Failsafe Actuator, add the following dependency to your project:

**Gradle:**
```groovy
compile("org.zalando:failsafe-actuator:${FAILSAFE-ACTUATOR-VERSION}")
```

**Maven:**
```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>failsafe-actuator</artifactId>
    <version>${failsafe-actuator.version}</version>
</dependency>
```

Create your `CircuitBreaker` by defining them as a `Bean`.

```java
@Configuration
public class CircuitBreakerConfiguration {
  @Bean
  public CircuitBreaker myBreaker() {
    return new CircuitBreaker();
  }
}
```

You can use and configure the created `CircuitBreaker` by autowiring it in the class where it should be used.


```java
@Component
public class MyBean {
    @Autowired
    private CircuitBreaker myBreaker;
}
```

That's it. By calling the [endpoint](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html) via _**http://${yourAddress}/actuator/circuitbreakers**_.
you will get a response which looks like the following:


```http
GET /actuator/circuitbreakers

HTTP/1.1 200
Content-Type: application/json

{
  "myBreaker": {
    "state": "OPEN"
  },
  "otherBreaker": {
    "state": "CLOSED"
  }
}
```

Individual circuit breakers can be requested via `/acutuator/circuitbreakers/{name}`:

```http
GET /actuator/circuitbreakers/myBreaker

HTTP/1.1 200
Content-Type: application/json

{
  "state": "OPEN"
}
```

You can even modify the circuit breaker state and manually open or close them:

```http
POST /actuator/circuitbreakers/myBreaker
Content-Type: application/json

{
  "state": "CLOSED"
}
```

## Example usage

To see a complete example on how to use the library take a look at the
[Sample Application](src/test/java/org/zalando/failsafeactuator/SampleApplication.java).
It starts a [Rest Controller](src/test/java/org/zalando/failsafeactuator/SampleController.java)
that shows how to autowire `CircuitBreaker` into your application
and configure them.

## How to build on your own

In order to build the JAR on your own run the following command:

```bash
mvn clean install
```

## License

This code is released under the MIT license. See [License](LICENSE).
