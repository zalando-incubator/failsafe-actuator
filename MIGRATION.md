# Failsafe actuator 2.0 Migration Guide

## Breaking changes

- Failsafe actuator changed endpoint id from `circuit-breakers` to `circuitbreakers` 
for alignment with naming constraints introduced in Spring Boot 2.1. 
This leads to change actuator endpoint url to `/acutuator/circuitbreakers`
