package com.challenge.contact.Exception;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(e->e.getField().toString()+" : "+e.getDefaultMessage()).collect(Collectors.toList());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Map<String, List<String>>> handleGeneralExceptions(Exception ex) {
        logger.error("Exception: " + ex.getMessage());
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, List<String>>> handleNotFoundException(NotFoundException ex) {
        logger.error("Not found exception: " + ex.getMessage());
        return new ResponseEntity<>(getResponse(ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(SaveContactException.class)
    public ResponseEntity<Map<String, List<String>>>handleSaveException(SaveContactException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(getResponse(ex.getMessage()), new HttpHeaders(), HttpStatus.CREATED);

    }
    @ExceptionHandler(DeleteContactException.class)
    public ResponseEntity<Map<String, List<String>>>handleDeleteException(SaveContactException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(getResponse(ex.getMessage()), new HttpHeaders(), HttpStatus.OK);
//        return ResponseEntity.status(HttpStatus.OK).body(getResponse(ex.getMessage()));


    }
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Map<String, List<String>>> handleRuntimeExceptions(RuntimeException ex) {
        logger.error("Runtime exception: " + ex.getMessage());
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private Map<String, List<String>> getResponse(String message) {
        List<String> messages = Arrays.asList(new String[]{message});
        Map<String, List<String>> msgResponse = new HashMap<>();
        msgResponse.put("message", messages);
        return msgResponse;
    }
    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

}