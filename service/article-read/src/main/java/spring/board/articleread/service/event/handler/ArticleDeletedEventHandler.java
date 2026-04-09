package spring.board.articleread.service.event.handler;


import spring.board.articleread.repository.ArticleIdListRepository;
import spring.board.articleread.repository.ArticleQueryModelRepository;
import spring.board.articleread.repository.BoardArticleCountRepository;
import spring.board.common.event.Event;
import spring.board.common.event.EventType;
import spring.board.common.event.payload.ArticleDeletedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final ArticleIdListRepository articleIdListRepository;

    @Override
    public void handle(Event<ArticleDeletedEventPayload> event) {
        ArticleDeletedEventPayload payload = event.getPayload();
        articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
        articleQueryModelRepository.delete(payload.getArticleId());
        // QueryModel이 먼저 삭제되면 IdList에는 조회되지만 원본 데이터는 조회할 수 없는 문제가 발생할 수도 있음.
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }

    @Override
    public boolean supports(Event<ArticleDeletedEventPayload> event) {
        return EventType.ARTICLE_DELETED == event.getType();
    }
}
