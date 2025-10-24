package com.denisneagu.primenumberapi.service;

import java.util.Map;

public interface CacheService {
    Map.Entry<Long, long[]> getHighestCachedPrimeNumbersEntry();
    Map<Long, long[]> getAllCachedLimitToPrimeNumbers();
    long[] getCachedPrimeNumbers(long limit);
    long getExistingCache();
    boolean isCachingSafe(long[] primesToCache);
    void addPrimeNumbersToCache(long limit, long[] primeNumbers);
    void clearCache();
}
