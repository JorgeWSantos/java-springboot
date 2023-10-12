package br.com.jorgesantos.todolist.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    public Boolean success;
    public String message;
}
