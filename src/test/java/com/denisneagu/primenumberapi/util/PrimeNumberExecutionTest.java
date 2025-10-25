package com.denisneagu.primenumberapi.util;

import com.denisneagu.primenumberapi.dto.PrimeNumberExecutionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class PrimeNumberExecutionTest {

    @Test
    void givenExceptionIsThrown_whenGetPrimeNumberWithExecutionTime_thenReturnException() {
        Supplier<Void> fn = () -> {
            throw new RuntimeException("Something went wrong");
        };
        Assertions.assertThrows(RuntimeException.class, () -> {
            PrimeNumberExecution.getPrimeNumberWithExecutionTime(fn);
        });
    }

    @Test
    void givenValidCallback_whenGetPrimeNumberWithExecutionTime_thenReturnExpectedCallback() {
        Supplier<String> fn = () -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return "Callback response";
        };

        PrimeNumberExecutionResponse<String> executionResponse = PrimeNumberExecution.getPrimeNumberWithExecutionTime(fn);
        Assertions.assertEquals("Callback response",executionResponse.response());
        Assertions.assertTrue(executionResponse.execDurationTimeInNs() >= 50_000_000,
                "Execution time in ns should be >= 50_000_000");
        Assertions.assertTrue(executionResponse.execDurationTimeInMs() >= 50,
                "Execution time in ms should be >= 50");
    }
}
