package com.denisneagu.primenumberapi.exception;

public class MemoryConstraintException extends RuntimeException {
    public MemoryConstraintException(String message) {
        super(message);
    }
}
