package spring.board.comment.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spring.board.comment.service.CommentServiceV2;
import spring.board.comment.service.request.CommentCreateRequestV2;
import spring.board.comment.service.response.CommentResponse;

@RestController
@RequiredArgsConstructor
public class CommentControllerV2 {
    private final CommentServiceV2 commentService;

    @GetMapping("/v2/comments/{commentId}")
    public CommentResponse read(
            @PathVariable("commentId") Long commentId
    ) {
        return commentService.read(commentId);
    }

    @PostMapping("/v2/comments")
    public CommentResponse create(@RequestBody CommentCreateRequestV2 request) {
        return commentService.create(request);
    }

    @DeleteMapping("/v2/comments/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }
}
