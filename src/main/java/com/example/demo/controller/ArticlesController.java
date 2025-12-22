package com.example.demo.controller;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.ArticlesResponse;
import com.example.demo.dtos.CreateArticleRequest;
import com.example.demo.dtos.commentDTOs.UpdateArticleRequest;
import com.example.demo.dtos.auth.UserDto;
import com.example.demo.service.ArticleService;
import com.example.demo.service.Auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticlesController {
    private final UserService userService;
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<ArticlesResponse> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection
    )
    {

        UserDto currentUser = userService.getCurrentUser();

        Sort sort;
        if (sortDirection == Sort.Direction.ASC) {
            sort = Sort.by("createdAt").ascending().and(Sort.by("id").ascending());
        } else {
            sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
        }

        PageRequest pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok().body(articleService.getAllArticles(pageable, currentUser != null ? currentUser.getId() : null, tags));
    }

    @GetMapping("/anonym")
    public ResponseEntity<ArticlesResponse> getAnonymArticle(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag
    ) {
        Sort sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
        PageRequest pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok().body(articleService.getAllArticles(pageable, null, tag));
    }

    @GetMapping("/anonym/{id}")
    public ResponseEntity<ArticleDTO> getAnonymArticleById(
            @PathVariable() Long id
    ) {
        var response = articleService.getArticleById(id, null);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticle(
            @PathVariable() Long id
    ) {
        UserDto currentUser = userService.getCurrentUser();

        var response = articleService.getArticleById(id, currentUser != null ? currentUser.getId() : null);
        return ResponseEntity.ok(response);
    }




    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> editArticle(
            @PathVariable() Long id,
            @RequestBody UpdateArticleRequest request
    ) {
        UserDto currentUser = userService.getCurrentUser();
        return ResponseEntity.ok().body(articleService.editArticleById(id, request, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable() Long id
    ) {
        UserDto currentUser = userService.getCurrentUser();
        articleService.deleteArticleById(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }


    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(
            @RequestBody CreateArticleRequest createArticleRequest
    )
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.createArticle(createArticleRequest));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<ArticleDTO> favoriteArticle(@PathVariable Long id) {
        UserDto currentUser = userService.getCurrentUser();

        ArticleDTO response = articleService.favoriteArticle(id, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<ArticleDTO> unfavoriteArticle(@PathVariable Long id) {
        UserDto currentUser = userService.getCurrentUser();

        ArticleDTO response = articleService.unfavoriteArticle(id, currentUser.getId());
        return ResponseEntity.ok(response);
    }

}
