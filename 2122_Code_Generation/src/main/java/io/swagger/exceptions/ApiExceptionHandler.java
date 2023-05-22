package io.swagger.exceptions;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

//Custom exception handler it does not handle specific exceptions one by one, instead I modified it to handle every exception
//My api exception contains the httpstatus so that I can pass it in here.
@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException exception) {
        ApiException apiException = new ApiException(
                exception.getMessage(),
                exception.getStatus(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException,apiException.getHttpStatus());
    }
}
