package com.example.emloyee.management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp;

    private final String message;

    private final HttpStatusCode statusCode;

    private final String path;
}
