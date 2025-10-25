package com.denisneagu.primenumberapi.integration;

import com.denisneagu.primenumberapi.dto.PrimeNumberResponse;
import com.denisneagu.primenumberapi.enums.Algorithm;
import com.denisneagu.primenumberapi.exception.dto.ErrorResponse;
import com.denisneagu.primenumberapi.service.CacheService;
import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PrimeNumberControllerIntegrationTest {

    @MockitoSpyBean
    private CacheService cacheService;

    @LocalServerPort
    private int portRunningAt;

    @BeforeEach
    void setUp() {
        cacheService.clearCache();
        RestAssured.port = portRunningAt;
    }

    private final long[] PRIME_NUMBERS_UP_TO_150 = new long[]{
            2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L,
            31L, 37L, 41L, 43L, 47L, 53L, 59L, 61L, 67L, 71L,
            73L, 79L, 83L, 89L, 97L, 101L, 103L, 107L, 109L, 113L,
            127L, 131L, 137L, 139L, 149L
    };

    private static Stream<Arguments> getPrimeNumbersTestcases() {
        return Stream.of(
                Arguments.of(Algorithm.NAIVE_TRIAL_DIVISION),
                Arguments.of(Algorithm.NAIVE_TRIAL_DIVISION_OPTIMISED),
                Arguments.of(Algorithm.SIEVE_OF_ERATOSTHENES),
                Arguments.of(Algorithm.CONCURRENT_SEGMENTED_SIEVE),
                Arguments.of(Algorithm.SEGMENTED_SIEVE_BITSET)
        );
    }

    @ParameterizedTest
    @MethodSource("getPrimeNumbersTestcases")
    void givenValidRequestParametersWithCache_whenGetPrimeNumbers_thenReturnCachedPrimeNumbers(Algorithm algorithm) {
        PrimeNumberResponse primeNumberFirstResponse = RestAssured
                .given()
                .queryParam("cache", true)
                .queryParam("showPrimes", true)
                .queryParam("limit", 150)
                .queryParam("algorithm", algorithm)
                .when()
                .get("/api/v1/primes")
                .then()
                .statusCode(200)
                .extract()
                .as(PrimeNumberResponse.class);

        Assertions.assertArrayEquals(PRIME_NUMBERS_UP_TO_150, primeNumberFirstResponse.primes());
        Assertions.assertFalse(primeNumberFirstResponse.cache());
        Assertions.assertEquals(algorithm, primeNumberFirstResponse.algorithm());

        PrimeNumberResponse primeNumberSecondResponse = RestAssured
                .given()
                .queryParam("cache", true)
                .queryParam("showPrimes", true)
                .queryParam("limit", 150)
                .queryParam("algorithm", algorithm)
                .when()
                .get("/api/v1/primes")
                .then()
                .statusCode(200)
                .extract()
                .as(PrimeNumberResponse.class);

        Assertions.assertArrayEquals(PRIME_NUMBERS_UP_TO_150, primeNumberSecondResponse.primes());
        Assertions.assertTrue(primeNumberSecondResponse.cache());
        Assertions.assertEquals(algorithm, primeNumberSecondResponse.algorithm());

        Mockito.verify(cacheService, Mockito.times(2)).getCachedPrimeNumbers(Mockito.anyLong());
        Mockito.verify(cacheService, Mockito.times(1))
                .addPrimeNumbersToCache(Mockito.anyLong(), Mockito.any(long[].class));
    }

    @Test
    void givenValidRequestParametersAndNoCache_whenGetPrimeNumbers_thenReturnUncachedPrimeNumbers() {
        PrimeNumberResponse primeNumberResponse  = RestAssured
                .given()
                .queryParam("cache", false)
                .queryParam("showPrimes", true)
                .queryParam("limit", 150)
                .queryParam("algorithm", Algorithm.NAIVE_TRIAL_DIVISION)
                .when()
                .get("/api/v1/primes")
                .then()
                .statusCode(200)
                .extract()
                .as(PrimeNumberResponse.class);

        Assertions.assertArrayEquals(PRIME_NUMBERS_UP_TO_150, primeNumberResponse.primes());
        Assertions.assertFalse(primeNumberResponse.cache());
        Assertions.assertEquals(Algorithm.NAIVE_TRIAL_DIVISION, primeNumberResponse.algorithm());

        Mockito.verify(cacheService, Mockito.never()).getCachedPrimeNumbers(Mockito.anyLong());
        Mockito.verify(cacheService, Mockito.never()).addPrimeNumbersToCache(Mockito.anyLong(), Mockito.any(long[].class));
    }

    @Test
    void givenValidRequestParametersWithXmlAcceptHeader_whenGetPrimeNumbers_thenReturnPrimeNumbersInXml() {
        Response primeNumberResponse  = RestAssured
                .given()
                .queryParam("cache", false)
                .queryParam("showPrimes", "true")
                .queryParam("limit", 150)
                .queryParam("algorithm", Algorithm.NAIVE_TRIAL_DIVISION)
                .header("Accept", "application/xml")
                .when()
                .get("/api/v1/primes");

        Assertions.assertTrue(primeNumberResponse.getContentType().contains(("application/xml")));
        Assertions.assertEquals(200, primeNumberResponse.getStatusCode());
        Assertions.assertTrue(primeNumberResponse.body().asString().contains(("<PrimeNumberResponse>")));

        XmlPath xml = new XmlPath(primeNumberResponse.asString());
        List<Long> primes = xml.getList("PrimeNumberResponse.primes.prime", Long.class);
        Assertions.assertArrayEquals(PRIME_NUMBERS_UP_TO_150, primes.stream().mapToLong(i -> i).toArray());
    }

    @Test
    void givenInvalidLimitRequestParameter_whenGetPrimeNumbers_thenReturnErrorResponse() {
        ErrorResponse errorResponse = RestAssured
                .given()
                .queryParam("limit", 1)
                .when()
                .get("/api/v1/primes")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        Assertions.assertEquals("limit must be greater than or equal to 2", errorResponse.description());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.httpStatus());
    }

    @Test
    void givenInvalidUri_whenGetPrimeNumbers_thenReturnErrorResponse() {
        ErrorResponse errorResponse = RestAssured
                .when()
                .get("/api/v1/primes/INVALID")
                .then()
                .statusCode(404)
                .extract()
                .as(ErrorResponse.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.httpStatus());
        Assertions.assertEquals("Unknown resource for GET /api/v1/primes/INVALID", errorResponse.description());
    }

    @Test
    void givenLimitTooBig_whenGetPrimeNumbers_thenReturnErrorResponse() {
        ErrorResponse errorResponse = RestAssured
                .given()
                .queryParam("limit", 70_000_000_000L)
                .when()
                .get("/api/v1/primes")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.httpStatus());
        Assertions.assertEquals("Too many prime numbers to fit within an array", errorResponse.description());
    }
}
