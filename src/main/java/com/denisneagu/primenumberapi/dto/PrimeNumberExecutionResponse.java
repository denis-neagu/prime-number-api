package com.denisneagu.primenumberapi.dto;

public record PrimeNumberExecutionResponse<T>(T response, long execDurationTimeInNs, long execDurationTimeInMs) {
}