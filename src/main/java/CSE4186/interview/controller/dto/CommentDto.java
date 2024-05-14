package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class CommentDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "commentCreateRequest", description = "댓글 생성 DTO")
    public static class createRequest {
        @NotNull private String content;
        @NotNull private Long userId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "commentUpdateRequest", description = "댓글 수정 DTO")
    public static class updateRequest {
        @NotNull
        private Long id;
        @NotNull
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "commentDeleteRequest", description = "댓글 삭제 DTO")
    public static class deleteRequest {
        private Long id;
    }

    @Getter
    @Schema(name = "commentResponse", description = "댓글 응답 DTO")
    public static class Response {
        private final Long id;
        private final ReviewDto content;
        private final String username;
        private final String createdAt;
        private final String updatedAt;
        private final Long userId;
        private final Long postId;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.content = new ReviewDto(comment.getContent());
            this.createdAt = String.valueOf(comment.getCreatedAt());
            this.updatedAt = String.valueOf(comment.getUpdatedAt());
            this.username = comment.getUser().getName();
            this.userId = comment.getUser().getId();
            this.postId = comment.getPost().getId();
        }
    }

    @Getter
    @Schema(name = "commentUpdateResponse", description = "댓글 수정 DTO")
    public static class updateResponse {
        private final Long id;

        public updateResponse(Long id) {
            this.id = id;
        }
    }

}
