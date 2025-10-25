package com.denisneagu.primenumberapi.exception;

import com.denisneagu.primenumberapi.exception.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void givenMethodArgumentTypeMismatchException_whenHandleMethodArgumentTypeMismatchException_thenReturnBadRequest() {
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc",
                Long.class,
                "limit",
                parameter,
                null
        );

        ErrorResponse response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        Assertions.assertEquals(400, response.httpStatus());
        Assertions.assertEquals(
                "Invalid value 'abc' for parameter 'limit'. Expected type: Long.",
                response.description()
        );
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenMissingServletRequestParameterException_whenHandleMissingServletRequestParameterException_thenReturnBadRequest() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("limit", "long");

        ErrorResponse response = globalExceptionHandler.handleMissingServletRequestParameterException(ex);

        Assertions.assertEquals(400, response.httpStatus());
        Assertions.assertEquals("Required request parameter 'limit' of type 'long' is missing", response.description());
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenIllegalLimitStateToAlgorithmException_whenHandleIllegalLimitStateToAlgorithmException_thenReturnBadRequest() {
        IllegalLimitStateToAlgorithmException ex =
                new IllegalLimitStateToAlgorithmException("Limit cannot be negative for this algorithm");

        ErrorResponse response = globalExceptionHandler.handleIllegalLimitStateToAlgorithmException(ex);

        Assertions.assertEquals(422, response.httpStatus());
        Assertions.assertEquals("Limit cannot be negative for this algorithm", response.description());
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenUnknownAlgorithmException_whenHandleUnknownAlgorithmException_thenReturnBadRequest() {
        UnknownAlgorithmException ex = new UnknownAlgorithmException("Algorithm is unknown");

        ErrorResponse response = globalExceptionHandler.handleUnknownAlgorithmException(ex);

        Assertions.assertEquals(400, response.httpStatus());
        Assertions.assertEquals("Algorithm is unknown", response.description());
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenConstraintViolationException_whenHandleConstraintViolationException_thenReturnBadRequest() {
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);

        Mockito.when(path.toString()).thenReturn("limit");
        Mockito.when(violation.getPropertyPath()).thenReturn(path);
        Mockito.when(violation.getMessage()).thenReturn("must be greater than 0");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ErrorResponse response = globalExceptionHandler.handleConstraintViolationException(ex);

        Assertions.assertEquals(400, response.httpStatus());
        Assertions.assertEquals("limit must be greater than 0", response.description());
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenNoResourceFoundException_whenHandleNoResourceFoundException_thenReturnNotFoundException() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "primes/123");

        ErrorResponse response = globalExceptionHandler.handleNoResourceFoundException(ex);

        Assertions.assertEquals(404, response.httpStatus());
        Assertions.assertEquals("Unknown resource for GET /primes/123", response.description());
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenUnknownException_whenHandleUnknownException_thenReturnInternalServerError() {
        Exception ex = new RuntimeException("Something went wrong");

        ErrorResponse response = globalExceptionHandler.handleUnknownException(ex);

        Assertions.assertEquals(500, response.httpStatus());
        Assertions.assertEquals(
                "Our backend is non-functional. Please try again later.",
                response.description()
        );
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenMemoryConstraintException_whenHandleMemoryConstraintException_thenReturnInsufficientStorage() {
        MemoryConstraintException ex = new MemoryConstraintException("Memory limit exceeded");

        ErrorResponse response = globalExceptionHandler.handleMemoryConstraintException(ex);

        Assertions.assertEquals(507, response.httpStatus());
        Assertions.assertEquals("Memory limit exceeded", response.description());
        Assertions.assertNotNull(response.errorThrownAt());
    }

    @Test
    void givenMethodArgumentTypeMismatchExceptionWithNullRequiredType_whenHandleMethodArgumentTypeMismatchException_thenReturnBadRequestWithUnknownType() {
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc",
                null,
                "limit",
                parameter,
                null
        );

        ErrorResponse response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        Assertions.assertEquals(400, response.httpStatus());
        Assertions.assertEquals(
                "Invalid value 'abc' for parameter 'limit'. Expected type: unknown.",
                response.description()
        );
        Assertions.assertNotNull(response.errorThrownAt());
    }
}