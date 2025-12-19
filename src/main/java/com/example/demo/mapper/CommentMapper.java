package com.example.demo.mapper;

import com.example.demo.dtos.commentDTOs.CommentDTO;
import com.example.demo.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component

public class CommentMapper {

    public CommentDTO toDTO(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setBody(comment.getBody());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());


        if (comment.getArticle() != null) {
            dto.setArticleId(comment.getArticle().getId());
            dto.setArticleTitle(comment.getArticle().getTitle());
        }


            dto.setUsername(comment.getAuthor().getUsername());


        return dto;
}

    public Page<CommentDTO> toPageDTO(Page<Comment> comments) {
        return comments.map(this::toDTO);
    }



}
