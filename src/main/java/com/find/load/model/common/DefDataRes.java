package com.find.load.model.common;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DefDataRes extends DefRes {
    private Object data;

    public DefDataRes(HttpStatus status, Object data) {
        super(status);
        this.data = data;
    }
}
