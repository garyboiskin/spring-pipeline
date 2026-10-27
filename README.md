# spring-pipeline

A minimal Spring Boot REST API sample that handles GET, POST, PUT, PATCH, and
DELETE requests at `/api/requests`. POST, PUT, and PATCH accept a JSON object;
each response reports the HTTP method and echoes the request body.

Run the application with Java 17 and Maven:

#Test dev branch


```sh
mvn spring-boot:run
```

For example:

```sh
curl http://localhost:8080/api/requests
curl -X POST http://localhost:8080/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"sample"}'
curl -X PUT http://localhost:8080/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"updated"}'
curl -X PATCH http://localhost:8080/api/requests \
  -H 'Content-Type: application/json' \
  -d '{"name":"changed"}'
curl -X DELETE http://localhost:8080/api/requests

mvn test
```
