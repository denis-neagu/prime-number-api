package com.denisneagu.primenumberapi.controller.operation;

import com.denisneagu.primenumberapi.enums.Algorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = {
                                    @Content(mediaType = "application/json", schema = @Schema()),
                                    @Content(mediaType = "application/xml", schema = @Schema())
                            }),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @Parameters(
            value = {
                    @Parameter(
                            name = "limit",
                            description = "Inclusive upper limit",
                            required = true,
                            in = ParameterIn.QUERY),
                    @Parameter(
                            name = "algorithm",
                            description = "Algorithm to calculate prime numbers",
                            required = false,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "cache",
                            description = "Flag whether to use cache or not",
                            required = false,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @GetMapping(path = "/primes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<?> getPrimeNumbers(
            @RequestParam(name = "limit") @Size(min = 2) long limit,
            @RequestParam(name = "algorithm", defaultValue = "NAIVE_TRIAL_DIVISION") Algorithm algorithm,
            @RequestParam(name = "cache", defaultValue = "false") boolean cache);
}