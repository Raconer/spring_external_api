package com.find.load.model.common;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class DefRes {
    private int code;
    private String message;

    public DefRes(HttpStatus status) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
    }
}
