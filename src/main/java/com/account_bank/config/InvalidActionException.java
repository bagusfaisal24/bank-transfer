package com.account_bank.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Data Not Found")
@NoArgsConstructor
public class InvalidActionException extends RuntimeException {
    @Getter
    @Setter
    private String message = "Data Not Found";

    public InvalidActionException(String message) {
        super();
        this.message = message;
    }
}
