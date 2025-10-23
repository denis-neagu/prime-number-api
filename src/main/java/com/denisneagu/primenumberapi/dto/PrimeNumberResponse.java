package com.denisneagu.primenumberapi.dto;

import com.denisneagu.primenumberapi.enums.Algorithm;

import java.time.LocalDateTime;

public record PrimeNumberResponse(
        Algorithm algorithm,
        boolean cache,
        long execTimeInNs,
        long execTimeInMs,
        LocalDateTime timestamp,
        long[] primes
        ) {
}