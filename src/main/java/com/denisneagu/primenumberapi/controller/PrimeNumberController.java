package com.denisneagu.primenumberapi.controller;

import com.denisneagu.primenumberapi.controller.operation.PrimeNumberOperation;
import com.denisneagu.primenumberapi.dto.PrimeNumberResponse;
import com.denisneagu.primenumberapi.enums.Algorithm;
import com.denisneagu.primenumberapi.service.PrimeNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PrimeNumberController implements PrimeNumberOperation {
    private final PrimeNumberService primeNumberService;

    @Override
    public ResponseEntity<PrimeNumberResponse> getPrimeNumbers(long limit,
                                                               boolean showPrimes,
                                                               Algorithm algorithm,
                                                               boolean cache) {
        PrimeNumberResponse primeNumberResponse = primeNumberService.getPrimeNumbers(limit, showPrimes, algorithm, cache);
        log.info("Finished calculating primes with limit: {}", limit);
        return ResponseEntity.ok(primeNumberResponse);
    }
}