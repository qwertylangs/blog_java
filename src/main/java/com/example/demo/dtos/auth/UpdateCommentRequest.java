package com.example.demo.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCommentRequest {
    @NotBlank(message = "Cummeant is responsible")
    @Size(min = 1, max = 1000)
    private String body;
}
