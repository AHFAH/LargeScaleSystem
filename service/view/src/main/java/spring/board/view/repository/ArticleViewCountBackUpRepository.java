package spring.board.view.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.board.view.entity.ArticleViewTest;

@Repository
public interface ArticleViewCountBackUpRepository extends JpaRepository<ArticleViewTest, Long> {

    @Query(
            value = "update article_view_count set view_count = :viewCount " +
                    "where article_id = :articleId and view_count < :viewCount",
            nativeQuery = true
    )
    @Modifying
    int updateViewCount(
            @org.springframework.data.repository.query.Param("articleId") Long articleId,
            @Param("viewCount") Long viewCount
    );
}
