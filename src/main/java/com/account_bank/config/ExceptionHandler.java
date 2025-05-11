package com.account_bank.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @Getter
    @Setter
    private static class Response {
        private String error;
        private String message;
        private String path;
        private Integer status;
        private java.sql.Timestamp timestamp;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            DataNotFoundException.class,
    })
    protected ResponseEntity<Object> handleDataNotFound(Exception ex, WebRequest r, HttpServletRequest request) {
        Response body = responseFactory(HttpStatus.NOT_FOUND, ex, ex.getMessage(), request.getRequestURI());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, r);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            InvalidActionException.class
    })
    protected ResponseEntity<Object> badRequest(Exception ex, WebRequest r, HttpServletRequest request) {
        Response body = responseFactory(HttpStatus.BAD_REQUEST, ex, ex.getMessage(), request.getRequestURI());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, r);
    }

    private Response responseFactory(HttpStatus status, Exception e, String message, String path) {
        Response response = new Response();
        response.setError(status.name().toLowerCase());
        response.setMessage(message);
        response.setPath(path);
        response.setStatus(status.value());
        response.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        return response;
    }
}
