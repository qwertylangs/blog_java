package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.User;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class ArticleDTO {
    private Long id;
    private  String title;
    private String description;
    private String body;
    private Set<String> tagList;
    private String createdAt;
    private String updatedAt;
    private boolean favorite;
    private int favoritesCount;
    private List<String> likedByUsers;
    private ArticleCreator author;

}

