package spring.board.articleread.api;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import spring.board.articleread.service.response.ArticleReadResponse;

public class ArticleReadApiTest {
    RestClient articleReadRestClient = RestClient.create("http://localhost:9005");
    RestClient articleRestClient = RestClient.create("http://localhost:9000");

    @Test
    void readTest() {
        ArticleReadResponse response = articleReadRestClient.get()
                .uri("/v1/articles/{articleId}", 191557552153919495L)
                .retrieve()
                .body(ArticleReadResponse.class);

        System.out.println("response = " + response);
    }
}
