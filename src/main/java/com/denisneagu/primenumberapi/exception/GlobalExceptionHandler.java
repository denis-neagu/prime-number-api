package com.denisneagu.primenumberapi.exception;

import com.denisneagu.primenumberapi.exception.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Method argument type mismatch exception thrown: {}", ex.getMessage());

        String paramName = ex.getName();
        Object invalidValue = ex.getValue();
        Class<?> requiredType = ex.getRequiredType();

        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s.",
                invalidValue,
                paramName,
                requiredType != null
                        ? requiredType.getSimpleName()
                        : "unknown"
        );

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("Missing servlet request parameter exception: {}", ex.getMessage());
        String message = String.format(
                "Required request parameter '%s' of type '%s' is missing",
                ex.getParameterName(),
                ex.getParameterType());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(UnknownAlgorithmException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownAlgorithmException(UnknownAlgorithmException ex) {
        log.error("Unknown algorithm exception thrown: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation exception thrown: {}", ex.getMessage());

        String message = ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> {
                    String paramName = constraintViolation.getPropertyPath().toString();

                    int dotIndex = paramName.indexOf('.');

                    if (dotIndex >= 0 && dotIndex < paramName.length() - 1) {
                        paramName = paramName.substring(dotIndex + 1);
                    }

                    return paramName + " " + constraintViolation.getMessage();
                })
                .collect(Collectors.joining(", "));

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("No resource found exception: {}", ex.getMessage());
        String message = String.format("Unknown resource for %s /%s", ex.getHttpMethod(), ex.getResourcePath());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(MemoryConstraintException.class)
    @ResponseStatus(value = HttpStatus.INSUFFICIENT_STORAGE)
    public ErrorResponse handleMemoryConstraintException(MemoryConstraintException ex) {
        log.error("Memory constraint exception: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.INSUFFICIENT_STORAGE.value(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknownException(Exception ex) {
        log.error("Unexpected exception: {}, message: {}", ex.getClass().getCanonicalName(), ex.getMessage());
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Our backend is non-functional. Please try again later."
        );
    }
}