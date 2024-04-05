package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class CommentDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "commentCreateRequest", description = "댓글 생성 DTO")
    public static class createRequest {
        private String content;
        private Long userId;
    }

    @Getter
    @Schema(name = "commentResponse", description = "댓글 응답 DTO")
    public static class Response {
        private final Long id;
        private final String content;
        private final String username;
        private final String createdAt;
        private final String updatedAt;
        private final Long userId;
        private final Long postId;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.createdAt = String.valueOf(comment.getCreatedAt());
            this.updatedAt = String.valueOf(comment.getUpdatedAt());
            this.username = comment.getUser().getName();
            this.userId = comment.getUser().getId();
            this.postId = comment.getPost().getId();
        }
    }
}
