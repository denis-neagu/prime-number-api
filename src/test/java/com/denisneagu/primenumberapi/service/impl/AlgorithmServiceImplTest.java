package com.denisneagu.primenumberapi.service.impl;

import com.denisneagu.primenumberapi.util.ExecutorServiceProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

public class AlgorithmServiceImplTest {
    AlgorithmServiceImpl service = new AlgorithmServiceImpl();

    @Nested
    class GetPrimeNumbersUsingNaiveTrialDivision {

        @Test
        void givenStartAndLimit_whenGetPrimeNumbersUsingNaive_thenReturnPrimeNumbers() {
            long[] primes = service.getPrimeNumbersUsingNaiveTrialDivision(2L, 20L);
            Assertions.assertArrayEquals(new long[]{2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L}, primes);
        }

        @Test
        void givenSinglePrimeRange_whenGetPrimeNumbersUsingNaive_thenReturnSinglePrime() {
            long[] primes = service.getPrimeNumbersUsingNaiveTrialDivision(13L, 13L);
            Assertions.assertArrayEquals(new long[]{13L}, primes);
        }

        @Test
        void givenSingleNonPrimeRange_whenGetPrimeNumbersUsingNaive_thenReturnEmptyArray() {
            long[] primes = service.getPrimeNumbersUsingNaiveTrialDivision(14L, 14L);
            Assertions.assertArrayEquals(new long[]{}, primes);
        }
    }

    @Nested
    class GetPrimeNumbersUsingOptimisedTrialDivision {

        @Test
        void givenValidHigherStartAtRange_whenGetPrimeNumbersUsingOptimisedNaive_thenReturnPrimes() {
            long[] primes = service.getPrimeNumbersUsingNaiveTrialDivisionOptimised(100L, 105L);
            Assertions.assertArrayEquals(new long[]{101, 103}, primes);
        }

        @Test
        void givenRangeStartingAtEvenNumber_whenGetPrimeNumbersUsingOptimisedNaive_thenSkipEvensCorrectly() {
            long[] primes = service.getPrimeNumbersUsingNaiveTrialDivisionOptimised(6L, 10L);
            Assertions.assertArrayEquals(new long[]{7L}, primes);
        }
    }

    @Nested
    class GetPrimeNumbersUsingSieveOfEratosthenes {

        @Test
        void givenValidRange_whenGetPrimeNumbersUsingSieveOfEratosthenes_thenReturnPrimes() {
            long[] primes = service.getPrimeNumbersUsingSieveOfEratosthenes(2L, 10L);
            Assertions.assertArrayEquals(new long[]{2L, 3L, 5L, 7L}, primes);
        }

        @Test
        void givenValidRangeFromSevenToEight_whenGetPrimeNumbersUsingSieveOfEratosthenes_thenReturnSinglePrime() {
            long[] primes = service.getPrimeNumbersUsingSieveOfEratosthenes(7L, 8L);
            Assertions.assertArrayEquals(new long[]{7L}, primes);
        }

        @Test
        void givenValidRangeFromNineToTen_whenGetPrimeNumbersUsingSieveOfEratosthenes_thenReturnEmptyArray() {
            long[] primes = service.getPrimeNumbersUsingSieveOfEratosthenes(9L, 10L);
            Assertions.assertArrayEquals(new long[]{}, primes);
        }
    }

    @Nested
    class GetPrimeNumbersUsingConcurrentSegmentedSieve {
        private static final ExecutorService executorService = new ExecutorServiceProvider()
                .getExecutorService();

        @Test
        void givenValidRangeFromSevenToEight_GetPrimeNumbersUsingConcurrentSegmentedSieve_thenReturnSinglePrime() {
            long[] primes = service.getPrimeNumbersUsingConcurrentSegmentedSieve(executorService, 7L, 8L);
            Assertions.assertArrayEquals(new long[]{7L}, primes);
        }

        @Test
        void givenValidRangeFromNineToTen_GetPrimeNumbersUsingConcurrentSegmentedSieve_thenReturnEmptyArray() {
            long[] primes = service.getPrimeNumbersUsingConcurrentSegmentedSieve(executorService, 9L, 10L);
            Assertions.assertArrayEquals(new long[]{}, primes);
        }

        @Test
        void givenValidRange_GetPrimeNumbersUsingConcurrentSegmentedSieve_thenReturnPrimeNumbers() {
            long[] primes = service.getPrimeNumbersUsingConcurrentSegmentedSieve(executorService, 2L, 20L);
            Assertions.assertArrayEquals(new long[]{2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L}, primes);
        }

        @AfterAll
        static void shutDown() {
            executorService.shutdown();
        }
    }
}