package com.example.demo.controller;


import com.example.demo.dtos.CommentDTO;
import com.example.demo.dtos.CreateCommentRequest;
import com.example.demo.dtos.auth.UpdateCommentRequest;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/articles/{article_id}/comments")
@RequiredArgsConstructor
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(
            @PathVariable long article_id,
            @Valid @RequestBody CreateCommentRequest request
            )
    {

         CommentDTO commentDTO = commentService.createComment(article_id,request);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentDTO);
    }


    @GetMapping
    public ResponseEntity<Page<CommentDTO>> getCommentsById(
            @PathVariable long article_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection
    )
    {
        Sort sort;
        if (sortDirection == Sort.Direction.ASC) {
            sort = Sort.by("createdAt").ascending().and(Sort.by("id").ascending());
        } else {
            sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommentDTO> commentsPage =  commentService.getCommentsByArticleId(article_id, pageable);

return ResponseEntity.ok(commentsPage)  ;
    }
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable long article_id,
            @PathVariable long commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {

        CommentDTO commentUp = commentService.updateComment(
                commentId,
                request,
                article_id
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(commentUp);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable long article_id,
            @PathVariable long commentId
    ) {

 commentService.deleteComment(
        article_id,
        commentId
);
        return ResponseEntity.noContent().build();
}


}
