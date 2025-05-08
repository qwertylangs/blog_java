package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

// сздает геттеры сеттеры и конструктор для final полей
@Data
// помечает как данные в бд
@Entity
@Table(name = "users")
//@DynamicUpdate  // Обновляет только изменённые поля
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String avatarUrl;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Article> articles = new ArrayList<>();

    @ManyToMany(mappedBy = "likedByUsers", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Article> likedArticles = new ArrayList<>();
}
