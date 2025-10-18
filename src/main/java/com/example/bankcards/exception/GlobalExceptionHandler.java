package com.example.bankcards.exception;

import com.example.bankcards.exception.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.FORBIDDEN.value(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now())
        );
    }



    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY.value(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(ActivatedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleActivatedException(ActivatedException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY.value(), LocalDateTime.now())
        );
    }


    @ExceptionHandler(FailedTransactionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleFailedTransactionException(FailedTransactionException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY.value(), LocalDateTime.now())
        );
    }


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(ForbiddenTransactionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleForbiddenTransactionException(ForbiddenTransactionException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY.value(), LocalDateTime.now())
        );
    }




}
