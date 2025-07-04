package spring.board.like.service.response;

import spring.board.like.entity.ArticleLike;

import java.time.LocalDateTime;

public class ArticleLikeResponse {
    private Long articleLikeId;
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;

    public static ArticleLikeResponse from(ArticleLike articleLike) {
        ArticleLikeResponse response = new ArticleLikeResponse();
        response.articleLikeId = articleLike.getArticleLikeId();
        response.articleId = articleLike.getArticleId();
        response.userId = articleLike.getUserId();
        response.createdAt = articleLike.getCreatedAt();
        return response;
    }
}
