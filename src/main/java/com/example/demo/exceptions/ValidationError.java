package com.example.demo.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {

        private String field;
        private String message;
        private Object rejectedValue;

    public static ValidationError fromFieldError(FieldError fieldError) {
        return ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }
    public static ValidationError fromObjectError(ObjectError objectError) {
        return ValidationError.builder()
                .field(null)
                .message(objectError.getDefaultMessage())
                .rejectedValue(null)
                .build();
    }
}

