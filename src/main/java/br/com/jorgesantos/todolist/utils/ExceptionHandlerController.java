package br.com.jorgesantos.todolist.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.jorgesantos.todolist.dto.ErrorResponse;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableExcption(HttpMessageNotReadableException e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMostSpecificCause().getMessage());
        error.setSuccess(false);

        return ResponseEntity.status(400).body(error);
    }
}
