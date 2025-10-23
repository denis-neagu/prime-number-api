package com.denisneagu.primenumberapi.exception.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDateTime;

@JacksonXmlRootElement
public record ErrorResponse(int httpStatus, String description, LocalDateTime errorThrownAt) {
    public ErrorResponse(int httpStatusValue, String description) {
        this(httpStatusValue, description, LocalDateTime.now());
    }
}