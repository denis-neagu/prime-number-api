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
- **Java Version**: 17
- **Framework**: Spring Boot
- **Testing**: JUnit, Mockito, REST-Assured
- **Build & Dependency Management**: Maven
- **Annotation Processing and Productivity**: Lombok
- **Serialisation/Deserialisation**: Jackson
- **Deployment**: AWS, EC2, Virtual Private Cloud (VPC)
- **Continuous Integration**: GitHub Workflows 
### Deployment
**Note:** No-configuration, easy-deployment platforms such as Heroku or Render provide HTTP(S) out the box. AWS EC2 does not enable HTTP(S) out of the box. 
To enable it we could use a reverse proxy like Nginx in the EC2 instance, but we'd need a certificate from a Certificate Authority (CA).
We could use a self-signed certificate, but we'd still receive a warning message. 
Alternatively we could also use a modern Load Balancer such as AWS Application Load Balancer (ALB) and use AWS' Certificate Manager (ACM).<br>

**Note:** I have mapped the app to port 8080 in the EC2 instance, and you need to explicitly define the port in the URL before making a request. 
Otherwise, by default HTTP uses port 80, so it won't be making the request to the correct resource.

**Note:** The EC2 instance only provides 1GB of RAM and 1vCPU, therefore the number of primes we can calculate is limited to a small number around 100 million. 
We could use more vertical scaling using more RAM and CPU Cores to support higher calculations.

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

### API Endpoint
##### `HTTP GET /api/v1/primes`
##### Query Parameters
| Parameter Name | Type               | Description                                                                                                                                                                                                      | Constraints                             | Default Value              |
|----------------|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|----------------------------|
| `limit`        | `long`             | Specifies the upper bound for the range of numbers to process.                                                                                                                                                   | Must be at least 2 (`@Min(2)`).         | None (required parameter). |
| `showPrimes`   | `boolean`          | Indicates whether to include prime numbers in the response. If `false` then returns empty array.                                                                                                                 | None.                                   | `false`                    |
| `algorithm`    | `Algorithm` (Enum) | Specifies the algorithm to use for processing. Expected values include:`NAIVE_TRIAL_DIVISION`, `NAIVE_TRIAL_DIVISION_OPTIMISED`, `SIEVE_OF_ERATOSTHENES`, `CONCURRENT_SEGMENTED_SIEVE`, `SEGMENTED_SIEVE_BITSET` | Must be a valid `Algorithm` enum value. | `NAIVE_TRIAL_DIVISION`     |
| `cache`        | `boolean`          | Determines whether to use caching for the operation to improve performance.                                                                                                                                      | None.                                   | `false`                    |
##### Example 1 - Valid Request with JSON response
###### Request
`HTTP GET /api/v1/primes?limit=50`<br>
`-H accept: application/json` 

###### Response
```json
{
  "algorithm": "NAIVE_TRIAL_DIVISION",
  "cache": false,
  "execTimeInNs": 44615,
  "execTimeInMs": 0,
  "timestamp": "2025-10-26T15:40:35.550205426",
  "numOfPrimes": 15,
  "primes": []
}
```
##### Example 2 - Valid Request with XML response
###### Request
`HTTP GET /api/v1/primes?limit=50`<br>
`-H accept: application/xml` 
###### Response 
```xml
<PrimeNumberResponse>
    <algorithm>NAIVE_TRIAL_DIVISION</algorithm>
    <cache>false</cache>
    <execTimeInNs>44615</execTimeInNs>
    <execTimeInMs>0</execTimeInMs>
    <timestamp>2025-10-26T15:40:35.550205426</timestamp>
    <numOfPrimes>15</numOfPrimes>
    <primes/>
</PrimeNumberResponse>
```
##### Example 3 - Invalid Request (missing limit query parameter) with JSON response
###### Request
`HTTP GET /api/v1/primes`<br>
`-H accept: application/xml`
###### Response 
```json
{
    "httpStatus": 400,
    "description": "Required request parameter 'limit' of type 'long' is missing",
    "errorThrownAt": "2025-10-26T15:47:45.433075"
}
```
### Testing Coverage Report - JaCoCo
![img.png](testing-coverage-report.png)