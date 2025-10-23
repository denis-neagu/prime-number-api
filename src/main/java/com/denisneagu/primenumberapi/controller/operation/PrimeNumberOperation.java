package com.denisneagu.primenumberapi.controller.operation;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/api/v1")
public interface PrimeNumberOperation {

    @Operation(
            summary = "Get all prime numbers up to and including the limit",
            description = """
                    Calculates and returns all prime numbers up to the given limit.
                    Supports optional selection of the algorithm selected and includes the time taken for computation.
                    """
    )
    @GetMapping(path = "/primes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<?> getPrimeNumbers();
}
