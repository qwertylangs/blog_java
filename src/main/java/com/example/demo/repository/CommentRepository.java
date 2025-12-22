package com.example.demo.repository;

import com.example.demo.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    Page<Comment> findByArticleId(Long articleId, Pageable pageable);

    long countByArticleId(Long articleId);
}
