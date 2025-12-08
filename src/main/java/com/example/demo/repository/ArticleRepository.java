package com.example.demo.repository;

import com.example.demo.model.Article;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface ArticleRepository extends JpaRepository<Article, Long>{
    @Query("SELECT a FROM Article a JOIN a.tagList t WHERE t = :tag")
    Page<Article> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.tagList t WHERE t IN :tags")
    Page<Article> findByTagsList(@Param("tags") Set<String> tags, Pageable pageable);

}
