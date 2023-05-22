package io.swagger.exceptions;

import org.springframework.http.HttpStatus;

//If you have questions about using ask me {egehan}
public class ApiRequestException extends RuntimeException {

    //This is the class used to throw exceptions.
    // HOW TO USE:
    private HttpStatus status;

    public ApiRequestException(String message, HttpStatus status) {
        super(message);
        this.status=status;
    }

    public HttpStatus getStatus()
    {
        return  this.status;
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
