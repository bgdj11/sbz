package sbnz.integracija.example.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotWritableException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<?> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {
        String message = "Error serializing response";
        
        // Check if it's a Jackson serialization issue
        if (ex.getCause() instanceof InvalidDefinitionException) {
            InvalidDefinitionException jacksonEx = (InvalidDefinitionException) ex.getCause();
            message = "Serialization error: " + jacksonEx.getMessage();
        }
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("details", "Please check for circular references in entity relationships");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An unexpected error occurred");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}