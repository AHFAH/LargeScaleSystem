package spring.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import spring.board.comment.service.request.CommentCreateRequestV2;
import spring.board.comment.service.response.CommentPageResponse;
import spring.board.comment.service.response.CommentResponse;

import java.util.List;

public class CommentApiV2Test {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
        CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

        System.out.println("response1.getPath() = " + response1.getPath());
        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());


        /**
         response1.getPath() = 00000
         response1.getCommentId() = 199172634903945216
         response2.getPath() = 0000000000
         response2.getCommentId() = 199172635910578176
         response3.getPath() = 000000000000000
         response3.getCommentId() = 199172635998658560
         */
    }

    CommentResponse create(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v2/comments/{commentId}", 199172634903945216L)
                .retrieve()
                .body(CommentResponse.class);
        System.out.println("response = " + response);

        /**
         response = CommentResponse(commentId=199172634903945216,
         content=my comment1, parentCommentId=null, articleId=1,
         writerId=1, deleted=false, path=00000, createdAt=2025-07-03T23:40:57)
         */
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v2/comments/{commentId}", 199172634903945216L)
                .retrieve();

        /**
         * response = CommentResponse(commentId=199172634903945216,
         * content=my comment1, parentCommentId=null, articleId=1,
         * writerId=1, deleted=true, path=00000, createdAt=2025-07-03T23:40:57)*/
    }


    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v2/comments?articleId=1&pageSize=10&page=50000")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        /**
         * comment.getCommentId() = 199174423274656165
         * comment.getCommentId() = 199174423274656166
         * comment.getCommentId() = 199174423274656167
         * comment.getCommentId() = 199174423274656168
         * comment.getCommentId() = 199174423274656169
         * comment.getCommentId() = 199174423274656170
         * comment.getCommentId() = 199174423274656171
         * comment.getCommentId() = 199174423274656172
         * comment.getCommentId() = 199174423274656173
         * comment.getCommentId() = 199174423274656174
         * */

    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse response : responses1) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        String lastPath = responses1.getLast().getPath();
        List<CommentResponse> responses2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse response : responses2) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }


        /**
         * firstPage
         * response.getCommentId() = 199172634903945216
         * response.getCommentId() = 199172635910578176
         * response.getCommentId() = 199172635998658560
         * response.getCommentId() = 199174272300683266
         * response.getCommentId() = 199174272388763648
         * secondPage
         * response.getCommentId() = 199174272388763657
         * response.getCommentId() = 199174272388763666
         * response.getCommentId() = 199174272388763673
         * response.getCommentId() = 199174272388763694
         * response.getCommentId() = 199174272392957955
         * */
    }


    @Test
    void countTest() {
        CommentResponse commentResponse = create(new CommentCreateRequestV2(2L, "my comment1", null, 1L));

        Long count1 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1); // 1

        restClient.delete()
                .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
                .retrieve();

        Long count2 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2); // 0
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }
}