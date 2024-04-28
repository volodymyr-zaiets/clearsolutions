package com.example.advice;

import com.example.exception.ConflictException;
import com.example.payload.response.BackendErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BackendErrorResponse> handleException(Exception e) {
        String title = "Internal Server Error";
        logException(title, e.getMessage() + e.getClass());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BackendErrorResponse(title, null, null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BackendErrorResponse> handleNoHandlerFoundException(HttpMessageNotReadableException e) {
        String title = "Message Not Readable";
        logException(title, e.getMessage());
        return ResponseEntity.badRequest()
                .body(new BackendErrorResponse(title, Objects.requireNonNull(e.getRootCause()).getMessage(), null));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BackendErrorResponse> handleNoHandlerFoundException(HttpMediaTypeNotSupportedException e) {
        String title = "Resource Not Found";
        logException(title, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BackendErrorResponse(title, e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BackendErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String title = "Validation Failed";
        Map<String, List<String>> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });
        logException(title, errors.toString());
        return ResponseEntity.badRequest()
                .body(new BackendErrorResponse(title, null, errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BackendErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        String title = "Wrong Data";
        logException(title, e.getMessage());
        return ResponseEntity.badRequest()
                .body(new BackendErrorResponse(title, e.getMessage(), null));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BackendErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        String title = "Not Found";
        logException(title, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BackendErrorResponse(title, e.getMessage(), null));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BackendErrorResponse> handleConflictException(ConflictException e) {
        String title = "Conflict";
        logException(title, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new BackendErrorResponse(title, e.getMessage(), null));
    }

    private void logException(String title, String message) {
        log.error(String.format("%s: %s", title, message));
    }
}
