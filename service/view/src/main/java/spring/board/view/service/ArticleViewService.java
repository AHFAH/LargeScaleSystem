package spring.board.view.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.board.view.repository.ArticleViewCountRepository;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ArticleViewCountRepository articleViewCountRepository;

    public Long increase(Long articleId, Long userId) {
        return articleViewCountRepository.increase(articleId);
    }

    public Long count(Long articleId) {
        return articleViewCountRepository.read(articleId);
    }
}
