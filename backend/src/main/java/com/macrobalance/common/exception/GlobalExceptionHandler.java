package com.macrobalance.common.exception;

import com.macrobalance.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex) {
        return new ApiResponse<>(false, ex.getMessage(), null);
    }
}