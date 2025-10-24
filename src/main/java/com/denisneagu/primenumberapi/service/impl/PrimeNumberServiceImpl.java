package com.denisneagu.primenumberapi.service.impl;

import com.denisneagu.primenumberapi.dto.PrimeNumberExecutionResponse;
import com.denisneagu.primenumberapi.dto.PrimeNumberResponse;
import com.denisneagu.primenumberapi.enums.Algorithm;
import com.denisneagu.primenumberapi.exception.UnknownAlgorithmException;
import com.denisneagu.primenumberapi.service.AlgorithmService;
import com.denisneagu.primenumberapi.service.CacheService;
import com.denisneagu.primenumberapi.service.PrimeNumberService;
import com.denisneagu.primenumberapi.util.Constant;
import com.denisneagu.primenumberapi.util.PrimeNumberExecution;
import com.denisneagu.primenumberapi.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrimeNumberServiceImpl implements PrimeNumberService {
    private final CacheService cacheService;
    private final AlgorithmService algorithmService;
    private final long[] EMPTY_PRIMES_ARRAY = new long[0];

    private long[] getPrimeNumbersAtAlgorithm(long startAt, long limit, Algorithm algorithm) {
        if (algorithm == null) {
            throw new UnknownAlgorithmException(Constant.UNKNOWN_ALGORITHM);
        }

        switch (algorithm) {
            case NAIVE_TRIAL_DIVISION:
                return algorithmService.getPrimeNumbersUsingNaiveTrialDivision(startAt, limit);
            case NAIVE_TRIAL_DIVISION_OPTIMISED:
                return algorithmService.getPrimeNumbersUsingNaiveTrialDivisionOptimised(startAt, limit);
            default:
                throw new UnknownAlgorithmException(Constant.UNKNOWN_ALGORITHM);
        }
    }

    private PrimeNumberExecutionResponse<long[]> computePrimeNumbers(long startAt, long limit, Algorithm algorithm) {
        return PrimeNumberExecution.getPrimeNumberWithExecutionTime(
                () -> getPrimeNumbersAtAlgorithm(startAt, limit, algorithm));
    }

    @Override
    public PrimeNumberResponse getPrimeNumbers(long limit, boolean showPrimes, Algorithm algorithm, boolean cache) {
        if (cache) {
            return getPrimeNumbersWithCache(limit, showPrimes, algorithm);
        } else {
            PrimeNumberExecutionResponse<long[]> executionResponse = computePrimeNumbers(2, limit, algorithm);

            return new PrimeNumberResponse(
                    algorithm,
                    false,
                    executionResponse.execDurationTimeInNs(),
                    executionResponse.execDurationTimeInMs(),
                    executionResponse.response().length,
                    showPrimes
                            ? executionResponse.response()
                            : EMPTY_PRIMES_ARRAY
            );
        }
    }

    private PrimeNumberResponse getPrimeNumbersWithCache(long limit, boolean showPrimes, Algorithm algorithm) {
        log.info("Existing Cache: {}", Util.formatSizeInMbAndMiB(cacheService.getExistingCache()));

        AtomicBoolean cache = new AtomicBoolean(true);

        PrimeNumberExecutionResponse<long[]> cachedExecutionResponse = PrimeNumberExecution
                .getPrimeNumberWithExecutionTime(
                        () -> cacheService.getCachedPrimeNumbers(limit));

        if (cachedExecutionResponse.response() != null) {
            // cache exists and return cache
            log.info("Caching found for limit: {}", limit);
            return new PrimeNumberResponse(
                    algorithm,
                    cache.get(),
                    cachedExecutionResponse.execDurationTimeInNs(),
                    cachedExecutionResponse.execDurationTimeInMs(),
                    cachedExecutionResponse.response().length,
                    showPrimes
                            ? cachedExecutionResponse.response()
                            : EMPTY_PRIMES_ARRAY);
        }

        // no cached prime number at the specific limit provided
        PrimeNumberExecutionResponse<long[]> totalExecutionResponse = PrimeNumberExecution
                .getPrimeNumberWithExecutionTime(() -> {
                    Map.Entry<Long, long[]> highestCacheEntry = cacheService.getHighestCachedPrimeNumbersEntry();

                    long[] primesToCache;

                    if (highestCacheEntry == null || limit < highestCacheEntry.getKey()) {
                        PrimeNumberExecutionResponse<long[]> executionResponse = computePrimeNumbers(
                                2,
                                limit,
                                algorithm);

                        primesToCache = executionResponse.response();

                        // cache for the first time
                        if (cacheService.isCachingSafe(primesToCache)) {
                            cacheService.addPrimeNumbersToCache(limit, primesToCache);
                            cache.set(false);
                        } else {
                            cacheService.clearCache();
                        }

                        log.info("Caching not found for limit: {}. Computation processed without initial caching", limit);
                    } else {
                        long[] firstHalf = highestCacheEntry.getValue();

                        // start at the existing highest limit
                        PrimeNumberExecutionResponse<long[]> executionResponse = computePrimeNumbers(
                                highestCacheEntry.getKey(),
                                limit,
                                algorithm);

                        long[] secondHalf = executionResponse.response();

                        primesToCache = new long[firstHalf.length + secondHalf.length];

                        System.arraycopy(firstHalf, 0, primesToCache, 0, firstHalf.length);
                        System.arraycopy(secondHalf, 0, primesToCache, firstHalf.length, secondHalf.length);

                        // cache merge results
                        if (cacheService.isCachingSafe(primesToCache)) {
                            cacheService.addPrimeNumbersToCache(limit, primesToCache);
                            cache.set(false);
                        } else {
                            cacheService.clearCache();
                        }
                        log.info("Merge computation was successful for limit: {}", limit);
                    }

                    return primesToCache;
                });

        return new PrimeNumberResponse(
                algorithm,
                cache.get(),
                totalExecutionResponse.execDurationTimeInNs(),
                totalExecutionResponse.execDurationTimeInMs(),
                totalExecutionResponse.response().length,
                showPrimes
                        ? totalExecutionResponse.response()
                        : EMPTY_PRIMES_ARRAY
        );
    }
}