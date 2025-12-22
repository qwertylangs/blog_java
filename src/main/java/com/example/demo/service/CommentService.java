package com.example.demo.service;

import com.example.demo.dtos.commentDTOs.CommentDTO;
import com.example.demo.dtos.commentDTOs.CreateCommentRequest;
import com.example.demo.dtos.auth.UpdateCommentRequest;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.Article;
import com.example.demo.model.Comment;
import com.example.demo.model.MyUser;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;




@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDTO createComment(Long article_id, CreateCommentRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();



        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }

        String username = authentication.getName();

        MyUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    ));


        Article article = articleRepository.findById(article_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article not found"));


        Comment comment = new Comment();
        comment.setBody(request.getBody());
        comment.setArticle(article);
        comment.setAuthor(user);


        Comment savedComment = commentRepository.save(comment);


        return commentMapper.toDTO(savedComment);
    }

    public Page<CommentDTO> getCommentsByArticleId(Long article_id, Pageable pageable) {

        if (!articleRepository.existsById(article_id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Article not found with id: " + article_id);
        }
Page comments =commentRepository.findByArticleId(article_id,pageable);
        return commentMapper.toPageDTO(comments);
    }

    @Transactional
    public CommentDTO updateComment(Long commentId, UpdateCommentRequest request, Long article_id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();



        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }


        String username = authentication.getName();


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cummeant is not found with id: " + commentId));



        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Article article = articleRepository.findById(article_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article not found"));

        if (!comment.getAuthor().getId().equals(user.getId())){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to modify this comment"
            );
        }

        comment.setBody(request.getBody());
        commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    @Transactional
    public void deleteComment(Long articleId, Long commentId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }


        String username = authentication.getName();


        if (!articleRepository.existsById(articleId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Article is not found with id: " + articleId);
        }


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cummeant is not found"));

        if (!comment.getArticle().getId().equals(articleId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cummeant is not belongs with this article,FUCKIN BUSTARD");
        }
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (!comment.getAuthor().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to delete this cumment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public Long countByArticleId(Long articleId) {


        if (!articleRepository.existsById(articleId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Article not found with id: " + articleId);
        }
        Long count = commentRepository.countByArticleId(articleId);
        return count;
    }
}
