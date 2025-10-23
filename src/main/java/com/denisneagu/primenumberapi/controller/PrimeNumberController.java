package com.denisneagu.primenumberapi.controller;

import com.denisneagu.primenumberapi.controller.operation.PrimeNumberOperation;
import com.denisneagu.primenumberapi.enums.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PrimeNumberController implements PrimeNumberOperation {

    @Override
    public ResponseEntity<?> getPrimeNumbers(long limit,
                                             Algorithm algorithm,
                                             boolean cache) {
        log.info("Prime numbers finished calculating");
        return null;
    }
}