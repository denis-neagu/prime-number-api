package com.denisneagu.primenumberapi.service;

public interface AlgorithmService {
    long[] getPrimeNumbersUsingNaiveTrialDivision(long startAt, long limit);

    long[] getPrimeNumbersUsingNaiveTrialDivisionOptimised(long startAt, long limit);

    long[] getPrimeNumbersUsingSieveOfEratosthenes(long startAt, long limit);
}