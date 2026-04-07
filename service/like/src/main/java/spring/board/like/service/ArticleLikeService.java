package spring.board.like.service;


import spring.board.common.event.EventType;
import spring.board.common.event.payload.ArticleLikedEventPayload;
import spring.board.common.event.payload.ArticleUnlikedEventPayload;
import spring.board.common.outboxmessagerelay.OutboxEventPublisher;
import spring.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.board.like.entity.ArticleLike;
import spring.board.like.entity.ArticleLikeCount;
import spring.board.like.repository.ArticleLikeCountRepository;
import spring.board.like.repository.ArticleLikeRepository;
import spring.board.like.service.response.ArticleLikeResponse;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow();
    }



    /**
    *   update 구문
    */
    @Transactional
    public void likePessimisticLock1(Long articleId, Long userId) {
        ArticleLike articleLike = articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        int result = articleLikeCountRepository.increases(articleId);
        if (result == 0) {
            articleLikeCountRepository.save(
                    ArticleLikeCount.init(articleId, 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_LIKED,
                ArticleLikedEventPayload.builder()
                        .articleLikeId(articleLike.getArticleLikeId())
                        .articleId(articleLike.getArticleId())
                        .userId(articleLike.getUserId())
                        .createdAt(articleLike.getCreatedAt())
                        .articleLikeCount(count(articleLike.getArticleId()))
                        .build(),
                articleLike.getArticleId()
        );
    }

    @Transactional
    public void unlikePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                        articleLikeRepository.delete(articleLike);
                        articleLikeCountRepository.decrease(articleId);
                        outboxEventPublisher.publish(
                                EventType.ARTICLE_UNLIKED,
                                ArticleUnlikedEventPayload.builder()
                                        .articleLikeId(articleLike.getArticleLikeId())
                                        .articleId(articleLike.getArticleId())
                                        .userId(articleLike.getUserId())
                                        .createdAt(articleLike.getCreatedAt())
                                        .articleLikeCount(count(articleLike.getArticleId()))
                                        .build(),
                                articleLike.getArticleId()
                        );
                });
    }

    /**
     * select ... for update + update
     * */
    @Transactional
    public void likePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }



    @Transactional
    public void unlikePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });

    }


    /**
     *  낙관적 락
     * */
    
    @Transactional
    public void likeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlikeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }

    public Long count(Long articleId) {
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);
    }
}
