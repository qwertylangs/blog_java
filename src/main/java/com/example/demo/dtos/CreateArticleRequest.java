package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class CreateArticleRequest {
    private String title;
    private String description;
    private String body;
    private String[] tagList;
}
