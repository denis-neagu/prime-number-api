package com.denisneagu.primenumberapi.service;

import com.denisneagu.primenumberapi.dto.PrimeNumberResponse;
import com.denisneagu.primenumberapi.enums.Algorithm;

public interface PrimeNumberService {
    PrimeNumberResponse getPrimeNumbers(long limit, boolean showPrimes, Algorithm algorithm, boolean cache);
}