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
public class ArticleViewTest {

    @Id
    private Long articleId;
    private Long viewCount;

    public static ArticleViewTest init(Long articleId, Long viewCount) {
        ArticleViewTest articleVIewCount = new ArticleViewTest();
        articleVIewCount.articleId = articleId;
        articleVIewCount.viewCount = viewCount;
        return articleVIewCount;
    }
}
