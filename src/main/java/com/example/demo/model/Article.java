package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.*;

@Data
@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String body;


    @ElementCollection
    @CollectionTable(
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id")
    )
    @Column(name = "tags")
    private Set<String> tagList = new HashSet<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private MyUser author;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "article_likes",
            joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<MyUser> likedByUsers = new ArrayList<>();


    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Comment> comments = new ArrayList<>();


    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setArticle(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setArticle(null);
    }

    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;
}
