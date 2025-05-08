package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleCreator {
    private String username;
    private String avatarUrl;
    private boolean following; // пока если не реализовано
}
