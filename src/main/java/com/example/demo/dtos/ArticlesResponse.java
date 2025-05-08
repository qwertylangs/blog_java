package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ArticlesResponse {
    private List<ArticleDTO> articles;
    private long articlesCount;
}
