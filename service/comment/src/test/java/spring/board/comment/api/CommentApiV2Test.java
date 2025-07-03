package spring.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import spring.board.comment.service.request.CommentCreateRequestV2;
import spring.board.comment.service.response.CommentResponse;

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

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }
}