package com.denisneagu.primenumberapi.dto;

import com.denisneagu.primenumberapi.enums.Algorithm;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDateTime;

@JacksonXmlRootElement
public record PrimeNumberResponse(
        Algorithm algorithm,
        boolean cache,
        long execTimeInNs,
        long execTimeInMs,
        LocalDateTime timestamp,
        int numOfPrimes,
        @JacksonXmlElementWrapper(localName = "primes")
        @JacksonXmlProperty(localName = "prime")
        long[] primes
        ) {
    public PrimeNumberResponse(Algorithm algorithm,
                               boolean cache,
                               long execTimeInNs,
                               long execTimeInMs,
                               int numOfPrimes,
                               long[] primes) {
        this(algorithm, cache, execTimeInNs, execTimeInMs, LocalDateTime.now(), numOfPrimes, primes);
    }
}