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
@Repository
@Transactional
public interface ArticleRepository extends JpaRepository<Article, Long>{

    @Query(
            value = "SELECT DISTINCT a.* FROM articles a " +
                    "INNER JOIN article_tags at ON a.id = at.article_id " +
                    "WHERE at.tags IN :tag " +
                    "ORDER BY a.created_at DESC",
            countQuery = "SELECT COUNT(DISTINCT a.id) FROM articles a " +
                    "INNER JOIN article_tags at ON a.id = at.article_id " +
                    "WHERE at.tags IN :tags",
            nativeQuery = true
    )
 List<Article> findByAnyTagNative(@Param("tag") List<String> tag);



    @Query(
            value = "SELECT DISTINCT a.* FROM articles a " +
                    "INNER JOIN article_tags at ON a.id = at.article_id " +
                    "WHERE at.tags = :tag" +
                    "ORDER BY a.created_at DESC",
            countQuery = "SELECT COUNT(DISTINCT a.id) FROM articles a " +
                    "INNER JOIN article_tags at ON a.id = at.article_id " +
                    "WHERE at.tags IN :tag",
            nativeQuery = true
    )
    List<Article> findByTagNative(@Param("tag") List<String> tag);
}
