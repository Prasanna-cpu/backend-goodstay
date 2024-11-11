package com.spring.backend.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MisappropriateDatesException extends RuntimeException {
  public MisappropriateDatesException(String message) {
    super(message);
  }
}
