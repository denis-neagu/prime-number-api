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
}