package com.example.storeproject.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomException(CustomException e) {

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

}
