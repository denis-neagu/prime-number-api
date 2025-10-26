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
**Note:** No-configuration, easy-deployment platforms such as Heroku or Render provide HTTP(S) out the box. AWS EC2 does not enable HTTP(S) out of the box. 
To enable it we could use a reverse proxy like Nginx in the EC2 instance, but we'd need a certificate from a Certificate Authority (CA).
We could use a self-signed certificate, but we'd still receive a warning message. 
Alternatively we could also use a modern Load Balancer such as AWS Application Load Balancer (ALB) and use AWS' Certificate Manager (ACM).<br>

**Note:** I have mapped the app to port 8080 in the EC2 instance, and you need to explicitly define the port in the URL before making a request. 
Otherwise, by default HTTP uses port 80, so it won't be making the request to the correct resource.

URL: http://ec2-18-132-106-253.eu-west-2.compute.amazonaws.com:8080/api/v1/primes
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