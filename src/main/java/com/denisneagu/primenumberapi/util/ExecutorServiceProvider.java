package com.denisneagu.primenumberapi.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Component
public class ExecutorServiceProvider {
    private final ExecutorService executorService;

    public ExecutorServiceProvider() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(availableProcessors);
    }
}
