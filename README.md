# Spring Pipeline

A minimal Spring Boot REST application.

## Run

Requires Java 17 and Maven.

```sh
mvn spring-boot:run
```

The greeting endpoint is available at `GET http://localhost:8080/api/hello` and returns:

```json
{"message":"Hello, World!"}
```

Run the tests with:

```sh
mvn test
```
