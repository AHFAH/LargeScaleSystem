package spring.board.articleread.service.event.handler;

import spring.board.articleread.repository.ArticleIdListRepository;
import spring.board.articleread.repository.ArticleQueryModel;
import spring.board.articleread.repository.ArticleQueryModelRepository;
import spring.board.articleread.repository.BoardArticleCountRepository;
import spring.board.common.event.Event;
import spring.board.common.event.EventType;
import spring.board.common.event.payload.ArticleCreatedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {
    private final ArticleIdListRepository articleIdListRepository;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleCreatedEventPayload> event) {
        ArticleCreatedEventPayload payload = event.getPayload();
        articleQueryModelRepository.create(
                ArticleQueryModel.create(payload),
                Duration.ofDays(1)
        );
        articleIdListRepository.add(payload.getBoardId(), payload.getArticleId(), 1000L);
        // QueryModel과 IdList의 순서를 지켜야함. IdList에 추가 후 QueryModel이 생성되기 직전에 조회가 들어오면
        // 목록에는 조회되지만 QueryModel에는 없어 불필요하게 원본 데이터에 접근해야 할 수도 있음
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }

    @Override
    public boolean supports(Event<ArticleCreatedEventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType();
    }
}
