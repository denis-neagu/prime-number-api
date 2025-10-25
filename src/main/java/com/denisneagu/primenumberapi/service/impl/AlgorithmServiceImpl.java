package com.denisneagu.primenumberapi.service.impl;

import com.denisneagu.primenumberapi.service.AlgorithmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class AlgorithmServiceImpl implements AlgorithmService {

    private int getArrSizeUsingPrimeNumberTheorem(long limit) {
        // limit should never be 1 due hibernate validations on the request parameter, but just in-case
        if (limit <= 1) {
            throw new IllegalArgumentException("Limit can't be less than or equal to 1 when calculating PNT");
        }

        // 1.3 buffer
        long limitSize = (long) (limit / Math.log(limit) * 1.3);

        // if there's more numbers than the Integer max limit then we'll throw an error to avoid an overflow.
        if (limitSize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too many prime numbers to fit within an array");
        }

        return (int) limitSize;
    }

    private boolean isPrime(long num) {
        for (long i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long[] getPrimeNumbersUsingNaiveTrialDivision(long startAt, long limit) {
        // ensure startAt is at least 2
        startAt = Math.max(startAt, 2);

        int estimatedSize = getArrSizeUsingPrimeNumberTheorem(limit);

        long[] primeNumbers = new long[estimatedSize];

        int index = 0;

        for (long i = startAt; i <= limit; i++) {
            if (isPrime(i)) {
                primeNumbers[index++] = i;
            }
        }

        return Arrays.copyOf(primeNumbers, index);
    }

    private boolean isPrimeOptimized(long num) {
        if (num <= 1) {
            return false;
        }
        if (num == 2) {
            return true;
        }
        // skip even numbers
        if (num % 2 == 0) {
            return false;
        }

        // only check odd divisors to cut the number of checks roughly in half
        for (long i = 3; i * i <= num; i = i + 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    // skip even numbers and only check odd numbers if they're prime
    @Override
    public long[] getPrimeNumbersUsingNaiveTrialDivisionOptimised(long startAt, long limit) {
        // ensure startAt is at least 2
        startAt = Math.max(startAt, 2);

        int estimatedSize = getArrSizeUsingPrimeNumberTheorem(limit);

        long[] primeNumbers = new long[estimatedSize];

        int index = 0;

        for (long i = startAt; i <= limit; i++) {
            if (isPrimeOptimized(i)) {
                primeNumbers[index++] = i;
            }
        }

        return Arrays.copyOf(primeNumbers, index);
    }

    @Override
    public long[] getPrimeNumbersUsingSieveOfEratosthenes(long startAt, long limit) {
        if (startAt >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("startAt is larger than Integer max value in Sieve of Eratosthenes");
        }

        // ensure startAt is at least 2
        int start = (int) Math.max(startAt, 2);

        // this should never overflow, we should have caught it earlier on the input size validations
        int n = (int) limit;
        boolean[] isPrime = new boolean[n + 1];

        // assume all numbers are prime until proven otherwise
        for (int i = 0; i < n; i++) {
            isPrime[i] = true;
        }

        // aggregate only through possible composite numbers, composite number must have at least one factor <= n
        for (int p = 2; p * p < n; p++) {
            // if previously already marked as false because it's a multiple of p, we don't need to check
            if (isPrime[p]) {
                // mark all multiples of p as composite, starting from p^2
                // because smaller multiples have already been marked by smaller primes to avoid marking the same prime twice
                for (int i = p * p; i < n; i = i + p) {
                    isPrime[i] = false;
                }
            }
        }

        // count primes
        int count = 0;

        // we don't need to start at 0 through the array. if merge happened then those numbers are already calculated.
        for (int i = start; i < n; i++) {
            if (isPrime[i]) {
                count++;
            }
        }

        // store primes in result array
        long[] result = new long[count];

        int index = 0;

        // add prime numbers
        for (int i = start; i < n; i++) {
            if (isPrime[i]) {
                result[index] = i;
                index++;
            }
        }

        return result;
    }

    private boolean[] sieve(long limit) {
        if (limit >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Limit is too large for an Integer array capacity");
        }

        boolean[] isPrime = new boolean[(int) (limit + 1)];

        Arrays.fill(isPrime, true);

        isPrime[0] = false;
        isPrime[1] = false;

        for (int p = 2; (long) p * p <= limit; p++) {
            if (isPrime[p]) {
                // set multiples of p to false indicating a non-prime number
                for (int j = p * p; j <= limit; j += p) {
                    isPrime[j] = false;
                }
            }
        }

        return isPrime;
    }
}