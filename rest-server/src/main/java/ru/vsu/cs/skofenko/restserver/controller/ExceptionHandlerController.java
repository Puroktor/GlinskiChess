package ru.vsu.cs.skofenko.restserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.vsu.cs.skofenko.restserver.controller.dto.ErrorDto;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handleRuntimeException(RuntimeException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto handleRuntimeException(NoSuchElementException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorDto handleRuntimeException(IllegalArgumentException e) {
        return new ErrorDto(e.getMessage());
    }
}
