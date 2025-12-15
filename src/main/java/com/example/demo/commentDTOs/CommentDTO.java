package com.example.demo.commentDTOs;

import lombok.*;

import java.time.LocalDateTime;
@NoArgsConstructor
@Data
public class CommentDTO {
    private Long id;
    private String body;


    private Long articleId;
    private String articleTitle;



    private String username;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
