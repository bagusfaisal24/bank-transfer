package com.account_bank.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    private ResponseUtil() {
    }

    public static <T> ResponseEntity<Object> build(String message, T data, HttpStatus httpStatus) {
        return new ResponseEntity<>(build(message, data, httpStatus.isError()), httpStatus);
    }

    private static <T> ApiResponse<T> build(String message, T data, boolean error) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .isError(error)
                .build();
    }

}
