package spring.board.view.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article_view_count")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleViewCount {

    @Id
    private Long articleId;
    private Long viewCount;

    public static ArticleViewCount init(Long articleId, Long viewCount) {
        ArticleViewCount articleVIewCount = new ArticleViewCount();
        articleVIewCount.articleId = articleId;
        articleVIewCount.viewCount = viewCount;
        return articleVIewCount;
    }
}
