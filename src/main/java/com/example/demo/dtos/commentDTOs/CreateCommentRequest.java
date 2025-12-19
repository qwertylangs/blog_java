package com.example.demo.dtos.commentDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class CreateCommentRequest {

    @NotBlank(message="Comment is required")
    @Size(min = 1,max = 1000,message = "Comment must be between 1 to 1000 characters")
        private String body;


}
