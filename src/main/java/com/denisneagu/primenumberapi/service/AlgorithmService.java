package com.denisneagu.primenumberapi.service;

import java.util.concurrent.ExecutorService;

public interface AlgorithmService {
    long[] getPrimeNumbersUsingNaiveTrialDivision(long startAt, long limit);

    long[] getPrimeNumbersUsingNaiveTrialDivisionOptimised(long startAt, long limit);

    long[] getPrimeNumbersUsingSieveOfEratosthenes(long startAt, long limit);

    long[] getPrimeNumbersUsingConcurrentSegmentedSieve(ExecutorService executorService,
                                                        long startAt,
                                                        long limit);

    long[] getPrimeNumbersUsingSegmentedSieveBitset(long startAt, long limit);
}