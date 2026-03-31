package spring.board.view.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.board.view.repository.ArticleViewCountRepository;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewCountBackUpProcessor articleViewCountBackUpProcessor;
    private final ArticleViewCountRepository articleViewCountRepository;

    private static final int BACK_UP_BACH_SIZE = 100;

    public Long increase(Long articleId, Long userId) {
        Long count = articleViewCountRepository.increase(articleId);
        if (count % BACK_UP_BACH_SIZE == 0) {
            articleViewCountBackUpProcessor.backUp(articleId, count);
        }
        return articleViewCountRepository.increase(articleId);
    }

    public Long count(Long articleId) {
        return articleViewCountRepository.read(articleId);
    }
}
