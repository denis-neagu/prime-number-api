package com.denisneagu.primenumberapi.service.impl;

import com.denisneagu.primenumberapi.dto.PrimeNumberResponse;
import com.denisneagu.primenumberapi.enums.Algorithm;
import com.denisneagu.primenumberapi.exception.UnknownAlgorithmException;
import com.denisneagu.primenumberapi.util.ExecutorServiceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class PrimeNumbersServiceImplTest {

    @Mock
    private CacheServiceImpl cacheServiceImpl;

    @Mock
    private AlgorithmServiceImpl algorithmServiceImpl;

    @Mock
    private ExecutorServiceProvider executorServiceProvider;

    @Mock
    private ExecutorService executorService;

    private static Stream<Arguments> whenGetPrimeNumbersTestCases() {
        return Stream.of(
                Arguments.of(10L, Algorithm.NAIVE_TRIAL_DIVISION, false),
                Arguments.of(10L, Algorithm.NAIVE_TRIAL_DIVISION, true),
                Arguments.of(20L, Algorithm.NAIVE_TRIAL_DIVISION_OPTIMISED, false),
                Arguments.of(20L, Algorithm.NAIVE_TRIAL_DIVISION_OPTIMISED, true),
                Arguments.of(30L, Algorithm.SIEVE_OF_ERATOSTHENES, false),
                Arguments.of(30L, Algorithm.SIEVE_OF_ERATOSTHENES, true),
                Arguments.of(40L, Algorithm.CONCURRENT_SEGMENTED_SIEVE, false),
                Arguments.of(40L, Algorithm.CONCURRENT_SEGMENTED_SIEVE, true)
        );
    }

    @ParameterizedTest
    @MethodSource("whenGetPrimeNumbersTestCases")
    void givenValidArguments_whenGetPrimeNumbers_thenReturnPrimeNumberResponse(long limit,
                                                                               Algorithm algorithm,
                                                                               boolean cache) {
        long startAt = 2L;
        long[] primeNumbers = new long[]{2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L};

        if (!cache) {
            switch (algorithm) {
                case NAIVE_TRIAL_DIVISION -> Mockito
                        .when(algorithmServiceImpl.getPrimeNumbersUsingNaiveTrialDivision(startAt, limit))
                        .thenReturn(primeNumbers);
                case NAIVE_TRIAL_DIVISION_OPTIMISED -> Mockito
                        .when(algorithmServiceImpl.getPrimeNumbersUsingNaiveTrialDivisionOptimised(startAt, limit))
                        .thenReturn(primeNumbers);
                case SIEVE_OF_ERATOSTHENES -> Mockito
                        .when(algorithmServiceImpl.getPrimeNumbersUsingSieveOfEratosthenes(startAt, limit))
                        .thenReturn(primeNumbers);
                case CONCURRENT_SEGMENTED_SIEVE -> {
                    Mockito.when(executorServiceProvider.getExecutorService())
                            .thenReturn(executorService);
                    Mockito.when(algorithmServiceImpl
                                    .getPrimeNumbersUsingConcurrentSegmentedSieve(executorService, startAt, limit))
                            .thenReturn(primeNumbers);
                }
            }
        }

        if (cache) {
            Mockito.when(cacheServiceImpl.getCachedPrimeNumbers(limit))
                    .thenReturn(primeNumbers);
            Mockito.when(cacheServiceImpl.getExistingCache())
                    .thenReturn(limit);
        }

        PrimeNumberServiceImpl service = new PrimeNumberServiceImpl(
                cacheServiceImpl,
                algorithmServiceImpl,
                executorServiceProvider);

        PrimeNumberResponse response = service.getPrimeNumbers(limit, true, algorithm, cache);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(algorithm, response.algorithm());
        Assertions.assertArrayEquals(primeNumbers, response.primes());
        Assertions.assertEquals(primeNumbers.length, response.numOfPrimes());

        if (cache) {
            Mockito.verify(cacheServiceImpl).getCachedPrimeNumbers(limit);
        } else {
            Mockito.verify(cacheServiceImpl, Mockito.never()).getCachedPrimeNumbers(limit);
        }
    }

    @Test
    void givenNoCacheWithNaiveAlgorithm_whenGetPrimeNumbers_thenComputeAndCache() {
        long startAt = 2L;
        long limit = 11L;
        long[] primeNumbers = new long[]{2L, 3L, 5L, 7L, 11L};

        Mockito.when(cacheServiceImpl.getHighestCachedPrimeNumbersEntry()).thenReturn(null);
        Mockito.when(algorithmServiceImpl.getPrimeNumbersUsingNaiveTrialDivision(startAt, limit)).thenReturn(primeNumbers);
        Mockito.when(cacheServiceImpl.isCachingSafe(primeNumbers)).thenReturn(true);

        PrimeNumberServiceImpl service = new PrimeNumberServiceImpl(
                cacheServiceImpl,
                algorithmServiceImpl,
                executorServiceProvider
        );

        PrimeNumberResponse response = service.getPrimeNumbers(
                limit,
                true,
                Algorithm.NAIVE_TRIAL_DIVISION,
                true);

        Assertions.assertArrayEquals(primeNumbers, response.primes());
        Mockito.verify(cacheServiceImpl).addPrimeNumbersToCache(limit, primeNumbers);
    }

    @Test
    void givenExistingLowerCacheThanLimit_whenGetPrimeNumbers_thenMergeAndCachePrimeNumbers() {
        long limit = 13;
        long[] cachedPrimes = new long[]{2L, 3L, 5L, 7L};
        long[] newPrimeNumbers = new long[]{11L, 13L};
        long[] mergedPrimeNumbers = new long[]{2L, 3L, 5L, 7L, 11L, 13L};

        Mockito.when(cacheServiceImpl.getHighestCachedPrimeNumbersEntry()).thenReturn(Map.entry(10L, cachedPrimes));
        Mockito.when(algorithmServiceImpl.getPrimeNumbersUsingNaiveTrialDivision(10L, limit)).thenReturn(newPrimeNumbers);
        Mockito.when(cacheServiceImpl.isCachingSafe(mergedPrimeNumbers)).thenReturn(true);

        PrimeNumberServiceImpl service = new PrimeNumberServiceImpl(
                cacheServiceImpl,
                algorithmServiceImpl,
                executorServiceProvider
        );

        PrimeNumberResponse response = service.getPrimeNumbers(limit, true, Algorithm.NAIVE_TRIAL_DIVISION, true);

        Assertions.assertArrayEquals(new long[]{2L, 3L, 5L, 7L, 11L, 13L}, response.primes());
        Mockito.verify(cacheServiceImpl).addPrimeNumbersToCache(limit, mergedPrimeNumbers);
    }

    @Test
    void givenNullAlgorithm_whenGetPrimeNumbers_thenThrowUnknownAlgorithmException() {
        PrimeNumberServiceImpl primeNumberService = new PrimeNumberServiceImpl(
                cacheServiceImpl,
                algorithmServiceImpl,
                executorServiceProvider);
        Assertions.assertThrows(
                UnknownAlgorithmException.class,
                () -> {primeNumberService.getPrimeNumbers(2L, true, null, false);});
    }
}
