package com.example.emloyee.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> onResourceNotFound(HttpServletRequest request,
                                                            ResourceNotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(LocalDateTime.now(), exception.getMessage(), NOT_FOUND, request.getServletPath()));
    }

    @ExceptionHandler(NoUpdateException.class)
    public ResponseEntity<ErrorResponse> onNoUpdate(HttpServletRequest request,
                                                    NoUpdateException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(), exception.getMessage(), BAD_REQUEST, request.getServletPath()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> onResourceDuplicate(HttpServletRequest request,
                                                             DuplicateResourceException exception) {
        return ResponseEntity.status(CONFLICT)
                .body(new ErrorResponse(LocalDateTime.now(), exception.getMessage(), CONFLICT, request.getServletPath()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentTypeMismatch(HttpServletRequest request,
                                                                      MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(), exception.getMessage(), BAD_REQUEST, request.getServletPath()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> onHttpMessageNotReadable(HttpServletRequest request,
                                                                  HttpMessageNotReadableException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(), exception.getMessage(), BAD_REQUEST, request.getServletPath()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentNotValid(HttpServletRequest request,
                                                                  MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String message = makeErrorMessage(fieldErrors);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(), message, BAD_REQUEST, request.getServletPath()));
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ErrorResponse> onPSQLException(HttpServletRequest request,
                                                         PSQLException exception) {

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(), exception.getMessage(), BAD_REQUEST, request.getServletPath()));
    }

    private String makeErrorMessage(List<FieldError> fieldErrors) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : fieldErrors) {
            builder.append("Field: ").append(error.getField())
                    .append(" has invalid value: ")
                    .append(error.getRejectedValue())
                    .append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }
}