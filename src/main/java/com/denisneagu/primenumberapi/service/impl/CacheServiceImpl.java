package com.denisneagu.primenumberapi.service.impl;

import com.denisneagu.primenumberapi.exception.MemoryConstraintException;
import com.denisneagu.primenumberapi.service.CacheService;
import com.denisneagu.primenumberapi.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final AtomicLong cacheSize = new AtomicLong(0);
    private final Map<Long, long[]> cacheLimitToPrimeNumbers = new ConcurrentHashMap<>();

    private long getNewCacheTotal(long newCacheToAdd) {
        return cacheSize.get() + newCacheToAdd;
    }

    @Override
    public boolean isCachingSafe(long[] primesToCache) {
        long newPrimesSize = primesToCache.length * 8L;

        // fail safely
        // on false indicates to not store to cache, but on calculating the PRT we'll throw an error if the PRT size
        // is larger than the memory we're willing to allocate safely
        try {
            Util.checkMemorySafety(getNewCacheTotal(newPrimesSize));
            return true;
        } catch (MemoryConstraintException ex) {
            log.error("Caching is not safe: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public Map.Entry<Long, long[]> getHighestCachedPrimeNumbersEntry() {
        if (cacheLimitToPrimeNumbers.isEmpty()) {
            return null;
        }

        // find the highest key
        Long highestLimit = Collections.max(cacheLimitToPrimeNumbers.keySet());

        // return the entry
        return Map.entry(highestLimit, cacheLimitToPrimeNumbers.get(highestLimit));
    }

    @Override
    public long[] getCachedPrimeNumbers(long limit) {
        return cacheLimitToPrimeNumbers.get(limit);
    }

    @Override
    public void addPrimeNumbersToCache(long limit, long[] primeNumbers) {
        long newPrimeNumbersSize = primeNumbers.length * 8L;
        log.info("Maximum safe cache size: {}", Util.formatSizeInMbAndMiB(Util.getMaxSafeMemory()));
        log.info("New primes will add {} bytes to our caching. That is: {}",
                newPrimeNumbersSize,
                Util.formatSizeInMbAndMiB(newPrimeNumbersSize));

        if (isCachingSafe(primeNumbers)) {
            cacheLimitToPrimeNumbers.put(limit, primeNumbers);
            cacheSize.set(getNewCacheTotal(newPrimeNumbersSize));
        }
    }

    @Override
    public void clearCache() {
        log.info("Clearing cache ...");
        cacheSize.set(0);
        cacheLimitToPrimeNumbers.clear();
    }

    @Override
    public long getExistingCache() {
        return cacheSize.get();
    }

    @Override
    public Map<Long, long[]> getAllCachedLimitToPrimeNumbers() {
        return cacheLimitToPrimeNumbers;
    }
}