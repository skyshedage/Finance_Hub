package com.examly.springapp.controller;
import com.examly.springapp.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<ApiResponse<String>> invalid(IllegalArgumentException e){return ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));}
 @ExceptionHandler(EntityNotFoundException.class)
 public ResponseEntity<ApiResponse<String>> missing(EntityNotFoundException e){return ResponseEntity.status(404).body(new ApiResponse<>(false,e.getMessage(),null));}
 @ExceptionHandler(IllegalStateException.class)
 public ResponseEntity<ApiResponse<String>> conflict(IllegalStateException e){return ResponseEntity.status(409).body(new ApiResponse<>(false,e.getMessage(),null));}
}
