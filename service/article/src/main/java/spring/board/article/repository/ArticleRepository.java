package spring.board.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.board.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
