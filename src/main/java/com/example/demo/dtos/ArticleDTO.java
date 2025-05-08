package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class ArticleDTO {
    private Long id;
    private String title;
    private String description;
    private String body;
    private Set<String> tagList;
    private String createdAt;
    private String updatedAt;
    private boolean favorite;
    private int favoritesCount;
    private ArticleCreator author;
}

