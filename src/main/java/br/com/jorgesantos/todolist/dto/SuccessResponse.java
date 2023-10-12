package br.com.jorgesantos.todolist.dto;

import lombok.Data;

@Data
public class SuccessResponse<T> {
    public Boolean success;
    public T data;
}
