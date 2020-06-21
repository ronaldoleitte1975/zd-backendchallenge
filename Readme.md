## Stack
- Java 8
- Spring Boot 2.3.0
- Maven
- MongoDB
- Redis
- Docker
- Postman 

## Technical Especifications

### MongoDB
Save and search business partner through GeoJSON. It supports Geospatial libs and we can easily query using "$near" and "$geoIntersects" operators.

### Cache with Redis
Cache aprroaches in get business partner from data base. This way improves performance when has a big volume of requests. 
 
 ![Screenshot](https://github.com/ronaldoleitte1975/zd-backendchallenge/blob/master/Cache%20Diagram.png)

### Bean Validations in Controllers
BusinessPartnerController class has validations' annotation like @NotNull @Min, @Max, @Valid. Those are a better way validate in first application's layer avoiding processing invalid objetcs. BusinessPartner model class has annotations too like @NotBlank, @CNPJ, @Null and annotations is validates by @Valid annotation in BusinnesPartnerController end-points.

### Docker Compose
Docker compose that automatically starts all necessary dependencies (redis and mongodb) and starts API on port 5151. 

### Postman Collections
Follow link below about Postman Collection repository:

https://web.postman.co/collections/4082433-e96388ea-7b10-4f9c-8ce7-b22f385fe08e?version=latest&workspace=6d9e3974-c2c1-47b2-862b-b72071a0f5c9

### Run API
Follow steps below in order to run this API through docker environment:

`mvn clean install`

`docker-compose up --build`


If you want run API in your IDE, change API port from `5151` to `8080` in `docker-compose.yml`. In `application.yml`  change mogo uri to: `mongodb://localhost:27017/db` and run `docker-compose-up` in order to start MongoDB e Redis containers.

 
