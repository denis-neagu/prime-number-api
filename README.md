### Prime Number API
Calculate prime numbers up to and including a limit using diverse algorithms. 
Choose your response layout and calculation strategy using query parameters.
### Project Set-up
Note: We'll need [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-0-13-later-archive-downloads.html)
and [Maven](https://maven.apache.org/download.cgi) installed.
```text
1. git clone https://github.com/denis-neagu/prime-number-api
2. cd primenumberapi
3. mvn clean install
4. mvn spring-boot:run
```
### Tech Stack
- Java Version: 17
- Framework: Spring Boot
- Testing: JUnit, Mockito, REST-Assured
- Build & Dependency: Maven
- Annotation Processing and Productivity: Lombok
- Serialisation/Deserialisation: Jackson
- Deployment: AWS, EC2, Virtual Private Cloud (VPC)
- Continuous Integration: GitHub Workflows 
### Deployment
// ToDo
### Features
- Diverse algorithms: Trial Division, Optimised Trial Division, Sieve of Eratosthenes, Segmented Sieve, Bitset Segmented Sieve
- Caching
- Cache merges
- REST API
- Invalid input handling
- Global exception handler including fallback responses
- JSON and XML support
- Swagger documentation
- Test coverage 