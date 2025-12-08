package com.example.demo.service;

import com.example.demo.dtos.*;
import com.example.demo.model.Article;
import com.example.demo.model.MyUser;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.TestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Auth.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
@RequiredArgsConstructor
public class ArticleService {
    @Autowired
    private final ArticleRepository articleRepository;
    private final TestRepository testRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    public ArticlesResponse getAllArticles (PageRequest pageRequest, Long currentUserId) {
        Page<Article> articlePage = articleRepository.findAll(pageRequest);


        List<ArticleDTO> articles = articlePage.getContent().stream().map(article -> convertToArticleDTO(article, currentUserId)).toList();
        return new ArticlesResponse(articles, articlePage.getTotalElements());
    }

    public ArticleDTO getArticleById (Long articleId, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article not found"));

        return convertToArticleDTO(article, currentUserId);
    }

    public ArticleDTO editArticleById(Long articleId, UpdateArticleRequest request, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Article not found"));

        if (!article.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only edit your own articles");
        }

        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
        }
        if (request.getBody() != null) {
            article.setBody(request.getBody());
        }
        if (request.getTagList() != null) {
            article.setTagList(new HashSet<>(request.getTagList()));
        }

        return convertToArticleDTO(articleRepository.save(article), currentUserId);
    };

    public void deleteArticleById(Long articleId, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article not found"));

        if (!article.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only delete your own articles");
        }

        articleRepository.delete(article);
    };
@Contract(pure = true)
    private static ArticleDTO convertToArticleDTO(Article article, Long currentUserId) {
        return new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getDescription(),
                article.getBody(),
                article.getTagList(),
                article.getCreatedAt().toString(),
                article.getUpdatedAt().toString(),
                isFavoritedByUser(article, currentUserId),
                article.getLikedByUsers().size(),
                article.getLikedByUsers().stream()
                        .map(MyUser::getUsername)
                        .collect(Collectors.toList()),
                new ArticleCreator(
                        article.getAuthor().getUsername(),
                        article.getAuthor().getAvatarUrl(),
                        false // following
                )
        );
    }

    private static boolean isFavoritedByUser(Article article, Long userId) {
        if (userId == null) return false;
        return article.getLikedByUsers().stream()
                .anyMatch(user ->  user.getId().equals(userId));
    }

    public ArticleDTO createArticle( CreateArticleRequest createArticleRequest) {
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<MyUser> userOptional = userRepository.findByUsername(username);
        MyUser user = userOptional.get();

        Set<String> tags = new HashSet<>(Arrays.asList(createArticleRequest.getTagList()));

        Article article = new Article();
        article.setTitle(createArticleRequest.getTitle());
        article.setDescription(createArticleRequest.getDescription());
        article.setBody(createArticleRequest.getBody());
        article.setTagList(new HashSet<>(tags));
        article.setAuthor(user);

        Article savedArticle = articleRepository.save(article);
        return convertToArticleDTO(savedArticle, user.getId());
    }


    @Transactional
    public ArticleDTO favoriteArticle(Long id, Long userId) {
        MyUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        var liked = article.getLikedByUsers();
        boolean alreadyFavorited = liked.stream()
                .anyMatch(u -> u.getId().equals(userId));


        if (alreadyFavorited) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article already favorited");
        }

        article.getLikedByUsers().add(user);
        articleRepository.save(article);

        return convertToArticleDTO(article, userId);
    }


    @Transactional
    public ArticleDTO unfavoriteArticle(Long id, Long userId) {
        MyUser currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        var isLiked = article.getLikedByUsers().stream()
                .anyMatch(user -> {
                    LOGGER.info(user.getId().toString() + " " + "isLiked");
                    return user.getId().equals(userId);
                });

        if (!isLiked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article not favorited yet");
        }

        article.getLikedByUsers().remove(currentUser);
        articleRepository.save(article);

        return convertToArticleDTO(article, userId);
    }
    @Transactional
    public static List<ArticleDTO> findArticlesByTags(List<ArticleDTO> tags) {
        List<Article> articlesPage;

        if (tags == null || tags.isEmpty()) {

            articlesPage = ArticleRepository

        }
        else {

            articlesPage = articleRepository.findByAnyTagNative(tags);
        }


        return articlesPage.stream()
                .map((Article t) ->ArticleDTO.getTitle())
                .collect(Collectors.toList());
    }
}
