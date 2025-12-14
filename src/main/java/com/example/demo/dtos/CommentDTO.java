package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Data
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String body;


    private Long articleId;
    private String articleTitle;



    private String username;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public CommentDTO() {

    }
}
