package com.denisneagu.primenumberapi.util;

import com.denisneagu.primenumberapi.dto.PrimeNumberExecutionResponse;

import java.util.function.Supplier;

public class PrimeNumberExecution {
    public static <T> PrimeNumberExecutionResponse<T> getPrimeNumberWithExecutionTime(Supplier<T> fn) {
        long execStartTimeInMs = System.currentTimeMillis();
        long execStartTimeInNs = System.nanoTime();

        T result = fn.get();

        long execDurationTimeInNs = System.nanoTime() - execStartTimeInNs;
        long execDurationTimeInMs = System.currentTimeMillis() - execStartTimeInMs;

        return new PrimeNumberExecutionResponse<>(result, execDurationTimeInNs, execDurationTimeInMs);
    }
}