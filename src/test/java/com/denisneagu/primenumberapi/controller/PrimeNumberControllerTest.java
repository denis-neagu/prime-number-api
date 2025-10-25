package com.denisneagu.primenumberapi.controller;

import com.denisneagu.primenumberapi.dto.PrimeNumberResponse;
import com.denisneagu.primenumberapi.enums.Algorithm;
import com.denisneagu.primenumberapi.service.PrimeNumberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

@WebMvcTest(PrimeNumberController.class)
public class PrimeNumberControllerTest {

    @MockitoBean
    private PrimeNumberService primeNumberService;

    @Autowired
    private MockMvc mockMvc;

    private final String PRIMES_URL_ENDPOINT = "/api/v1/primes";

    private static Stream<Arguments> whenGetPrimeNumbersTestCases() {
        return Stream.of(
                Arguments.of(10L, Algorithm.NAIVE_TRIAL_DIVISION, false),
                Arguments.of(20L, Algorithm.NAIVE_TRIAL_DIVISION_OPTIMISED, true),
                Arguments.of(30L, Algorithm.SIEVE_OF_ERATOSTHENES, false),
                Arguments.of(40L, Algorithm.CONCURRENT_SEGMENTED_SIEVE, true),
                Arguments.of(40L, Algorithm.SEGMENTED_SIEVE_BITSET, false)
        );
    }

    @ParameterizedTest
    @MethodSource("whenGetPrimeNumbersTestCases")
    void givenValidGetHttpRequest_whenGetPrimeNumbers_thenReturnPrimeNumberResponse(long limit,
                                                                                    Algorithm algorithm,
                                                                                    boolean cache) throws Exception {
        long[] expectedPrimes = switch ((int) limit) {
            case 10 -> new long[]{2L, 3L, 5L, 7L};
            case 20 -> new long[]{2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L};
            case 30 -> new long[]{2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L};
            default -> new long[]{2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L, 31L, 37L};
        };

        PrimeNumberResponse mockResponse = new PrimeNumberResponse(
                algorithm,
                cache,
                0L,
                0L,
                expectedPrimes.length,
                expectedPrimes);

        Mockito
                .when(primeNumberService.getPrimeNumbers(limit, true, algorithm, cache))
                .thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(PRIMES_URL_ENDPOINT)
                                .param("limit", String.valueOf(limit))
                                .param("showPrimes", "true")
                                .param("cache", String.valueOf(cache))
                                .param("algorithm", algorithm.name())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        PrimeNumberResponse response = objectMapper.readValue(jsonResult, PrimeNumberResponse.class);

        Assertions.assertEquals(mockResponse.cache(), response.cache());
        Assertions.assertArrayEquals(mockResponse.primes(), response.primes());
        Assertions.assertEquals(algorithm, response.algorithm());
    }

    @Test
    void givenInvalidLimit_whenGetPrimeNumbers_thenReturnBadRequest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(PRIMES_URL_ENDPOINT)
                                .param("limit", "1")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("$.httpStatus").value(400),
                        MockMvcResultMatchers.jsonPath("$.description")
                                .value("limit must be greater than or equal to 2")
                );
    }

    @Test
    void givenInvalidAlgorithm_whenGetPrimeNumbers_thenReturnBadRequest() throws Exception {
        String invalidAlgorithm = "INVALID_ALGORITHM";
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(PRIMES_URL_ENDPOINT)
                                .param("limit", "10")
                                .param("showPrimes", "true")
                                .param("algorithm", invalidAlgorithm)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("$.httpStatus").value(400),
                        MockMvcResultMatchers
                                .jsonPath("$.description")
                                .value("Invalid value '" + invalidAlgorithm +
                                        "' for parameter 'algorithm'. Expected type: Algorithm.")
                );
    }

    @Test
    void givenUnknownEndpoint_whenRequest_thenReturnNotFoundException() throws Exception {
        String invalidUrl = PRIMES_URL_ENDPOINT + "/INVALID";
        String errorMessage = "Unknown resource for GET " + invalidUrl;
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(invalidUrl)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.httpStatus").value(404),
                        MockMvcResultMatchers.jsonPath("$.description").value(errorMessage)
                );
    }

    @Test
    void givenMissingRequestParameters_whenGetPrimeNumbers_thenReturnBadRequest() throws Exception {
        String errorMessage = "Required request parameter 'limit' of type 'long' is missing";
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(PRIMES_URL_ENDPOINT)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("$.httpStatus").value(400),
                        MockMvcResultMatchers.jsonPath("$.description").value(errorMessage)
                );
    }
}