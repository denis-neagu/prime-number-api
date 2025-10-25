package com.denisneagu.primenumberapi.service.impl;

import com.denisneagu.primenumberapi.exception.MemoryConstraintException;
import com.denisneagu.primenumberapi.util.Util;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;

public class CacheServiceImplTest {

    @Test
    void givenSafeCaching_whenAddPrimeNumbersToCache_thenPrimesAreAdded() {
        CacheServiceImpl cacheService = new CacheServiceImpl();
        long[] primes = {2L, 3L, 5L, 7L};

        cacheService.addPrimeNumbersToCache(10L, primes);

        assertThat(cacheService.getCachedPrimeNumbers(10L)).containsExactly(2L, 3L, 5L, 7L);
        assertThat(cacheService.getAllCachedLimitToPrimeNumbers().get(10L)).containsExactly(2L, 3L, 5L, 7L);
        assertThat(cacheService.getExistingCache()).isEqualTo(32L);
    }

    @Test
    void givenNotEnoughSafeCache_whenAddPrimeNumbersToCache_thenPrimesAreNotAddedToCache() {
        try (MockedStatic<Util> utilMock = Mockito.mockStatic(Util.class)) {
            utilMock.when(() -> Util.checkMemorySafety(anyLong()))
                    .thenThrow(new MemoryConstraintException("Insufficient memory"));

            CacheServiceImpl cacheService = new CacheServiceImpl();
            long[] primes = {2L, 3L, 5L, 7L};

            cacheService.addPrimeNumbersToCache(10L, primes);

            assertThat(cacheService.getCachedPrimeNumbers(10L)).isNull();
            assertThat(cacheService.getAllCachedLimitToPrimeNumbers()).isEmpty();
            assertThat(cacheService.getExistingCache()).isEqualTo(0L);
        }
    }

    @Test
    void givenCacheReset_whenClearCache_thenReturnEmptyCache() {
        CacheServiceImpl cacheService = new CacheServiceImpl();
        long[] primes1 = {2L, 3L, 5L, 7L};
        cacheService.addPrimeNumbersToCache(10L, primes1);

        assertThat(cacheService.getExistingCache()).isGreaterThan(0);
        assertThat(cacheService.getAllCachedLimitToPrimeNumbers()).isNotEmpty();

        cacheService.clearCache();

        assertThat(cacheService.getExistingCache()).isEqualTo(0);
        assertThat(cacheService.getAllCachedLimitToPrimeNumbers()).isEmpty();
        assertThat(cacheService.getCachedPrimeNumbers(10L)).isNull();
    }

    @Test
    void givenEmptyCache_whenGetHighestCachedPrimeNumbersEntry_thenReturnNull() {
        CacheServiceImpl cacheService = new CacheServiceImpl();

        Map.Entry<Long, long[]> result = cacheService.getHighestCachedPrimeNumbersEntry();

        assertThat(result).isNull();
    }

    @Test
    void givenCacheHasEntries_whenGetHighestCachedPrimeNumbersEntry_thenReturnHighestEntry() {
        CacheServiceImpl cacheService = new CacheServiceImpl();

        long[] primes1 = {2L, 3L, 3L, 7L};
        long[] primes2 = {2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L};

        cacheService.addPrimeNumbersToCache(10L, primes1);
        cacheService.addPrimeNumbersToCache(20L, primes2);

        Map.Entry<Long, long[]> result = cacheService.getHighestCachedPrimeNumbersEntry();

        assertThat(result).isNotNull();
        assertThat(result.getKey()).isEqualTo(20L);
        assertThat(result.getValue()).containsExactly(2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L);
    }
}