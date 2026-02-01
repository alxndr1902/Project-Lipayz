package com.zezame.lipayz.exceptiohandler;

import com.zezame.lipayz.dto.ErrorResDTO;
import com.zezame.lipayz.exceptiohandler.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleDataNotFoundException(NotFoundException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> handleOptimisticLockException(OptimisticLockException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        var message = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(message), httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getAllErrors().stream()
                .map((ObjectError oe) -> oe.getDefaultMessage()).toList();
        return new ResponseEntity<>(new ErrorResDTO<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUUIDException.class)
    public ResponseEntity<?> handleUUIDNotValidException(InvalidUUIDException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
         var errorMessage = e.getMessage();

         return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> handleDuplicateException(DuplicateException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        var errorMessage = "Method Is Not Found/Request Body Is Not Valid";

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        var errorMessage = "Invalid Request Parameter";

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException e) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(InvalidSortFieldException.class)
    public ResponseEntity<?> handleInvalidSortFieldException(InvalidSortFieldException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }

    @ExceptionHandler(InvalidActionException.class)
    public ResponseEntity<?> handleInvalidActionException(InvalidActionException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        var errorMessage = e.getMessage();

        return new ResponseEntity<>(new ErrorResDTO<>(errorMessage), httpStatus);
    }
}
